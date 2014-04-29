(ns orbitmain
  (:require [orbit.world :as world]))

(defn -main [& args]
  (world/run-world))
