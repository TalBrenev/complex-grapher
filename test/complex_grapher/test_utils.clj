(ns complex-grapher.test-utils
  (:require [cljs.test :refer [is are]]))

(defmacro is-close
  "Assert that `x` and `y` are within `delta` of one another.
  If `delta` is not given, `complex-grapher.test-utils/default-delta` is used instead."
  ([x y]
   `(is (close? ~x ~y)))
  ([x y delta]
   `(is (close? ~x ~y ~delta))))

(defmacro are-close
  "Assert that each pair of values is within `delta` of each other, where `delta` is the first value given.
  There must be an even number of remaining forms after `delta`.
  If `delta` is not given, `complex-grapher.test-utils/default-delta` is used instead."
  ([& forms]
   (if (even? (count forms))
     `(are [x y] (close? x y) ~@forms)
     `(are [x y] (close? x y ~(first forms)) ~@(rest forms)))))

(defmacro is-complex-close
  "Assert that both the real and imaginary parts of two complex numbers are within `delta` of each other.
  If `delta` is not given, `complex-grapher.test-utils/default-delta` is used instead."
  ([x y]
   `(is (complex-close? ~x ~y)))
  ([x y delta]
   `(is (complex-close? ~x ~y ~delta))))

(defmacro are-complex-close
  "Assert that each pair of complex numbers have both real and imaginary parts that are
  within `delta` of each other, where `delta` is the first value given.
  There must be an even number of remaining forms after `delta`.
  If `delta` is not given, `complex-grapher.test-utils/default-delta` is used instead."
  ([& forms]
   (if (even? (count forms))
     `(are [x y] (complex-close? x y) ~@forms)
     `(are [x y] (complex-close? x y ~(first forms)) ~@(rest forms)))))
