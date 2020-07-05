(defproject complex-grapher "1.0.0"
  :description "A tool for graphing complex-valued functions."
  :url "https://www.talbrenev.com/complexgrapher/"
  :license {:name "GNU General Public License"
            :url "https://www.gnu.org/licenses/gpl-3.0.txt"}

  :min-lein-version "2.8.3"

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.339"]
                 [cljsjs/smooth-scroll "10.2.1-0"]
                 [reagent "0.10.0"]]

  :source-paths ["src"]

  :aliases {"fig:min" ["run" "-m" "figwheel.main" "-bo" "min"]}

  :plugins [[cider/cider-nrepl "0.21.1"]]

  :profiles {:dev {:dependencies [[com.bhauman/figwheel-main "0.2.0"]
                                  [cider/piggieback "0.4.0"]]
                   :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]
                                  :init (do (require 'figwheel.main.api) (future (figwheel.main.api/start "dev")))}
                   :clean-targets ^{:protect false} ["resources/public/cljs-out"
                                                     "resources/public/js"
                                                     :target-path]}})
