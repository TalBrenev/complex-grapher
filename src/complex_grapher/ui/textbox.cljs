(ns complex-grapher.ui.textbox
    (:require [reagent.core :as r]))

(defn textbox [value]
  [:input {:type "textbox"
           :defaultValue @value
           :value @value
           :onInput #(reset! value (-> % (.-target) (.-value)))}])
