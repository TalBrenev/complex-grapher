(ns complex-grapher.core
    (:require [complex-grapher.complex-arithmetic :refer [complex-from-cartesian complex->str add sub mul re im i]]
              [complex-grapher.parser :refer [parse]]
              [complex-grapher.webgl :refer [draw]]
              [complex-grapher.utils :refer [get-element get-value set-value add-event-listener width height fix-size]]))

(enable-console-print!)

(defonce canvas-id "canvas")
(defonce top-left-id "topleft")
(defonce bottom-right-id "bottomright")
(defonce overlay-id "error")
(defonce function-id "function")
(defonce modulus-id "modulus")
(defonce zoom-in-id "zoomin")
(defonce zoom-out-id "zoomout")
(defonce shift-up-id "shiftup")
(defonce shift-down-id "shiftdown")
(defonce shift-right-id "shiftright")
(defonce shift-left-id "shiftleft")

(defonce graph-state (atom {:centre   (complex-from-cartesian 0 0)
                            :zoom     0.01
                            :function "z"
                            :modulus  0.5}))

(defn top-left-corner [centre zoom]
  (add centre (complex-from-cartesian (- (* 0.5 zoom (width canvas-id)))
                                      (* 0.5 zoom (height canvas-id)))))

(defn bottom-right-corner [centre zoom]
  (add centre (complex-from-cartesian (* 0.5 zoom (width canvas-id))
                                      (- (* 0.5 zoom (height canvas-id))))))

(defn show-error-overlay []
  (set! (.-style (get-element overlay-id)) "opacity: 1"))

(defn hide-error-overlay []
  (set! (.-style (get-element overlay-id)) "opacity: 0"))

(defn draw-graph
  ([]
   (draw-graph @graph-state))
  ([state]
   (try
     (let [{:keys [centre zoom function modulus]} state
           top-left (top-left-corner centre zoom)
           bottom-right (bottom-right-corner centre zoom)]
       (set! (.-innerText (get-element top-left-id)) (complex->str top-left))
       (set! (.-innerText (get-element bottom-right-id)) (complex->str bottom-right))
       (draw canvas-id
             (parse function)
             modulus
             (re top-left)
             (re bottom-right)
             (im top-left)
             (im bottom-right))
       (hide-error-overlay))
     (catch :default e
       (show-error-overlay)))))

(defn setup []
  (fix-size canvas-id)

  (set-value function-id (:function @graph-state))
  (set-value modulus-id (:modulus @graph-state))

  (add-event-listener function-id "input" #(swap! graph-state assoc :function (get-value function-id)))
  (add-event-listener modulus-id "input" #(swap! graph-state assoc :modulus (get-value modulus-id)))
  (add-event-listener zoom-in-id "click" #(swap! graph-state update :zoom / 2))
  (add-event-listener zoom-out-id "click" #(swap! graph-state update :zoom * 2))
  (add-event-listener shift-up-id "click" #(swap! graph-state (fn [state]
                                                               (update state :centre sub (mul (* 0.3 (height canvas-id) (:zoom state)) i)))))
  (add-event-listener shift-down-id "click" #(swap! graph-state (fn [state]
                                                                  (update state :centre add (mul (* 0.3 (height canvas-id) (:zoom state)) i)))))
  (add-event-listener shift-left-id "click" #(swap! graph-state (fn [state]
                                                                  (update state :centre sub (* 0.3 (width canvas-id) (:zoom state))))))
  (add-event-listener shift-right-id "click" #(swap! graph-state (fn [state]
                                                                   (update state :centre add (* 0.3 (width canvas-id) (:zoom state))))))

  (add-watch graph-state :drawer
    (fn [_ _ _ new-state]
      (draw-graph new-state)))

  (.addEventListener js/window "resize" #(do (fix-size canvas-id) (draw-graph)))

  (draw-graph))

(.addEventListener js/window "load" setup)
