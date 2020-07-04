(ns complex-grapher.parser-test
  (:require [cljs.test :refer [deftest testing is]]
            [complex-grapher.test-utils :refer [value-ast]]
            [complex-grapher.complex-arithmetic :refer [i re im arg mag
                                                        add sub mul div
                                                        negate pow log
                                                        sin cos tan]]
            [complex-grapher.parser :refer [parse]]))

(deftest parser
  (testing "parse"
    (testing "a number literal"
      (is (= (value-ast (parse "5.2"))
             5.2)))

    (testing "a variable"
      (is (= (value-ast (parse "z"))
             "z")))

    (testing "the imaginary constant"
      (is (= (value-ast (parse "i"))
             i)))

    (testing "euler's number"
      (is (= (value-ast (parse "e"))
             Math/E)))

    (testing "pi"
      (is (= (value-ast (parse "pi"))
             Math/PI)))

    (testing "addition of two numbers"
      (is (= (value-ast (parse "1 + 5.5"))
             [add 1 5.5])))

    (testing "subtraction of two numbers"
      (is (= (value-ast (parse "2.5-e"))
             [sub 2.5 Math/E])))

    (testing "explicit multiplication of two numbers"
      (is (= (value-ast (parse " pi * 3 "))
             [mul Math/PI 3])))

    (testing "implicit multiplication of two numbers"
      (is (= (value-ast (parse "iz"))
             [mul i "z"])))

    (testing "division of two numbers"
      (is (= (value-ast (parse "3/5"))
             [div 3 5])))

    (testing "exponentiation of two numbers"
      (is (= (value-ast (parse "e^z"))
             [pow Math/E "z"])))

    (testing "the natural logarithm of a number"
      (is (= (value-ast (parse "log e"))
             [log Math/E]))
      (is (= (value-ast (parse "lne"))
             [log Math/E])))

    (testing "the sine of a number"
      (is (= (value-ast (parse "sin(pi)"))
             [sin Math/PI])))

    (testing "the cosine of a number"
      (is (= (value-ast (parse "cosz"))
             [cos "z"])))

    (testing "the tangent of a number"
      (is (= (value-ast (parse "tan i"))
             [tan i])))

    (testing "the negation of a number"
      (is (= (value-ast (parse "-i"))
             [negate i])))

    (testing "the double negation of a number"
      (is (= (value-ast (parse "--z"))
             [negate [negate "z"]])))

    (testing "the triple negation of a number"
      (is (= (value-ast (parse "---i"))
             [negate [negate [negate i]]])))

    (testing "the real part of a number"
      (is (= (value-ast (parse "re(z)"))
             [re "z"])))

    (testing "the imaginary part of a number"
      (is (= (value-ast (parse "im(i)"))
             [im i])))

    (testing "the argument of a number"
      (is (= (value-ast (parse "arge"))
             [arg Math/E])))

    (testing "the magnitude of a number"
      (is (= (value-ast (parse "mag z"))
             [mag "z"])))

    (testing "an expression with no brackets"
      (is (= (value-ast (parse "1+5^e-89i+pi/2^z"))
             [add [sub [add 1 [pow 5 Math/E]]
                       [mul 89 i]]
                  [div Math/PI
                       [pow 2 "z"]]])))

    (testing "an expression with brackets"
      (is (= (value-ast (parse "(z-1)^5 + 233(pi/4)*(1.5^z)"))
             [add [pow [sub "z" 1] 5]
                  [mul [mul 233 [div Math/PI 4]]
                       [pow 1.5 "z"]]])))

    (testing "exponents of exponents"
      (is (= (value-ast (parse "z^i^e^pi^1^2^3"))
             [pow "z" [pow i [pow Math/E [pow Math/PI [pow 1 [pow 2 3]]]]]])))

    (testing "exponents of exponents with brackets"
      (is (= (value-ast (parse "((((((z^i)^e)^pi)^1)^2)^3)"))
             [pow [pow [pow [pow [pow [pow "z" i]
                                      Math/E]
                                 Math/PI]
                            1]
                       2]
                  3])))

    (testing "an expression with lots of functions and variables"
      (is (= (value-ast (parse "isini(ipii)z+cos(log(tan(e^z)))"))
             [add [mul [mul [mul i [sin i]]
                            [mul [mul i Math/PI] i]]
                       "z"]
                  [cos [log [tan [pow Math/E "z"]]]]])))))
