(ns complex-grapher.canvas)

(defn fix-size [canvas-id]
  (let [canvas (.getElementById js/document canvas-id)]
    (set! (.-width canvas)  (.-scrollWidth canvas))
    (set! (.-height canvas) (.-scrollHeight canvas))))

(defn width [canvas-id]
  "Gets the width of the canvas with id `canvas-id`."
  (-> js/document
      (.getElementById canvas-id)
      (.-width)))

(defn height [canvas-id]
  "Gets the height of the canvas with id `canvas-id`."
  (-> js/document
      (.getElementById canvas-id)
      (.-height)))
