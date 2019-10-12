(ns complex-grapher.graph
  (:require [complex-grapher.complex-arithmetic :refer [complex-from-cartesian add arg mag]]
            [complex-grapher.parser :refer [evaluate]]
            [complex-grapher.color  :refer [hsv->rgb]]))

(defn arg->hue [a]
  (Math/floor (* (+ a Math/PI) (/ Math/PI) 180)))

(defn mag->val [m modulus]
  (let [v (/ (mod m modulus) modulus)]
    (if (> (mod m (* 2 modulus)) modulus)
      (- 1 v)
      v)))

(defn graph [start width height zoom modulus ast]
  "Given:
  - `start`, a complex number representing the top-left corner of the graph,
  - `width` and `height`, the dimensions, in pixels, of the graph,
  - `zoom`, the size of each pixel in the complex plane,
  - `modulus`, the maximum magnitude which can be graphed, and
  - `ast`, an abstract syntax tree of some complex function with variable z
  Returns a collection of collections, where each element is a hash with
  keys `:r`, `:g`, and `:b`., representing the graph of the function."
  (map
    (fn [y]
      (map
        (fn [x]
          (let [z  (add start (complex-from-cartesian (* zoom x) (- (* zoom y))))
                fz (evaluate ast {"z" z})]
            (hsv->rgb {:h (arg->hue (arg fz))
                       :s 1
                       :v (mag->val (mag fz) modulus)})))
        (range width)))
    (range height)))
