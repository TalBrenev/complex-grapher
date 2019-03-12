(ns complex-grapher.color)

(defn hsv->rgb [{:keys [h s v]}]
  (let [c (* s v)
        x (* c (- 1 (Math/abs (- (mod (/ h 60) 2) 1))))
        [r g b] (map #(int (* (+ % (- v c)) 255))
                     (cond
                       (<= 0   h 60)  [c x 0]
                       (<= 60  h 120) [x c 0]
                       (<= 120 h 180) [0 c x]
                       (<= 180 h 240) [0 x c]
                       (<= 240 h 300) [x 0 c]
                       (<= 300 h 360) [c 0 x]))]
    {:r r :g g :b b}))
