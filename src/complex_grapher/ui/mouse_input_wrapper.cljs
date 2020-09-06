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

(defn mouse-input-wrapper [{:keys [mouse-enter mouse-leave mouse-move drag zoom]} body]
  (let [pos            (r/atom nil)
        drag-start-pos (r/atom nil)]
    (fn []
      [:div {:style        {:width "100%" :height "100%"}
             :onMouseEnter (fn [event]
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
                               (if (= (.-length touches) 1)
                                 (reset! drag-start-pos (mouse-pos (first touches)))
                                 (reset! drag-start-pos nil))))
             :onTouchEnd   (fn [event]
                             (reset! drag-start-pos nil))
             :onTouchMove  (fn [event]
                             (reset! pos (-> event (.-touches) (first) (mouse-pos)))
                             (when drag (drag @drag-start-pos @pos))
                             (reset! drag-start-pos @pos))
             :onWheel      (fn [event]
                             (zoom (.-deltaY event)))}
        body])))
