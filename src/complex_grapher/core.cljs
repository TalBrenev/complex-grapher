(ns complex-grapher.core
    (:require [reagent.core :as r]
              [reagent.dom :as d]
              [cljsjs.smooth-scroll]
              [complex-grapher.ui.main :refer [main]]
              [complex-grapher.ui.no-webgl :refer [no-webgl check-webgl]]))

(defn get-time []
  (.getTime (js/Date.)))

(defonce app-state (r/atom {:webgl?      nil
                            :last-resize (get-time)
                            :graph       {:centre              0
                                          :zoom                0.01
                                          :function            "z"
                                          :modulus             0.5
                                          :top-left-corner     0
                                          :bottom-right-corner 0
                                          :width               0
                                          :height              0}}))

(defn app []
  [:div
   [no-webgl (r/cursor app-state [:webgl?])]
   [:div {:class "wrapper"}
    [:h1 {:class "title"} "The Complex Grapher"]
    [main app-state]
    [:p {:class "footnote"} "Created by " [:a {:href "https://www.talbrenev.com/"} "Tal Brenev"]]]])

(defn setup-smooth-scroll []
  (.init js/smoothScroll #js {:speed 850}))

(defn setup-resize-listener []
  (.addEventListener js/window
                     "resize"
                     #(swap! app-state assoc :last-resize (get-time))))

(.addEventListener
  js/window
  "load"
  (fn []
    (setup-smooth-scroll)
    (setup-resize-listener)
    (d/render app (.getElementById js/document "app"))
    (check-webgl (r/cursor app-state [:webgl?]))))
