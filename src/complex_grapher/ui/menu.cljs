(ns complex-grapher.ui.menu
    (:require [reagent.core :as r]
              [complex-grapher.ui.textbox :refer [textbox]]))

(defn menu [graph-state initial-graph-state]
  (let [show (r/atom true)]
    (fn []
      [:div {:class "menu"}
        [:div.row
          [:div.input-wrapper
            [:label "Function:"]
            [textbox (r/cursor graph-state [:function])]]]
        [:div.row
          [:div.input-wrapper
            [:label "Magnitude Modulo:"]
            [textbox (r/cursor graph-state [:modulus])]]]
        [:div.button-wrapper
          [:button {:onClick #(reset! graph-state initial-graph-state)} "Reset"]
          [:button "Help"]
          [:button "About"]]
        [:div.hide-button-wrapper
          [:button {:onClick #(let [menu (-> % (.-target) (.-parentElement) (.-parentElement))]
                                (if @show
                                  (set! (-> menu (.-style) (.-top)) (str "-" (.-clientHeight menu) "px"))
                                  (set! (-> menu (.-style) (.-top)) ""))
                                (reset! show (not @show)))}
            (if @show "Hide" "Show")]]])))
