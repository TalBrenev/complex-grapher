(ns complex-grapher.test-runner
  (:require
    [cljs-test-display.core]
    [figwheel.main.testing :refer [run-tests]]

    [complex-grapher.complex-arithmetic-test]))

(defn -main [& args]
  (run-tests (cljs-test-display.core/init! "test-results")))
