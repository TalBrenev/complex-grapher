(ns complex-grapher.test-utils
  (:require [complex-grapher.complex-arithmetic :refer [re im]]))

(def default-delta 0.00001)

(defn close?
  "Check if `x` and `y` are within `delta` of one another.
  If `delta` is not given, `complex-grapher.test-utils/default-delta` is used instead."
  ([x y]
   (close? x y default-delta))
  ([x y delta]
   (< (Math/abs (- x y)) delta)))

(defn complex-close?
  "Check if both the real and imaginary parts of two complex numbers are within `delta` of each other.
  If `delta` is not given, `complex-grapher.test-utils/default-delta` is used instead."
  ([x y]
   (complex-close? x y default-delta))
  ([x y delta]
   (and (close? (re x) (re y) delta)
        (close? (im x) (im y) delta))))
