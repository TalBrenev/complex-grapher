(ns complex-grapher.complex-arithmetic)

(defprotocol ComplexArithmetic "Performs the basic arithmetic of the complex numbers."
  (re  [this]         "The real part of the complex number.")
  (im  [this]         "The imaginary part of the complex number.")
  (arg [this]         "The argument of the complex number, in radians.")
  (mag [this]         "The magnitude of the complex number.")
  (add [this other]   "Adds the given numbers together.")
  (sub [this other]   "Subtracts the second number from the first.")
  (mul [this other]   "Multiplies the given numbers together.")
  (div [this other]   "Divides the first number by the second."))

(defrecord ComplexNumber [real imaginary])

(defn complex-from-cartesian [real imaginary]
  (->ComplexNumber real imaginary))

(defn complex-from-polar [argument magnitude]
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
                  (Math/pow (im this) 2))))

  (add [this other]
    (complex-from-cartesian (+ (re this) (re other))
                            (+ (im this) (im other))))

  (sub [this other]
    (complex-from-cartesian (- (re this) (re other))
                            (- (im this) (im other))))

  (mul [this other]
    (complex-from-polar (+ (arg this) (arg other))
                        (* (mag this) (mag other))))

  (div [this other]
    (complex-from-polar (- (arg this) (arg other))
                        (/ (mag this) (mag other)))))

(extend-type number
  ComplexArithmetic

  (re [this]
    this)

  (im [this]
    0)

  (arg [this]
    0)

  (mag [this]
    this)

  (add [this other]
    (complex-from-cartesian (+ this (re other))
                            (im other)))

  (sub [this other]
    (complex-from-cartesian (- this (re other))
                            (- (im other))))

  (mul [this other]
    (complex-from-cartesian (* this (re other))
                            (* this (im other))))

  (div [this other]
    (complex-from-polar (- (arg other))
                        (/ this (mag other)))))
