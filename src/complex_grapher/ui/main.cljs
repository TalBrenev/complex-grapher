(ns complex-grapher.ui.main
    (:require [complex-grapher.ui.graph :refer [graph]]
              [complex-grapher.ui.controls :refer [controls]]
              [complex-grapher.ui.overlay :refer [overlay]]))

(defn main [app-state]
  [:div {:class "main"}
   [overlay]
   [graph]
   [controls]])
