(ns complex-grapher.ui.graph
    (:require [complex-grapher.complex-arithmetic :refer [complex-from-cartesian re im add]]
              [complex-grapher.parser :refer [parse]]
              [complex-grapher.utils :refer [width height]]
              [complex-grapher.webgl :as webgl]))

(defonce canvas-id "canvas")

(defn top-left-corner [centre zoom]
  "Computes the complex number represented by the top-left corner of the graph."
  (add centre (complex-from-cartesian (- (* 0.5 zoom (width canvas-id)))
                                      (* 0.5 zoom (height canvas-id)))))

(defn bottom-right-corner [centre zoom]
  "Computes the complex number represented by the bottom-right corner of the graph."
  (add centre (complex-from-cartesian (* 0.5 zoom (width canvas-id))
                                      (- (* 0.5 zoom (height canvas-id))))))

(defn draw-graph [graph-state]
   (try
     (let [{:keys [centre zoom function modulus]} @graph-state
           top-left (top-left-corner centre zoom)
           bottom-right (bottom-right-corner centre zoom)]
       ;; TODO: top-left/bottom-right corner labels
       (webgl/draw canvas-id
                   (parse function)
                   modulus
                   (re top-left)
                   (re bottom-right)
                   (im top-left)
                   (im bottom-right)))
       ;; TODO: error overlay
     (catch :default e)))
       ;; TODO: error overlay

(defn graph [webgl? graph-state]
  (when @webgl? (draw-graph graph-state))
  [:div {:class "graph"}
   [:canvas {:id canvas-id}]
   [:p {:class "graphlbl"}]])
