(ns complex-grapher.core
    (:require))

(enable-console-print!)

(defonce app-state (atom {:text "Hello world!"}))

(println (:text @app-state))
