(ns complex-grapher.complex-arithmetic-test
  (:require [cljs.test :refer [deftest testing]]
            [complex-grapher.complex-arithmetic :refer [complex-from-cartesian
                                                        complex-from-polar
                                                        re im arg mag
                                                        add sub mul div]])
  (:require-macros [complex-grapher.test-utils :refer [are-close is-complex-close]]))

(deftest complex-arithmetic
  (testing "create a complex number"
    (testing "from caretesian coordinates"
      (let [z (complex-from-cartesian 0 0)]
        (are-close
          (re z)  0
          (im z)  0
          (arg z) 0
          (mag z) 0))
      (let [z (complex-from-cartesian 1 0)]
        (are-close
          (re z)  1
          (im z)  0
          (arg z) 0
          (mag z) 1))
      (let [z (complex-from-cartesian 0 1)]
        (are-close
          (re z)  0
          (im z)  1
          (arg z) (/ Math/PI 2)
          (mag z) 1))
      (let [z (complex-from-cartesian -1 0)]
        (are-close
          (re z)  -1
          (im z)  0
          (arg z) Math/PI
          (mag z) 1))
      (let [z (complex-from-cartesian 0 -1)]
        (are-close
          (re z)  0
          (im z)  -1
          (arg z) (- (/ Math/PI 2))
          (mag z) 1))
      (let [z (complex-from-cartesian -5 23)]
        (are-close
          (re z)  -5
          (im z)  23
          (arg z) 1.78485701
          (mag z) 23.53720459)))

    (testing "from polar coordinates"
      (let [z (complex-from-polar 0 0)]
        (are-close
          (re z)  0
          (im z)  0
          (arg z) 0
          (mag z) 0))
      (let [z (complex-from-polar (/ Math/PI 2) 1)]
        (are-close
          (re z)  0
          (im z)  1
          (arg z) (/ Math/PI 2)
          (mag z) 1))
      (let [z (complex-from-polar Math/PI 1)]
        (are-close
          (re z)  -1
          (im z)  0
          (arg z) Math/PI
          (mag z) 1))
      (let [z (complex-from-polar (- (/ Math/PI 2)) 1)]
        (are-close
          (re z)  0
          (im z)  -1
          (arg z) (- (/ Math/PI 2))
          (mag z) 1))
      (let [z (complex-from-polar 1025.78 123)]
        (are-close
          (re z)  -6.147266197
          (im z)  122.8462906
          (arg z) (mod 1025.78 (* Math/PI 2))
          (mag z) 123))))

  (testing "add"
    (testing "two complex numbers"
      (is-complex-close (add (complex-from-cartesian 3 6) (complex-from-cartesian 2.5 -8))
                        (complex-from-cartesian 5.5 -2)))
    (testing "two real numbers"
      (is-complex-close (add 2.79 3)
                        (complex-from-cartesian 5.79 0)))
    (testing "a real number and a complex number"
      (is-complex-close (add -3 (complex-from-cartesian 2 1))
                        (complex-from-cartesian -1 1))))

  (testing "subtract"
    (testing "two complex numbers"
      (is-complex-close (sub (complex-from-cartesian 10 4) (complex-from-cartesian -3 2.2))
                        (complex-from-cartesian 13 1.8)))
    (testing "two real numbers"
      (is-complex-close (sub 10 4)
                        (complex-from-cartesian 6 0)))
    (testing "a real number and a complex number"
      (is-complex-close (sub 0.5 (complex-from-cartesian 4 7))
                        (complex-from-cartesian -3.5 -7))))

  (testing "multiply"
    (testing "two complex numbers"
      (is-complex-close (mul (complex-from-polar 0.22 5) (complex-from-polar 0.3 6))
                        (complex-from-polar 0.52 30)))
    (testing "two real numbers"
      (is-complex-close (mul 2 2)
                        (complex-from-cartesian 4 0)))
    (testing "a real number and a complex number"
      (is-complex-close (mul 2 (complex-from-cartesian 1.5 3.5))
                        (complex-from-cartesian 3 7))))

  (testing "divide"
    (testing "two complex numbers"
      (is-complex-close (div (complex-from-polar 0.1 9) (complex-from-polar 0.3 3))
                        (complex-from-polar -0.2 3)))
    (testing "two real numbers"
      (is-complex-close (div 10 5)
                        (complex-from-cartesian 2 0)))
    (testing "a real number and a complex number"
      (is-complex-close (div (complex-from-cartesian 4 2) 2)
                        (complex-from-cartesian 2 1)))))
