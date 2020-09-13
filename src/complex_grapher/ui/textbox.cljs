(ns complex-grapher.ui.textbox
    (:require [reagent.core :as r]))

(defn textbox [value & {:keys [numeric? min max] :or {numeric? false}}]
  (let [textbox-node (r/atom nil)]
    (add-watch value
               :textbox
               (fn [_ _ _ new-value]
                 (if-let [textbox-node @textbox-node]
                   (if-not (= (.-value textbox-node) new-value)
                     (aset textbox-node "value" new-value)))))
    (fn []
      [:input {:ref #(reset! textbox-node %)
               :class "textbox"
               :type (if numeric? "number" "textbox")
               :defaultValue @value
               :onInput #(reset! value (-> % (.-target) (.-value)))
               :min (if (and numeric? min) min)
               :max (if (and numeric? max) max)}])))
