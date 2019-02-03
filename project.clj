(defproject complex-grapher "1.0.0"
  :description "A tool for graphing complex-valued functions."
  :url "https://www.complexgrapher.com/"
  ;; :license {:name "Eclipse Public License"
  ;;           :url "http://www.eclipse.org/legal/epl-v10.html"

  :min-lein-version "2.8.3"

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.238"]
                 [org.clojure/core.async  "0.4.474"]]

  :plugins [[lein-figwheel "0.5.18"]
            [lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]]

  :source-paths ["src"]

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src"]
                :figwheel {:on-jsload "complex-grapher.core/on-js-reload"
                           :open-urls ["http://localhost:3449/index.html"]}
                :compiler {:main complex-grapher.core
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/complex_grapher.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true
                           :preloads [devtools.preload]}}
               {:id "min"
                :source-paths ["src"]
                :compiler {:output-to "resources/public/js/compiled/complex_grapher.js"
                           :main complex-grapher.core
                           :optimizations :advanced
                           :pretty-print false}}]}

  :figwheel {:http-server-root "public"
             :server-port 3449
             :server-ip "127.0.0.1"
             :css-dirs ["resources/public/css"]
             :nrepl-port 7888}

  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.9"]
                                  [figwheel-sidecar "0.5.18"]
                                  [cider/piggieback "0.4.0"]]
                   :source-paths ["src" "dev"]
                   :plugins [[cider/cider-nrepl "0.20.0"]]
                   :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}
                   :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                                     :target-path]}})
