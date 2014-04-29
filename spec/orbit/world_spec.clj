(ns orbit.world-spec
  (:require
    [orbit.world :refer :all]
    [speclj.core :refer :all]))

(describe "world"
  (it "ages collisions"
    (should= [[4 :x]] (age-collisions [[5 :x]]))
    (should= [[1 :y]] (age-collisions [[1 :x] [2 :y]]))))