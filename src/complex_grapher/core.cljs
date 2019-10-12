(ns complex-grapher.core
    (:require [complex-grapher.complex-arithmetic :refer [complex-from-cartesian add arg mag]]
              [complex-grapher.canvas :refer [draw fix-size width height]]
              [complex-grapher.parser :refer [parse prune evaluate]]
              [complex-grapher.graph :refer [graph]]))

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

(defn draw-graph []
  (let [{:keys [centre zoom]} @graph-state]
    (draw
      canvas-id
      (graph
        (top-left-corner centre zoom)
        (width canvas-id)
        (height canvas-id)
        zoom
        (get-modulus)
        (-> (get-function) (parse) (prune))))))

(defn setup []
  (fix-size canvas-id)
  (-> js/document
      (.getElementById "graphbutton")
      (.-firstChild)
      (.addEventListener "click" draw-graph)))

(.addEventListener js/window "load" setup)
