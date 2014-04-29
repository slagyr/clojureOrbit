(ns physics.position-spec
  (:require [physics.position :as position]
            [speclj.core :refer :all]))


(describe "position"
  (it "creation"
    (should= true (position/origin? (position/make)))
    (should= (position/make 1 1) (position/make 1 1))
    (should= 1 (first (position/make 1 0)))
    (should= 0 (second (position/make 1 0))))

  (it "addition"
    (should= (position/make 2 2)
             (position/add
               (position/make 1 1)
               (position/make 1 1))))

  (it "subtraction"
    (should= (position/make 1 1)
             (position/subtract
               (position/make 3 4)
               (position/make 2 3))))

  (it "distance"
    (should= 1.0
             (position/distance
               (position/make 0 0)
               (position/make 0 1)))

    (should= 1.0
             (position/distance
               (position/make 1 0)
               (position/make 2 0)))

    (should= 5.0
             (position/distance
               (position/make 0 0)
               (position/make 3 4)))
    )
  )
