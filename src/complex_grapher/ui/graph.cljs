(ns complex-grapher.ui.graph)

(defonce canvas-id "canvas")

(defn graph []
  [:div {:class "graph"}
   [:canvas {:id canvas-id}]
   [:p {:class "graphlbl"}]])
