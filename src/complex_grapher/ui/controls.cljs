(ns complex-grapher.ui.controls
    (:require [reagent.core :as r]
              [goog.string :refer [unescapeEntities]]
              [complex-grapher.complex-arithmetic :refer [i add sub mul complex->str]]))

;; How much the graph shifts/zooms by when the zoom/shift buttons are used
(defonce zoom-factor 2)
(defonce shift-factor 0.3)

(defn controls [graph-state]
  (let [show (r/atom false)]
    (fn []
      [:div {:class "control" :style (if @show {:bottom "0px"} {})}
       [:button {:class "ctr-show mob"
                 :onClick #(swap! show not)}
        (if @show "Hide Controls" "Show Controls")]
       [:div {:class "ctrrow"}
        [:p {:class "label"} "Function:"]
        [:input {:type "textbox"
                 :defaultValue (:function @graph-state)
                 :onInput #(swap! graph-state assoc :function (.-value (.-target %)))}]]
       [:div {:class "ctrrow"}
        [:p {:class "label"} "Magnitude modulus:"]
        [:input {:type "textbox"
                 :defaultValue (:modulus @graph-state)
                 :onInput #(swap! graph-state assoc :modulus (.-value (.-target %)))}]]
       [:div {:class "ctrrow"}
        [:p {:class "label"} "Top-left corner:"]
        [:p {:class "info"} (complex->str (:top-left-corner @graph-state))]]
       [:div {:class "ctrrow"}
        [:p {:class "label"} "Bottom-right corner:"]
        [:p {:class "info"} (complex->str (:bottom-right-corner @graph-state))]]
       [:div {:class "ctrrow ctrrow-b"}
        [:div {:class "arrows"}
         [:div {:class "arrowvert"}
          [:button
           {:onClick (fn [] (swap!
                              graph-state
                              #(update % :centre sub (mul (* shift-factor (:height @graph-state) (:zoom %)) i))))}
           (unescapeEntities "&#8593;")]]
         [:div {:class "arrowhori"}
          [:button
           {:onClick (fn [] (swap!
                              graph-state
                              #(update % :centre sub (* shift-factor (:width @graph-state) (:zoom %)))))}
           (unescapeEntities "&#8592;")]]
         [:div {:class "arrowhori"}
          [:button
           {:onClick (fn [] (swap!
                              graph-state
                              #(update % :centre add (* shift-factor (:width @graph-state) (:zoom %)))))}
           (unescapeEntities "&#8594;")]]
         [:div {:class "arrowvert"}
          [:button
           {:onClick (fn [] (swap!
                              graph-state
                              #(update % :centre add (mul (* shift-factor (:height @graph-state) (:zoom %)) i))))}
           (unescapeEntities "&#8595;")]]]
        [:div {:class "zooms"}
         [:div {:class "zoom"}
          [:button
           {:onClick #(swap! graph-state update :zoom * 2)}
           (unescapeEntities "&#8211;")]]
         [:div {:class "zoom"}
          [:button
           {:onClick #(swap! graph-state update :zoom / 2)}
           "+"]]]]
       [:div {:class "ctrrow ctrrow-b"}
        [:a {:data-scroll "" :href "#about"}
         [:p {:class "learnmore"} "Click here to learn more"]]]])))
