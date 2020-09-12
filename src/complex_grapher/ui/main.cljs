(ns complex-grapher.ui.main
    (:require [reagent.core :as r]
              [reagent.dom :as d]
              [complex-grapher.ui.no-webgl :refer [no-webgl check-webgl]]
              [complex-grapher.ui.graph :refer [graph]]
              [complex-grapher.ui.controls :refer [controls]]
              [complex-grapher.ui.about :refer [about]]))

(defn get-time []
  (.getTime (js/Date.)))

(def initial-state {:webgl? nil
                    :last-resize (get-time)
                    :graph       {:centre              0
                                  :zoom                0.01
                                  :function            "z"
                                  :modulus             1
                                  :top-left-corner     0
                                  :bottom-right-corner 0
                                  :width               0
                                  :height              0}})

(defonce app-state (r/atom initial-state))

(defn app []
  [:div {:id "main"}
   [no-webgl (r/cursor app-state [:webgl?])]
   [graph (r/cursor app-state [:webgl?])
          (r/cursor app-state [:last-resize])
          (r/cursor app-state [:graph])]])

(defn setup-resize-listener []
  (.addEventListener js/window
                     "resize"
                     #(swap! app-state assoc :last-resize (get-time))))

(defn render-app []
  (d/render app (.getElementById js/document "app")))

(defn init []
  (setup-resize-listener)
  (render-app)
  (check-webgl (r/cursor app-state [:webgl?])))

(.addEventListener
  js/window
  "load"
  init)
