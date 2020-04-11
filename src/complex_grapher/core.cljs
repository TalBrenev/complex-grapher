(ns complex-grapher.core
    (:require [complex-grapher.complex-arithmetic :refer [complex-from-cartesian add re im]]
              [complex-grapher.parser :refer [parse]]
              [complex-grapher.canvas :refer [fix-size width height]]
              [complex-grapher.webgl :refer [draw]]
              [complex-grapher.utils :refer [get-value set-value add-event-listener]]))

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

(defn draw-graph [state]
  (let [{:keys [centre zoom function modulus]} state
        top-left (top-left-corner centre zoom)
        bottom-right (bottom-right-corner centre zoom)]
    (draw canvas-id
          (parse function)
          modulus
          (re top-left)
          (im top-left)
          (re bottom-right)
          (im bottom-right))))

(defn setup []
  (fix-size canvas-id)

  (set-value "function" (:function @graph-state))
  (set-value "modulus" (:modulus @graph-state))

  (add-event-listener "function" "input" #(swap! graph-state assoc :function (get-value "function")))
  (add-event-listener "modulus" "input" #(swap! graph-state assoc :modulus (get-value "modulus")))

  (add-watch graph-state :drawer
    (fn [_ _ _ new-state]
      (draw-graph new-state)))

  (draw-graph @graph-state))

(.addEventListener js/window "load" setup)
