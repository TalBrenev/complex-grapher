(ns complex-grapher.ui.main
    (:require [goog.string :refer [unescapeEntities]]))

(defn main []
  [:div {:class "main"}
   [:div {:class "overlay"}
    [:p {:class "overlaytext"} "Invalid Function"]]
   [:div {:class "graph"}
    [:canvas {:id "canvas"}]
    [:p {:class "graphlbl"}]]
   [:div {:class "control"}
    [:button {:class "ctr-show mob"} "Show Controls"]
    [:div {:class "ctrrow"}
     [:p {:class "label"} "Function:"]
     [:input {:type "search"}]]
    [:div {:class "ctrrow"}
     [:p {:class "label"} "Magnitude modulus:"]
     [:input {:type "search"}]]
    [:div {:class "ctrrow"}
     [:p {:class "label"} "Top left corner:"]
     [:p {:class "info"}]]
    [:div {:class "ctrrow"}
     [:p {:class "label"} "Bottom right corner:"]
     [:p {:class "info"}]]
    [:div {:class "ctrrow ctrrow-b"}
     [:div {:class "arrows"}
      [:div {:class "arrowvert"}
       [:button (unescapeEntities "&#8593;")]]
      [:div {:class "arrowhori"}
       [:button (unescapeEntities "&#8592;")]]
      [:div {:class "arrowhori"}
       [:button (unescapeEntities "&#8594;")]]
      [:div {:class "arrowvert"}
       [:button (unescapeEntities "&#8595;")]]]
     [:div {:class "zooms"}
      [:div {:class "zoom"}
       [:button (unescapeEntities "&#8211;")]]
      [:div {:class "zoom"}
       [:button "+"]]]]
    [:div {:class "ctrrow ctrrow-b"}
     [:a {:data-scroll "" :href "#about"}
      [:p {:class "learnmore"} "Click here to learn more"]]]]])
