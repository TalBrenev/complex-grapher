(ns complex-grapher.complex-arithmetic)

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

(defn add [x y]   "Adds the given complex numbers together."
  (complex-from-cartesian (+ (re x) (re y))
                          (+ (im x) (im y))))

(defn sub [x y]   "Subtracts the second complex number from the first."
  (complex-from-cartesian (- (re x) (re y))
                          (- (im x) (im y))))

(defn mul [x y]   "Multiplies the given complex numbers together."
  (complex-from-polar (+ (arg x) (arg y))
                      (* (mag x) (mag y))))

(defn div [x y]   "Divides the first complex number by the second."
  (complex-from-polar (- (arg x) (arg y))
                      (/ (mag x) (mag y))))