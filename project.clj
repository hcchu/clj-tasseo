(defproject clj-tasseo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [hiccup "1.0.3"]
                 [cheshire "5.2.0"]
                 [me.raynes/fs "1.4.4"]
                 [prismatic/dommy "0.1.1"]]
  :plugins [[lein-ring "0.8.5"]
            [lein-cljsbuild "0.3.2"]]
  :ring {:handler clj-tasseo.handler/app}
  :cljsbuild {:builds
              [{:source-paths ["src/cljs_tasseo"]
               :compiler {:output-to "resources/public/j/clj-tasseo.js"
                          :optimizations :whitespace
                          :pretty-print true}}]}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.5"]]}})
