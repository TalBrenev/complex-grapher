(ns complex-grapher.test-utils)

(defn close?
  "Returns true if `x` and `y` are within `delta` of one another.
  If `delta` is not given, it is 0.00001 by default."
  ([x y]
   (close? x y 0.00001))
  ([x y delta]
   (< (Math/abs (- x y)) delta)))
