(defproject snake "1"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [helpers "1"]
                 [quil "2.6.0"]]

  :main ^:skip-aot snake.main

  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}})
