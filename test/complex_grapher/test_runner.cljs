(ns complex-grapher.test-runner
  (:require
    [cljs-test-display.core]
    [figwheel.main.testing :refer [run-tests]]))

(defn -main [& args]
  (run-tests (cljs-test-display.core/init! "test-results")))
