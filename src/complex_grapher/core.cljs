(ns complex-grapher.core
    (:require [reagent.core :as r]
              [reagent.dom :as d]
              [goog.string :refer [unescapeEntities]]
              [cljsjs.smooth-scroll]))

(defn main []
  [:div
   [:div {:id "no-webgl-wrapper" :class "no-webgl-wrapper"}
    [:div {:class "no-webgl"}]]
   [:div {:id "wrapper" :class "wrapper"}
    [:h1 {:class "title"} "The Complex Grapher"]
    [:div {:id "main" :class "main"}
     [:div {:class "overlay"} {:id "error"}
      [:p {:class "overlaytext"} "Invalid Function"]]
     [:div {:class "graph"}
      [:canvas {:id "canvas"}]
      [:p {:id "graphlbl"}]]
     [:div {:id "controls" :class "control"}
      [:button {:id "ctr-show ":class "mob"} "Show Controls"]
      [:div {:class "ctrrow"}
       [:p {:class "label"} "Function:"]
       [:input {:type "search" :id "function"}]]
      [:div {:class "ctrrow"}
       [:p {:class "label"} "Magnitude modulus:"]
       [:input {:type "search" :id "modulus"}]]
      [:div {:class "ctrrow"}
       [:p {:class "label" :id "tlclabel"} "Top left corner:"]
       [:p {:class "info" :id "topleft"}]]
      [:div {:class "ctrrow"}
       [:p {:class "label" :id "brclabel"} "Bottom right corner:"]
       [:p {:class "info" :id "bottomright"}]]
      [:div {:class "ctrrow ctrrow-b"}
       [:div {:class "arrows"}
        [:div {:id "shiftup" :class "arrowvert"}
         [:button (unescapeEntities "&#8593;")]]
        [:div {:id "shiftleft" :class "arrowhori"}
         [:button (unescapeEntities "&#8592;")]]
        [:div {:id "shiftright" :class "arrowhori"}
         [:button (unescapeEntities "&#8594;")]]
        [:div {:id "shiftdown" :class "arrowvert"}
         [:button (unescapeEntities "&#8595;")]]]
       [:div {:class "zooms"}
        [:div {:id "zoomout" :class "zoom"}
         [:button (unescapeEntities "&#8211;")]]
        [:div {:id "zoomin" :class "zoom"}
         [:button "+"]]]]
      [:div {:class "ctrrow ctrrow-b"}
       [:a {:data-scroll "" :href "#about"}
        [:p {:class "learnmore"} "Click here to learn more"]]]]]
    [:p {:class "footnote"} "Created by " [:a {:href "https://www.talbrenev.com/"} "Tal Brenev"]]]])

(.addEventListener
  js/window
  "load"
  (fn []
    (.init js/smoothScroll #js {:speed 850})
    (d/render main (.getElementById js/document "app"))))
