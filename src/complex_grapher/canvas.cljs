(ns complex-grapher.canvas)

(defn draw [canvas-id pixels]
  "Draws an image on the canvas with id `canvas-id`. `pixels` must be a
  collection of collections, and each element must be a hash with keys `:r`,
  `:g`, and `:b`.  The dimensions of `pixels` must match the dimensions of the
  canvas."
  (let [canvas     (.getElementById js/document canvas-id)
        context    (.getContext canvas "2d")
        image-data (.getImageData context 0 0 (.-width canvas) (.-height canvas))
        data       (.-data image-data)]
    (.set data (clj->js (mapcat
                          (fn [row]
                            (mapcat
                              (fn [pixel]
                                [(:r pixel) (:g pixel) (:b pixel) 255])
                              row))
                          pixels)))
    (.putImageData context image-data 0 0)))

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
