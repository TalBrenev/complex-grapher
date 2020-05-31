(ns complex-grapher.core-old
    (:require [complex-grapher.complex-arithmetic :refer [complex-from-cartesian complex->str add sub mul re im i]]
              [complex-grapher.parser :refer [parse]]
              [complex-grapher.webgl :refer [draw detect-webgl]]
              [complex-grapher.utils :refer [add-event-listener get-attr set-attr width height]]
              [cljsjs.smooth-scroll]))

(enable-console-print!)

;; Ids of elements in the DOM
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
(defonce location-id "graphlbl")
(defonce controls-id "controls")
(defonce controls-btn-id "ctr-show")
(defonce no-webgl-id "no-webgl-wrapper")

;; How much the graph shifts/zooms by when the zoom buttons are used
(defonce zoom-factor 2)
(defonce shift-factor 0.3)

(defonce graph-state (atom {:centre   (complex-from-cartesian 0 0)
                            :zoom     0.01
                            :function "z"
                            :modulus  0.5}))

(defn top-left-corner [centre zoom]
  "Computes the complex number represented by the top-left corner of the graph."
  (add centre (complex-from-cartesian (- (* 0.5 zoom (width canvas-id)))
                                      (* 0.5 zoom (height canvas-id)))))

(defn bottom-right-corner [centre zoom]
  "Computes the complex number represented by the bottom-right corner of the graph."
  (add centre (complex-from-cartesian (* 0.5 zoom (width canvas-id))
                                      (- (* 0.5 zoom (height canvas-id))))))

(defn draw-graph
  ([]
   (draw-graph @graph-state))
  ([state]
   (try
     (let [{:keys [centre zoom function modulus]} state
           top-left (top-left-corner centre zoom)
           bottom-right (bottom-right-corner centre zoom)]
       (set-attr top-left-id "innerText" (complex->str top-left))
       (set-attr bottom-right-id "innerText" (complex->str bottom-right))
       (draw canvas-id
             (parse function)
             modulus
             (re top-left)
             (re bottom-right)
             (im top-left)
             (im bottom-right))
       (set-attr overlay-id "style" "opacity: 0"))
     (catch :default e
       (set-attr overlay-id "style" "opacity: 1")))))

(defn zoom-in []
  (swap! graph-state update :zoom / 2))

(defn zoom-out []
  (swap! graph-state update :zoom * 2))

(defn shift-up []
  (swap! graph-state #(update % :centre sub (mul (* shift-factor (height canvas-id) (:zoom %)) i))))

(defn shift-down []
  (swap! graph-state #(update % :centre add (mul (* shift-factor (height canvas-id) (:zoom %)) i))))

(defn shift-left []
  (swap! graph-state #(update % :centre sub (* shift-factor (width canvas-id) (:zoom %)))))

(defn shift-right []
  (swap! graph-state #(update % :centre add (* shift-factor (width canvas-id) (:zoom %)))))

(defn graph-location [x y]
  (let [{:keys [centre zoom]} @graph-state
        top-left (top-left-corner centre zoom)
        bottom-right (bottom-right-corner centre zoom)
        graph-range (sub bottom-right top-left)]
    (complex->str
      (complex-from-cartesian
        (+ (re top-left) (* (/ x (width canvas-id)) (re graph-range)))
        (+ (im top-left) (* (/ y (height canvas-id)) (im graph-range)))))))

(defn toggle-controls []
  (if (= (get-attr controls-btn-id "innerText") "Show Controls")
    (do
      (set-attr controls-btn-id "innerText" "Hide Controls")
      (set-attr controls-id "style" "bottom: 0px"))
    (do
      (set-attr controls-btn-id "innerText" "Show Controls")
      (set-attr controls-id "style" ""))))

(defn show-no-webgl []
  (set-attr no-webgl-id "style" "visibility: visible"))

(defn setup-smooth-scroll []
  (.init js/smoothScroll #js {:speed 850}))

(defn setup-default-values []
  (set-attr function-id "value" (:function @graph-state))
  (set-attr modulus-id "value" (:modulus @graph-state)))

(defn setup-ui-events []
  (add-event-listener function-id "input" #(swap! graph-state assoc :function (get-attr function-id "value")))
  (add-event-listener modulus-id "input" #(swap! graph-state assoc :modulus (get-attr modulus-id "value")))
  (add-event-listener zoom-in-id "click" zoom-in)
  (add-event-listener zoom-out-id "click" zoom-out)
  (add-event-listener shift-up-id "click" shift-up)
  (add-event-listener shift-down-id "click" shift-down)
  (add-event-listener shift-left-id "click" shift-left)
  (add-event-listener shift-right-id "click" shift-right)
  (add-event-listener canvas-id "mouseover" #(set-attr location-id "style" "visibility: visible"))
  (add-event-listener canvas-id "mouseout" #(set-attr location-id "style" "visibility: hidden"))
  (add-event-listener canvas-id "mousemove" #(set-attr location-id "innerText" (graph-location (.-layerX %) (.-layerY %))))
  (add-event-listener controls-btn-id "click" toggle-controls))

(defn setup-graph []
  (add-watch graph-state :drawer
    (fn [_ _ _ new-state]
      (draw-graph new-state)))
  (.addEventListener js/window "resize" #(draw-graph))
  (draw-graph))

(defn setup []
  (if (detect-webgl canvas-id)
    (do
      (setup-smooth-scroll)
      (setup-default-values)
      (setup-ui-events)
      (setup-graph))
    (show-no-webgl)))

(.addEventListener js/window "load" setup)