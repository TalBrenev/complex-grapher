(ns complex-grapher.core
    (:require [complex-grapher.complex-arithmetic :refer [complex-from-cartesian add sub mul re im i]]
              [complex-grapher.parser :refer [parse]]
              [complex-grapher.canvas :refer [fix-size width height]]
              [complex-grapher.webgl :refer [draw]]
              [complex-grapher.utils :refer [get-element get-value set-value add-event-listener]]))

(enable-console-print!)

(def canvas-id "canvas")

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
  (set! (.-style (get-element "error")) "opacity: 1"))

(defn hide-error-overlay []
  (set! (.-style (get-element "error")) "opacity: 0"))

(defn draw-graph [state]
  (try
    (let [{:keys [centre zoom function modulus]} state
          top-left (top-left-corner centre zoom)
          bottom-right (bottom-right-corner centre zoom)]
      (draw canvas-id
            (parse function)
            modulus
            (re top-left)
            (re bottom-right)
            (im top-left)
            (im bottom-right))
      (hide-error-overlay))
    (catch :default e
      (show-error-overlay))))

(defn setup []
  (fix-size canvas-id)

  (set-value "function" (:function @graph-state))
  (set-value "modulus" (:modulus @graph-state))

  (add-event-listener "function" "input" #(swap! graph-state assoc :function (get-value "function")))
  (add-event-listener "modulus" "input" #(swap! graph-state assoc :modulus (get-value "modulus")))
  (add-event-listener "zoomin" "click" #(swap! graph-state update :zoom / 2))
  (add-event-listener "zoomout" "click" #(swap! graph-state update :zoom * 2))
  (add-event-listener "shiftup" "click" #(swap! graph-state (fn [state]
                                                              (update state :centre sub (mul (* 0.3 (width canvas-id) (:zoom state)) i)))))
  (add-event-listener "shiftdown" "click" #(swap! graph-state (fn [state]
                                                                (update state :centre add (mul (* 0.3 (width canvas-id) (:zoom state)) i)))))
  (add-event-listener "shiftleft" "click" #(swap! graph-state (fn [state]
                                                                (update state :centre sub (* 0.3 (width canvas-id) (:zoom state))))))
  (add-event-listener "shiftright" "click" #(swap! graph-state (fn [state]
                                                                 (update state :centre add (* 0.3 (width canvas-id) (:zoom state))))))

  (add-watch graph-state :drawer
    (fn [_ _ _ new-state]
      (draw-graph new-state)))

  (draw-graph @graph-state))

(.addEventListener js/window "load" setup)
