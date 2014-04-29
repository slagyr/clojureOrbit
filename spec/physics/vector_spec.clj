(ns physics.vector-spec
  (:require [physics.vector :as vector]
            [physics.spec-helper :as helper]
            [speclj.core :refer :all]))

(describe "vector"
  (it "vector creation"
    (should= 0 (first (vector/make)))
    (should= 0 (second (vector/make)))
    (should= (vector/make 1 1) (vector/make 1 1))
    (should= 1 (first (vector/make 1 0)))
    (should= 0 (second (vector/make 1 0))))

  (it "vector addition"
    (should= true (vector/equal
                    (vector/make 2 2)
                    (vector/add
                      (vector/make 1 1)
                      (vector/make 1 1)))))

  (it "vector subtraction"
    (should= true (vector/equal
                    (vector/make 1 2)
                    (vector/subtract
                      (vector/make 3 4)
                      (vector/make 2 2)))))

  (it "vector scaling"
    (should= true (vector/equal
                    (vector/make 3 3)
                    (vector/scale
                      (vector/make 1 1)
                      3))))

  (it "magnitude"
    (should= 3.0 (vector/magnitude (vector/make 0 3)))
    (should= 3.0 (vector/magnitude (vector/make 3 0)))
    (should= 5.0 (vector/magnitude (vector/make 3 4))))

  (it "unit vector"
    (should= true (vector/equal
                    (vector/make 0 1)
                    (vector/unit (vector/make 0 99))))

    (should= true (vector/equal
                    (vector/make 1 0)
                    (vector/unit (vector/make 99 0))))

    (let [r2 (Math/sqrt (/ 1 2))]
      (should= true (helper/vector-close-to?
                      (vector/make r2 r2)
                      (vector/unit (vector/make 1 1))))))

  (it "rotate90"
    (should= (vector/make -1 2) (vector/rotate90 (vector/make 2 1))))
  )


