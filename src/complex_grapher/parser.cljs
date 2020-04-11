(ns complex-grapher.parser
  (:require [complex-grapher.complex-arithmetic :refer [ComplexArithmetic
                                                        i re im arg mag
                                                        add sub mul div
                                                        negate pow log
                                                        sin cos tan]]
            [complex-grapher.utils :refer [find-first in?]]
            [clojure.string :as s]))

(def tokens [{:token "("     :type :left-bracket}
             {:token ")"     :type :right-bracket}
             {:token "re"    :type :function   :value re}
             {:token "im"    :type :function   :value im}
             {:token "arg"   :type :function   :value arg}
             {:token "mag"   :type :function   :value mag}
             {:token "sin"   :type :function   :value sin}
             {:token "cos"   :type :function   :value cos}
             {:token "tan"   :type :function   :value tan}
             {:token "log"   :type :function   :value log}
             {:token "ln"    :type :function   :value log}
             {:token "-"     :type :function   :value negate}
             {:token "z"     :type :number     :value "z"}
             {:token "e"     :type :number     :value Math/E}
             {:token "pi"    :type :number     :value Math/PI}
             {:token "i"     :type :number     :value i}
             {:token "+"     :type :operator   :value add   :precedence 1   :associativity :left}
             {:token "-"     :type :operator   :value sub   :precedence 1   :associativity :left}
             {:token "*"     :type :operator   :value mul   :precedence 2   :associativity :left}
             {:token "/"     :type :operator   :value div   :precedence 2   :associativity :left}
             {:token "^"     :type :operator   :value pow   :precedence 3   :associativity :right}])

(defn strip-starting-whitespace [expression]
  (s/replace expression #"^\s+" ""))

(defn tokenize-starting-number [expression]
  (if-let [number (re-find #"^\d*\.?\d+|^\d+\.?\d*" expression)]
    {:token number
     :type  :number
     :value (js/parseFloat number)}))

(defn first-token [previous-token expression]
  (if-not (empty? expression)
    (or (tokenize-starting-number expression)
        (let [found-tokens (filter #(s/starts-with? expression (:token %)) tokens)]
          (case (count found-tokens)
            0 (throw "Invalid Expression")
            1 (first found-tokens)
            2 (if (in? (:type previous-token) [nil :operator :left-bracket])
                (find-first #(= (:type %) :function) found-tokens)
                (find-first #(= (:type %) :operator) found-tokens)))))))

(def multiplication-token (find-first #(= (:token %) "*") tokens))
(defn insert-implicit-multiplication [previous-token token tokens]
  (if (and (in? (:type previous-token) [:number :right-bracket])
           (in? (:type token) [:number :left-bracket :function]))
    (lazy-seq (cons multiplication-token tokens))
    tokens))

(defn tokenize
  ([expression]
   (tokenize nil expression))
  ([previous-token expression]
   (let [expression (strip-starting-whitespace expression)]
     (if-let [token (first-token previous-token expression)]
       (->> (subs expression (count (:token token)))
            (tokenize token)
            (cons token)
            (insert-implicit-multiplication previous-token token)
            lazy-seq)))))

(defn apply-top-operator [ast-stack operator-stack]
  (let [operator (first operator-stack)]
    [(case (:type operator)
      :function (if (first ast-stack)
                  (cons (list operator (first ast-stack))
                        (rest ast-stack))
                  (throw "Invalid Expression"))
      :operator (if (second ast-stack)
                  (cons (list operator (second ast-stack) (first ast-stack))
                        (nthrest ast-stack 2))
                  (throw "Invalid Expression")))
     (rest operator-stack)]))

(defn apply-remaining-operators [[ast-stack operator-stack]]
  (if (empty? operator-stack)
    ast-stack
    (if (= (:type (first operator-stack)) :left-bracket)
      (throw "Invalid Expression")
      (recur (apply-top-operator ast-stack operator-stack)))))

(defn apply-right-bracket [[ast-stack operator-stack]]
  (if (empty? operator-stack)
    (throw "Invalid Expression")
    (if (= (:type (first operator-stack)) :left-bracket)
      [ast-stack (rest operator-stack)]
      (recur (apply-top-operator ast-stack operator-stack)))))

(defn add-operator-to-stack [[ast-stack operator-stack] operator]
  (let [token (first operator-stack)]
    (if (and (not (= (:type token) :left-bracket))
             (or (= (:type token) :function)
                 (and (= (:type token) :operator) (> (:precedence token) (:precedence operator)))
                 (and (= (:type token) :operator) (= (:precedence token) (:precedence operator)) (= (:associativity token) :left))))
      (recur (apply-top-operator ast-stack operator-stack) operator)
      [ast-stack (cons operator operator-stack)])))

(defn parse [expression]
  (if (empty? expression)
    (throw "Invalid Expression"))
  (->> expression
       (s/lower-case)
       (tokenize)
       (reduce (fn [[ast-stack operator-stack] token]
                 (case (:type token)
                   :number        [(cons token ast-stack) operator-stack]
                   :function      [ast-stack (cons token operator-stack)]
                   :left-bracket  [ast-stack (cons token operator-stack)]
                   :right-bracket (apply-right-bracket [ast-stack operator-stack])
                   :operator      (add-operator-to-stack [ast-stack operator-stack] token)))
               [(list) (list)])
       (apply-remaining-operators)
       (first)))

(defn evaluate
  ([ast]
   (evaluate ast {}))
  ([ast variables]
   (if (map? ast)
     (or (get variables (:value ast)) (:value ast))
     (apply (:value (first ast)) (map #(evaluate % variables) (rest ast))))))
