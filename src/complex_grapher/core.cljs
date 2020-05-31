(ns complex-grapher.core
    (:require [reagent.core :as r]
              [reagent.dom :as d]
              [cljsjs.smooth-scroll]
              [complex-grapher.ui.main :refer [main]]
              [complex-grapher.complex-arithmetic :refer [complex-from-cartesian]]))

(defonce app-state (r/atom {:centre   (complex-from-cartesian 0 0)
                            :zoom     0.01
                            :function "z"
                            :modulus  0.5}))

(defn app []
  [:div
   [:div {:id "no-webgl-wrapper" :class "no-webgl-wrapper"}
    [:div {:class "no-webgl"}]]
   [:div {:id "wrapper" :class "wrapper"}
    [:h1 {:class "title"} "The Complex Grapher"]
    [main]
    [:p {:class "footnote"} "Created by " [:a {:href "https://www.talbrenev.com/"} "Tal Brenev"]]]])

(defn setup-smooth-scroll []
  (.init js/smoothScroll #js {:speed 850}))

(.addEventListener
  js/window
  "load"
  (fn []
    (setup-smooth-scroll)
    (d/render app (.getElementById js/document "app"))))
