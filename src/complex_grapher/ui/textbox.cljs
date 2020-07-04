(ns complex-grapher.ui.textbox
    (:require [reagent.core :as r]))

(defn textbox [value]
  (let [textbox-node (r/atom nil)]
    (add-watch value
               :textbox
               (fn [_ _ _ new-value]
                 (if-let [textbox-node @textbox-node]
                   (if-not (= (.-value textbox-node) new-value)
                     (aset textbox-node "value" new-value)))))
    (fn []
      [:input {:ref #(reset! textbox-node %)
               :type "textbox"
               :defaultValue @value
               :onInput #(reset! value (-> % (.-target) (.-value)))}])))
