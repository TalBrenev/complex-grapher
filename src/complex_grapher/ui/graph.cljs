(ns complex-grapher.ui.graph
    (:require [reagent.core :as r]
              [complex-grapher.complex-arithmetic :refer [complex-from-cartesian re im add complex->str evaluate]]
              [complex-grapher.utils :refer [width height pos]]
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

(defn graphpos->complex [centre zoom pos-x pos-y]
  "Computes the complex number represented by a position on the graph."
  (add (top-left-corner centre zoom)
       (complex-from-cartesian (* zoom pos-x) (- (* zoom pos-y)))))

(defn graph [webgl? last-resize graph-state]
  (let [valid-function? (r/atom true)
        mouse-pos       (r/atom nil)
        mouse-dragging? (r/atom false)]
    (fn []
      @last-resize ;; Dereference to force render on window size change

      (when @webgl?
        (try
          (let [{:keys [centre zoom function modulus]} @graph-state
                  top-left (top-left-corner centre zoom)
                  bottom-right (bottom-right-corner centre zoom)]
              (swap! graph-state assoc :top-left-corner top-left)
              (swap! graph-state assoc :bottom-right-corner bottom-right)
              (swap! graph-state assoc :width (width canvas-id))
              (swap! graph-state assoc :height (height canvas-id))
              (webgl/draw canvas-id
                          function
                          modulus
                          (re top-left)
                          (re bottom-right)
                          (im top-left)
                          (im bottom-right)))
         (reset! valid-function? true)
         (catch :default e
           (reset! valid-function? false))))

      [:div
       [:div {:class "overlay" :style (if-not @valid-function? {:opacity 1} {:opacity 0})}
        [:p {:class "overlaytext"} "Invalid Function"]]
       [:div {:class "graph"}
        [:canvas {:id canvas-id
                  :onMouseLeave #(do
                                   (reset! mouse-pos nil)
                                   (reset! mouse-dragging? false))
                  :onMouseDown  #(reset! mouse-dragging? true)
                  :onMouseUp    #(reset! mouse-dragging? false)
                  :onMouseMove  #(if @mouse-dragging?
                                   (do
                                     (reset! mouse-pos nil)
                                     (let [change-x (.-movementX %)
                                           change-y (.-movementY %)
                                           {:keys [centre zoom]} @graph-state]
                                       (swap! graph-state assoc :centre (add centre (complex-from-cartesian
                                                                                      (- (* zoom change-x))
                                                                                      (* zoom change-y))))))
                                   (reset! mouse-pos (let [{:keys [x y]} (pos canvas-id)]
                                                       {:x (- (.-clientX %) x)
                                                        :y (- (.-clientY %) y)})))}]
        [:div {:class "graphlbl"}
         (if-let [{:keys [x y]} @mouse-pos]
           (let [{:keys [centre zoom function]} @graph-state
                 z (graphpos->complex centre zoom x y)
                 fz (if @valid-function? (evaluate function z))]
             [:div
              [:p {:class "graphlbl-row"} (str "z = " (complex->str z))]
              (if fz [:p {:class "graphlbl-row"} (str "f(z) = " (complex->str fz))])]))]]])))
