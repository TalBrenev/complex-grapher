(ns complex-grapher.parser
  (:require [complex-grapher.complex-arithmetic :refer [i re im arg mag
                                                        add sub mul div
                                                        pow log
                                                        sin cos tan]]
            [clojure.string :as s]))

(def tokens [{:token "("     :type :left-bracket}
             {:token ")"     :type :right-bracket}
             {:token "sin"   :type :function   :value sin}
             {:token "cos"   :type :function   :value cos}
             {:token "tan"   :type :function   :value tan}
             {:token "log"   :type :function   :value log}
             {:token "ln"    :type :function   :value log}
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

(defn first-token [expression]
  (or
    (tokenize-starting-number expression)
    (first (filter #(s/starts-with? expression (:token %)) tokens))))

(defn tokenize [expression]
  (let [expression (strip-starting-whitespace expression)]
    (if-let [token (first-token expression)]
      (lazy-seq (cons token (tokenize (subs expression (count (:token token)))))))))

(defn apply-top-operator [ast-stack operator-stack]
  (let [operator (first operator-stack)]
    [(case (:type operator)
      :function (cons (list (:value operator) (first ast-stack))
                      (rest ast-stack))
      :operator (cons (list (:value operator) (second ast-stack) (first ast-stack))
                      (nthrest ast-stack 2)))
     (rest operator-stack)]))

(defn apply-remaining-operators [[ast-stack operator-stack]]
  (if (empty? operator-stack)
    ast-stack
    (recur (apply-top-operator ast-stack operator-stack))))

(defn apply-right-bracket [[ast-stack operator-stack]]
  (if (= (:type (first operator-stack)) :left-bracket)
    [ast-stack (rest operator-stack)]
    (recur (apply-top-operator ast-stack operator-stack))))

(defn add-operator-to-stack [[ast-stack operator-stack] operator]
  (let [token (first operator-stack)]
    (if (and (not (= (:type token) :left-bracket))
             (or (= (:type token) :function)
                 (and (= (:type token) :operator) (> (:precedence token) (:precedence operator)))
                 (and (= (:type token) :operator) (= (:precedence token) (:precedence operator)) (= (:associativity token) :left))))
      (recur (apply-top-operator ast-stack operator-stack) operator)
      [ast-stack (cons operator operator-stack)])))

(defn parse [expression]
  (->> (tokenize expression)
       (reduce (fn [[ast-stack operator-stack] token]
                 (case (:type token)
                   :number        [(cons (:value token) ast-stack) operator-stack]
                   :function      [ast-stack (cons token operator-stack)]
                   :left-bracket  [ast-stack (cons token operator-stack)]
                   :right-bracket (apply-right-bracket [ast-stack operator-stack])
                   :operator      (add-operator-to-stack [ast-stack operator-stack] token)))
               [(list) (list)])
       (apply-remaining-operators)
       (first)))
