(ns complex-grapher.ui.menu
    (:require [reagent.core :as r]
              [complex-grapher.ui.textbox :refer [textbox]]))

(defn menu [graph-state show-about show-help initial-graph-state]
  (let [show (r/atom true)]
    (fn []
      [:div.menu
        [:div.menu-wrapper
          [:div.function
            [:div.input-wrapper
              [:label "Function:"]
              [textbox (r/cursor graph-state [:function])]]]
          [:div.button-wrapper
            [:button {:onClick #(reset! graph-state initial-graph-state)} "Reset"]
            [:button {:onClick #(reset! show-help true)} "Help"]
            [:button {:onClick #(reset! show-about true)} "About"]]
          [:div.hide-button-wrapper
            [:button {:onClick #(let [menu (-> % (.-target) (.-parentElement) (.-parentElement) (.-parentElement))]
                                  (if @show
                                    (set! (-> menu (.-style) (.-top)) (str "-" (.-clientHeight menu) "px"))
                                    (set! (-> menu (.-style) (.-top)) ""))
                                  (reset! show (not @show)))}
              (if @show "Hide" "Show")]]]])))
