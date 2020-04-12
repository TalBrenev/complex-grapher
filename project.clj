(defproject complex-grapher "1.0.0"
  :description "A tool for graphing complex-valued functions."
  :url "https://www.complexgrapher.com/"
  ;; :license {:name "Eclipse Public License"
  ;;           :url "http://www.eclipse.org/legal/epl-v10.html"

  :min-lein-version "2.8.3"

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.339"]
                 [cljsjs/smooth-scroll "10.2.1-0"]]

  :source-paths ["src"]

  :aliases {"fig"       ["trampoline" "run" "-m" "figwheel.main"]
            "fig:build" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "fig:min"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]
            "fig:test"  ["run" "-m" "figwheel.main" "-co" "test.cljs.edn" "-m" complex-grapher.test-runner]}

  :plugins [[cider/cider-nrepl "0.21.1"]]

  :profiles {:dev {:dependencies [[com.bhauman/figwheel-main "0.2.0"]
                                  [com.bhauman/rebel-readline-cljs "0.1.4"]
                                  [cider/piggieback "0.4.0"]]
                   :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}
                   :clean-targets ^{:protect false} ["resources/public/cljs-out"
                                                     :target-path]}})
