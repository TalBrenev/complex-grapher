(ns complex-grapher.ui.mouse-input-wrapper
    (:require [reagent.core :as r]
              [complex-grapher.utils :refer [pos]]))

(defn- mouse-pos [event]
  "Gets the position of the mouse in a div on the based on the `clientX` and `clientY` properties of the given event.
  Returns a map with keys `:x` and `:y`."
  (let [{div-x :x div-y :y} (pos (.-id (.-target event)))
        client-x (.-clientX event)
        client-y (.-clientY event)]
    {:x (- client-x div-x)
     :y (- client-y div-y)}))

(defn- pos-dist [p1 p2]
  (Math/sqrt
    (+ (Math/pow (- (:x p1) (:x p2)) 2)
       (Math/pow (- (:y p1) (:y p2)) 2))))

(defn mouse-input-wrapper [{:keys [mouse-enter mouse-leave mouse-move drag zoom]} body]
  (let [pos              (r/atom nil)
        drag-start-pos   (r/atom nil)
        pinch-start-dist (r/atom nil)]
    (fn []
      [:div {:style        {:width "100%" :height "100%"}
             :onMouseEnter (fn [event]
                             (.addEventListener (.-target event) "wheel" #(.preventDefault %))
                             (reset! pos (mouse-pos event))
                             (when mouse-enter (mouse-enter @pos)))
             :onMouseLeave (fn [event]
                             (reset! pos nil)
                             (reset! drag-start-pos nil)
                             (when mouse-leave (mouse-leave)))
             :onMouseDown  (fn [event]
                             (reset! drag-start-pos (mouse-pos event)))
             :onMouseUp    (fn [event]
                             (reset! drag-start-pos nil))
             :onMouseMove  (fn [event]
                             (reset! pos (mouse-pos event))
                             (when mouse-move (mouse-move @pos))
                             (when @drag-start-pos
                               (when drag (drag @drag-start-pos @pos))
                               (reset! drag-start-pos @pos)))
             :onTouchStart (fn [event]
                             (let [touches (.-touches event)]
                               (case (.-length touches)
                                 1 (do
                                     (reset! drag-start-pos (mouse-pos (first touches)))
                                     (reset! pinch-start-dist nil))
                                 2 (do
                                     (reset! drag-start-pos nil)
                                     (reset! pinch-start-dist (apply pos-dist (mapv mouse-pos touches))))
                                 (do
                                   (reset! pinch-start-dist nil)
                                   (reset! drag-start-pos nil)))))
             :onTouchEnd   (fn [event]
                             (reset! drag-start-pos nil)
                             (reset! pinch-start-dist nil))
             :onTouchMove  (fn [event]
                             (let [touches (.-touches event)]
                               (when @drag-start-pos
                                 (let [pos (mouse-pos (first touches))]
                                   (when drag (drag @drag-start-pos pos))
                                   (reset! drag-start-pos pos)))
                               (when @pinch-start-dist
                                 (let [dist (apply pos-dist (mapv mouse-pos touches))]
                                   (when zoom (zoom (* 3 (- @pinch-start-dist dist))))
                                   (reset! pinch-start-dist dist)))))
             :onWheel      (fn [event]
                             (when zoom (zoom (.-deltaY event))))}
        body])))
