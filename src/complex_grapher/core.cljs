(ns complex-grapher.core
    (:require [complex-grapher.complex-arithmetic :refer [complex-from-cartesian add arg mag]]
              [complex-grapher.canvas :refer [width height]]
              [complex-grapher.webgl :refer [draw]]))

(enable-console-print!)

(def canvas-id "canvas")

(defonce graph-state (atom {:centre (complex-from-cartesian 0 0)
                            :zoom   0.01}))

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

(defn draw-graph []
  (let [{:keys [centre zoom]} @graph-state]
    (draw canvas-id (get-modulus))))

(defn setup []
  (-> js/document
      (.getElementById "graphbutton")
      (.-firstChild)
      (.addEventListener "click" draw-graph)))

(.addEventListener js/window "load" setup)
