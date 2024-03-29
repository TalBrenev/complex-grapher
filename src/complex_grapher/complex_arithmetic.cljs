(ns complex-grapher.complex-arithmetic
  (:require [goog.string :as gstring]
            [goog.string.format]
            [complex-grapher.parser :as parser]))

(defprotocol ComplexArithmetic "Perform the basic arithmetic of the complex numbers."
  (re  [this] "The real part of the complex number.")
  (im  [this] "The imaginary part of the complex number.")
  (arg [this] "The argument of the complex number, in radians.")
  (mag [this] "The magnitude of the complex number."))

(defrecord ComplexNumber [real imaginary])

(defn complex-from-cartesian [real imaginary]
  "Create a complex number by specifying cartesian coordinates."
  (->ComplexNumber real imaginary))

(defn complex-from-polar [argument magnitude]
  "Create a complex number by specifying polar coordinates."
  (->ComplexNumber (* magnitude (Math/cos argument))
                   (* magnitude (Math/sin argument))))

(defn complex->str [z]
  (if (pos? (im z))
    (gstring/format "%.3f + %.3fi" (re z) (im z))
    (gstring/format "%.3f - %.3fi" (re z) (- (im z)))))

(def i (complex-from-cartesian 0 1))

(extend-type ComplexNumber
  ComplexArithmetic
  (re [this]
    (:real this))
  (im [this]
    (:imaginary this))
  (arg [this]
    (Math/atan2 (im this) (re this)))
  (mag [this]
    (Math/sqrt (+ (Math/pow (re this) 2)
                  (Math/pow (im this) 2)))))

(extend-type number
  ComplexArithmetic
  (re [this]
    this)
  (im [this]
    0)
  (arg [this]
    0)
  (mag [this]
    this))

(defn add [x y] "Adds the given complex numbers together."
  (complex-from-cartesian (+ (re x) (re y))
                          (+ (im x) (im y))))

(defn sub [x y] "Subtracts the second complex number from the first."
  (complex-from-cartesian (- (re x) (re y))
                          (- (im x) (im y))))

(defn mul [x y] "Multiplies the given complex numbers together."
  (complex-from-polar (+ (arg x) (arg y))
                      (* (mag x) (mag y))))

(defn div [x y] "Divides the first complex number by the second."
  (complex-from-polar (- (arg x) (arg y))
                      (/ (mag x) (mag y))))

(defn negate [x] "Returns the additive inverse of the complex number."
  (sub 0 x))

(defn pow [x y] "Returns the exponent of the first complex number to the second."
  (let [a (arg x)
        b (Math/log (mag x))
        c (re y)
        d (im y)]
    (complex-from-polar (+ (* a c) (* b d))
                        (Math/pow Math/E (- (* b c) (* a d))))))

(defn log [x] "Computes the natural logarithm of the given complex number."
  (complex-from-cartesian (Math/log (mag x))
                          (arg x)))

(defn sin [x] "Computes the sine of the given complex number."
  (let [a (pow Math/E (mul i x))]
    (div (sub a (div 1 a)) (mul 2 i))))

(defn cos [x] "Computes the cosine of the given complex number."
  (let [a (pow Math/E (mul i x))]
    (div (add a (div 1 a)) 2)))

(defn tan [x] "Computes the tangent of the given complex number."
  (let [a (pow Math/E (mul i x))
        b (div 1 a)]
    (div (sub a b) (mul i (add a b)))))

(defn conjugate [x] "Computes the complex conjugate of the given complex number."
  (complex-from-cartesian (re x) (- (im x))))

(defn mandelbrot [c] "Computes mandelbrot set values."
  (if-let [i (first
               (reduce
                 (fn [[result z] i]
                   (if result
                     [result z]
                     (let [z (add (complex-from-cartesian (- (Math/pow (re z) 2)
                                                             (Math/pow (im z) 2))
                                                          (* 2 (re z) (im z)))
                                  c)]
                       (if (> (mag z) 2)
                         [i z]
                         [nil z]))))
                 [nil (complex-from-cartesian 0 0)]
                 (range 200)))]
    (complex-from-polar (* (Math/sqrt (/ i 200))
                           (* Math/PI 1.5))
                        1)
    (complex-from-cartesian 0 0)))

(defn- _evaluate [transformed-ast z])

(def ^:private token-map
  {:re     re
   :im     im
   :arg    arg
   :mag    mag
   :sin    sin
   :cos    cos
   :tan    tan
   :log    log
   :negate negate
   :conj   conjugate
   :z      "z"
   :e      Math/E
   :pi     Math/PI
   :i      i
   :add    add
   :sub    sub
   :mul    mul
   :div    div
   :pow    pow
   :man    mandelbrot})

(defn- evaluate-ast [ast z]
  (cond
    (= ast "z")      z
    (not (seq? ast)) ast
    :else            (apply
                       (first ast)
                       (map #(evaluate-ast % z) (rest ast)))))

(defn evaluate [expression z]
  (evaluate-ast
    (parser/transform-ast
      (parser/parse expression)
      token-map
      js/parseFloat)
    z))
