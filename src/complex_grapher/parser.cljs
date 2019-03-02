(ns complex-grapher.parser
  (:require [complex-grapher.complex-arithmetic :refer [i re im arg mag
                                                        add sub mul div
                                                        pow log
                                                        sin cos tan]]
            [clojure.string :as s]))

(def tokens [{:token "("     :type :bracket}
             {:token ")"     :type :bracket}
             {:token "z"     :type :variable}
             {:token "log"   :type :function   :value log}
             {:token "sin"   :type :function   :value sin}
             {:token "cos"   :type :function   :value cos}
             {:token "tan"   :type :function   :value tan}
             {:token "e"     :type :constant   :value Math/E}
             {:token "pi"    :type :constant   :value Math/PI}
             {:token "i"     :type :constant   :value i}
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
     :type  :constant
     :value (js/parseFloat number)}))

(defn first-token [expression]
  (or
    (tokenize-starting-number expression)
    (first (filter #(s/starts-with? expression (:token %)) tokens))))

(defn tokenize [expression]
  (let [expression (strip-starting-whitespace expression)]
    (if-not (empty? expression)
      (let [token (first-token expression)]
        (lazy-seq (cons token (tokenize (subs expression (count (:token token))))))))))

(defn parse [x] [])
