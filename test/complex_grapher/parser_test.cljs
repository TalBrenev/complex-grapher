(ns complex-grapher.parser-test
  (:require [cljs.test :refer [deftest testing is]]
            [complex-grapher.parser :refer [parse transform-ast]]))

(deftest parser
  (testing "parse"
    (testing "a number literal"
      (is (= (parse "5.2")
             "5.2")))

    (testing "a variable"
      (is (= (parse "z")
             :z)))

    (testing "the imaginary constant"
      (is (= (parse "i")
             :i)))

    (testing "euler's number"
      (is (= (parse "e")
             :e)))

    (testing "pi"
      (is (= (parse "pi")
             :pi)))

    (testing "addition of two numbers"
      (is (= (parse "1 + 5.5")
             [:add "1" "5.5"])))

    (testing "subtraction of two numbers"
      (is (= (parse "2.5-e")
             [:sub "2.5" :e])))

    (testing "explicit multiplication of two numbers"
      (is (= (parse " pi * 3 ")
             [:mul :pi "3"])))

    (testing "implicit multiplication of two numbers"
      (is (= (parse "iz")
             [:mul :i :z])))

    (testing "division of two numbers"
      (is (= (parse "3/5")
             [:div "3" "5"])))

    (testing "exponentiation of two numbers"
      (is (= (parse "e^z")
             [:pow :e :z])))

    (testing "the natural logarithm of a number"
      (is (= (parse "log e")
             [:log :e]))
      (is (= (parse "lne")
             [:log :e])))

    (testing "the sine of a number"
      (is (= (parse "sin(pi)")
             [:sin :pi])))

    (testing "the cosine of a number"
      (is (= (parse "cosz")
             [:cos :z])))

    (testing "the tangent of a number"
      (is (= (parse "tan i")
             [:tan :i])))

    (testing "the negation of a number"
      (is (= (parse "-i")
             [:negate :i])))

    (testing "the double negation of a number"
      (is (= (parse "--z")
             [:negate [:negate :z]])))

    (testing "the triple negation of a number"
      (is (= (parse "---i")
             [:negate [:negate [:negate :i]]])))

    (testing "the real part of a number"
      (is (= (parse "re(z)")
             [:re :z])))

    (testing "the imaginary part of a number"
      (is (= (parse "im(i)")
             [:im :i])))

    (testing "the argument of a number"
      (is (= (parse "arge")
             [:arg :e])))

    (testing "the magnitude of a number"
      (is (= (parse "mag z")
             [:mag :z])))

    (testing "an expression with no brackets"
      (is (= (parse "1+5^e-89i+pi/2^z")
             [:add [:sub [:add "1" [:pow "5" :e]]
                         [:mul "89" :i]]
                   [:div :pi
                         [:pow "2" :z]]])))

    (testing "an expression with brackets"
      (is (= (parse "(z-1)^5 + 233(pi/4)*(1.5^z)")
             [:add [:pow [:sub :z "1"] "5"]
                   [:mul [:mul "233" [:div :pi "4"]]
                         [:pow "1.5" :z]]])))

    (testing "exponents of exponents"
      (is (= (parse "z^i^e^pi^1^2^3")
             [:pow :z [:pow :i [:pow :e [:pow :pi [:pow "1" [:pow "2" "3"]]]]]])))

    (testing "exponents of exponents with brackets"
      (is (= (parse "((((((z^i)^e)^pi)^1)^2)^3)")
             [:pow [:pow [:pow [:pow [:pow [:pow :z :i]
                                      :e]
                                :pi]
                          "1"]
                    "2"]
              "3"])))

    (testing "an expression with lots of functions and variables"
      (is (= (parse "isini(ipii)z+cos(log(tan(e^z)))")
             [:add [:mul [:mul [:mul :i [:sin :i]]
                          [:mul [:mul :i :pi] :i]]
                         :z]
                   [:cos [:log [:tan [:pow :e :z]]]]]))))

  (testing "transform-ast"
    (let [token-map {:re     "r"
                     :im     "i"
                     :arg    "a"
                     :mag    "m"
                     :sin    "s"
                     :cos    "c"
                     :tan    "t"
                     :log    "l"
                     :negate "n"
                     :z      "z"
                     :e      "e"
                     :pi     "p"
                     :i      "i"
                     :add    "a"
                     :sub    "s"
                     :mul    "m"
                     :div    "d"
                     :pow    "p"}]

      (testing "transforms a token"
        (is (= (transform-ast (parse "pi") token-map identity)
               "p")))

      (testing "transforms an ast with tokens"
        (is (= (transform-ast (parse "z*pi") token-map identity)
               ["m" "z" "p"])))

      (testing "transforms a number"
        (is (= (transform-ast (parse "7.5") token-map js/parseFloat)
               7.5)))

      (testing "transforms an expression with tokens and numbers"
        (is (= (transform-ast (parse "7.5+e") token-map #(inc (js/parseFloat %)))
               ["a" 8.5 "e"]))))))
