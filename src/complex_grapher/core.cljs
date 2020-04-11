(ns complex-grapher.core
    (:require [complex-grapher.complex-arithmetic :refer [complex-from-cartesian add re im]]
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
  (let [{:keys [centre zoom]} @graph-state
        top-left (top-left-corner centre zoom)
        bottom-right (bottom-right-corner centre zoom)]
    (draw canvas-id
          (re top-left)
          (im top-left)
          (re bottom-right)
          (im bottom-right)
          (get-modulus))))

(defn setup []
  (-> js/document
      (.getElementById "graphbutton")
      (.-firstChild)
      (.addEventListener "click" draw-graph)))

(.addEventListener js/window "load" setup)
