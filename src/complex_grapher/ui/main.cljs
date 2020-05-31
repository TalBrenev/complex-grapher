(ns complex-grapher.ui.main
    (:require [reagent.core :as r]
              [complex-grapher.ui.graph :refer [graph]]
              [complex-grapher.ui.controls :refer [controls]]))

(defn main [app-state]
  [:div {:class "main"}
   [graph (r/cursor app-state [:webgl?]) (r/cursor app-state [:graph])]
   [controls (r/cursor app-state [:graph])]])
