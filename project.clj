(defproject clojureOrbit "0.1"
  :description "simple Orbital Simulator written in Clojure."
  :url "http://github.com/unclebob/clojureOrbit"
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :profiles {:dev {:dependencies [[speclj "3.0.2"]]}}
  :plugins [[speclj "3.0.2"]]
  :test-paths ["spec"]
  :main orbitmain
  )

