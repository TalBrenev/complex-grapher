(ns complex-grapher.ui.graph
    (:require [reagent.core :as r]
              [complex-grapher.ui.mouse-input-wrapper :refer [mouse-input-wrapper]]
              [complex-grapher.complex-arithmetic :refer [complex-from-cartesian re im add complex->str evaluate]]
              [complex-grapher.utils :refer [width height]]
              [complex-grapher.webgl :as webgl]))

(defonce ^:private canvas-id "canvas")

(defn- graphpix->complex [zoom x y]
  "Given `x` and `y`, which represent the real and imaginary parts of a complex number in graph pixels,
  computes the actual complex number which they represent."
  (complex-from-cartesian (* zoom x) (- (* zoom y))))

(defn- graphpos->complex [top-left-corner zoom x y]
  "Computes the complex number represented by a position on the graph."
  (add top-left-corner (graphpix->complex zoom x y)))

(defn- compute-graph-info [graph-state]
  "Computes info about the graph (width/height, corner numbers) and stores it in the graph state."
  (let [{:keys [centre zoom]} @graph-state
        width (width canvas-id)
        height (height canvas-id)]
    (swap! graph-state merge {:width               width
                              :height              height
                              :top-left-corner     (add centre (complex-from-cartesian (- (* 0.5 zoom width))
                                                                                       (* 0.5 zoom height)))
                              :bottom-right-corner (add centre (complex-from-cartesian (* 0.5 zoom width)
                                                                                       (- (* 0.5 zoom height))))})))

(defn- draw-graph [graph-state]
  "Tries graphing the complex function. Throws an exception in case of invalid input."
  (let [{:keys [function top-left-corner bottom-right-corner]} @graph-state]
    (webgl/draw canvas-id
                function
                1
                (re top-left-corner)
                (re bottom-right-corner)
                (im top-left-corner)
                (im bottom-right-corner))))

(defn graph [webgl? last-resize graph-state]
  (let [valid-function? (r/atom true)
        mouse-pos       (r/atom nil)]
    (fn []
      @last-resize ;; Dereference to force re-render on window size change

      (when @webgl?
        (compute-graph-info graph-state)
        (try
          (draw-graph graph-state)
          (reset! valid-function? true)
          (catch :default e
            (reset! valid-function? false))))

      (let [{:keys [function top-left-corner zoom]} @graph-state]
        [:div {:class "graph"}
          [:div {:class "overlay" :style {:opacity (if @valid-function? 0 1)}}
            [:p "Invalid Function"]]
          [mouse-input-wrapper
            {:mouse-leave #(reset! mouse-pos nil)
             :mouse-move  #(reset! mouse-pos %)
             :zoom        (fn [amount]
                            (swap! graph-state update :zoom
                                   #(* % (Math/pow Math/E (/ amount 800)))))
             :drag        (fn [start end]
                            (swap! graph-state
                                   #(assoc % :centre (add (:centre %)
                                                          (graphpix->complex
                                                             (:zoom %)
                                                             (- (:x start) (:x end))
                                                             (- (:y start) (:y end)))))))}
            [:canvas {:id canvas-id}]]
          (if-let [{:keys [x y]} @mouse-pos]
            (when @valid-function?
              (let [z (graphpos->complex top-left-corner zoom x y)
                    fz (evaluate function z)]
                [:div {:class "mouse-position"}
                 [:p (str "z = " (complex->str z))]
                 [:p (str "f(z) = " (complex->str fz))]])))]))))
