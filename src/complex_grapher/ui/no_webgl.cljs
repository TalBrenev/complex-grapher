(ns complex-grapher.ui.no-webgl
    (:require [reagent.core :as r]
              [complex-grapher.webgl :refer [detect-webgl]]))

(defonce canvas-id "tmp-canvas")

;; This only works if the no-webgl component is already rendered
(defn check-webgl [webgl?]
  (reset! webgl? (detect-webgl canvas-id)))

(defn no-webgl [webgl?]
  (case @webgl?
    nil   [:canvas {:id canvas-id :width 0 :height 0}]
    false [:div {:class "no-webgl-wrapper"} [:div {:class "no-webgl"}]]
    true  nil))
