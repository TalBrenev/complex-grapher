(ns complex-grapher.ui.controls
    (:require [reagent.core :as r]
              [goog.string :refer [unescapeEntities]]
              [complex-grapher.complex-arithmetic :refer [i add sub mul complex->str]]
              [complex-grapher.ui.textbox :refer [textbox]]))

(defn shift-factor [direction {:keys [width height zoom]}]
  (case direction
    :vertical   (mul i (* 0.3 height zoom))
    :horizontal (* 0.3 width zoom)))

(defn shift-up [graph-state]
  (swap!
    graph-state
    #(update % :centre add (shift-factor :vertical %))))

(defn shift-left [graph-state]
  (swap!
    graph-state
    #(update % :centre sub (shift-factor :horizontal %))))

(defn shift-right [graph-state]
  (swap!
    graph-state
    #(update % :centre add (shift-factor :horizontal %))))

(defn shift-down [graph-state]
  (swap!
    graph-state
    #(update % :centre sub (shift-factor :vertical %))))

(defn zoom-in [graph-state]
  (swap! graph-state update :zoom / 2))

(defn zoom-out [graph-state]
  (swap! graph-state update :zoom * 2))

(defn controls [graph-state initial-graph-state]
  (let [show (r/atom false)]
    (fn []
      [:div {:class "control" :style (if @show {:bottom "0px"} {})}
       [:button {:class "ctr-show mob"
                 :onClick #(swap! show not)}
        (if @show "Hide Controls" "Show Controls")]
       [:div {:class "ctrrow"}
        [:p {:class "label"} "Function:"]
        [textbox (r/cursor graph-state [:function])]]
       [:div {:class "ctrrow"}
        [:p {:class "label"} "Magnitude modulus:"]
        [textbox (r/cursor graph-state [:modulus]) :numeric? true :min 0]]
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
           {:onClick #(shift-up graph-state)}
           (unescapeEntities "&#8593;")]]
         [:div {:class "arrowhori"}
          [:button
           {:onClick #(shift-left graph-state)}
           (unescapeEntities "&#8592;")]]
         [:div {:class "arrowhori"}
          [:button
           {:onClick #(shift-right graph-state)}
           (unescapeEntities "&#8594;")]]
         [:div {:class "arrowvert"}
          [:button
           {:onClick #(shift-down graph-state)}
           (unescapeEntities "&#8595;")]]]
        [:div {:class "zooms"}
         [:div {:class "zoom"}
          [:button
           {:onClick #(zoom-out graph-state)}
           (unescapeEntities "&#8211;")]]
         [:div {:class "zoom"}
          [:button
           {:onClick #(zoom-in graph-state)}
           "+"]]]]
       [:div {:class "ctrrow ctrrow-b"}
        [:div
         [:button
          {:class "reset-btn"
           :onClick #(reset! graph-state initial-graph-state)}
          "Reset"]]]
       [:div {:class "ctrrow ctrrow-b"}
        [:a {:href "#about"}
         [:p {:class "learnmore"} "Learn more"]]]])))
