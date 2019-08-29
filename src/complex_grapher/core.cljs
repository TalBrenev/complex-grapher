(ns complex-grapher.core
    (:require [complex-grapher.complex-arithmetic :refer [complex-from-cartesian add arg mag]]
              [complex-grapher.canvas :refer [draw fix-size width height]]
              [complex-grapher.parser :refer [parse prune evaluate]]
              [complex-grapher.color  :refer [hsv->rgb]]))

(enable-console-print!)

(defonce graph-state (atom {:centre (complex-from-cartesian 0 0)
                            :zoom   0.01}))

(def canvas-id "canvas")

(defn top-left-corner [centre zoom]
  (add centre (complex-from-cartesian (- (* 0.5 zoom (width canvas-id)))
                                      (* 0.5 zoom (height canvas-id)))))

(defn bottom-right-corner [centre zoom]
  (add centre (complex-from-cartesian (* 0.5 zoom (width canvas-id))
                                      (- (* 0.5 zoom (height canvas-id))))))

(defn get-modulus []
  (-> js/document
      (.getElementById "modulus")
      (.-value)
      (js/parseFloat)))

(defn get-function []
  (-> js/document
      (.getElementById "function")
      (.-value)))

(defn arg->hue [a]
  (Math/floor (* (+ a Math/PI) (/ Math/PI) 180)))

(defn mag->val [m modulus]
  (let [v (/ (mod m modulus) modulus)]
    (if (> (mod m (* 2 modulus)) modulus)
      (- 1 v)
      v)))

(defn graph []
  (let [{:keys [centre zoom]} @graph-state
        start   (top-left-corner centre zoom)
        modulus (get-modulus)
        ast     (-> (get-function) (parse) (prune))]
    (draw canvas-id (map
                      (fn [y]
                        (map
                          (fn [x]
                            (let [z  (add start (complex-from-cartesian (* zoom x) (- (* zoom y))))
                                  fz (evaluate ast {"z" z})]
                              (hsv->rgb {:h (arg->hue (arg fz))
                                         :s 1
                                         :v (mag->val (mag fz) modulus)})))
                          (range (width canvas-id))))
                      (range (height canvas-id))))))

(defn setup []
  (fix-size canvas-id)
  (-> js/document
      (.getElementById "graphbutton")
      (.-firstChild)
      (.addEventListener "click" graph)))

(.addEventListener js/window "load" setup)
