(ns complex-grapher.test-utils)

(def default-delta 0.00001)

(defn close?
  "Check if `x` and `y` are within `delta` of one another.
  If `delta` is not given, `complex-grapher.test-utils/default-delta` is used instead."
  ([x y]
   (close? x y default-delta))
  ([x y delta]
   (< (Math/abs (- x y)) delta)))
