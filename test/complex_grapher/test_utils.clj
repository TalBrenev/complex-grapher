(ns complex-grapher.test-utils)

(defmacro close?
  "Check if `x` and `y` are within `delta` of one another.
  If `delta` is not given, it is 0.00001 by default."
  ([x y]
   `(close? ~x ~y 0.00001))
  ([x y delta]
   (list '< (list 'Math/abs (list '- x y)) delta)))
