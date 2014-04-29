(ns physics.object-spec
  (:require
    [physics.object :as object]
    [physics.position :as position]
    [physics.spec-helper :as helper]
    [physics.vector :as vector]
    [speclj.core :refer :all]))

(defn world-momentum [world]
  (reduce vector/add (map #(vector/scale (:velocity %) (:mass %)) world)))

(defn world-energy [world]
  (reduce + (map #(* 0.5 (:mass %) (helper/square (vector/magnitude (:velocity %)))) world)))

(describe "object"
  (with v0 (vector/make))
  (with v11 (vector/make 1 1))
  (with o1 (object/make (position/make 1 1) 2 @v0 @v0 "o1"))
  (with o2 (object/make (position/make 1 2) 3 @v0 @v0 "o2"))
  (with o3 (object/make (position/make 10 10) 4 @v0 @v0 "o3"))
  (with world [@o1 @o2 @o3])

  (it "default values"
    (let [o (object/make)]
      (should= true (position/origin? (:position o)))
      (should= 0 (:mass o))
      (should= @v0 (:velocity o))
      (should= @v0 (:force o))
      (should= "TILT" (:name o))))

  (it "created with values"
    (let [pos (position/make 1 1)
          mass 2
          velocity (vector/make 2 2)
          force (vector/make 3 3)
          name "name"
          o (object/make pos mass velocity force name)]
      (should= pos (:position o))
      (should= mass (:mass o))
      (should= velocity (:velocity o))
      (should= force (:force o))
      (should= name (:name o))))

  (it "has gravity"
    (should= (/ 6 16) (object/gravity 2 3 4)))

  (it "has force between"
    (let [c3r2 (/ 3 (Math/sqrt 2))
          o1 (object/make (position/make 1 1) 2 v0 v0 "o1")
          o2 (object/make (position/make 2 2) 3 v0 v0 "o2")]
      (should= true (helper/vector-close-to?
                      (vector/make c3r2 c3r2)
                      (object/force-between o1 o2)))))

  (it "accumulatez forces"
    (let [acumulated-o1 (object/accumulate-forces @o1 @world)
          expected (vector/add (object/force-between @o1 @o2) (object/force-between @o1 @o3))]
      (should= true (helper/vector-close-to? expected (:force acumulated-o1)))))

  (it "calculates forces on all"
    (let [fs (object/calculate-forces-on-all @world)]
      (should= 3 (count fs))
      (should= (nth fs 0) (object/accumulate-forces @o1 @world))
      (should= (nth fs 1) (object/accumulate-forces @o2 @world))
      (should= (nth fs 2) (object/accumulate-forces @o3 @world))))

  (it "accelerates"
    (let [o (object/make (position/make) 2 @v11 @v11 "o1")
          ao (object/accelerate o)]
      (should= true (helper/vector-close-to? (vector/make 1.5 1.5) (:velocity ao)))))

  (it "accelerates all"
    (let [as (object/accelerate-all (object/calculate-forces-on-all @world))]
      (should= 3 (count @world))
      (should= (nth as 0) (-> @o1 (object/accumulate-forces @world) object/accelerate))
      (should= (nth as 1) (-> @o2 (object/accumulate-forces @world) object/accelerate))
      (should= (nth as 2) (-> @o3 (object/accumulate-forces @world) object/accelerate))))

  (it "repositions"
    (let [o (object/make (position/make 1 1) 2 @v11 @v0 "o1")
          ro (object/reposition o)]
      (should= (position/make 2 2) (:position ro))))        ;

  (it "reposition-all"
    (let [rs (-> @world object/calculate-forces-on-all object/accelerate-all object/reposition-all)]
      (should= 3 (count @world))
      (should= (nth rs 0) (-> @o1 (object/accumulate-forces @world) object/accelerate object/reposition))
      (should= (nth rs 1) (-> @o2 (object/accumulate-forces @world) object/accelerate object/reposition))
      (should= (nth rs 2) (-> @o3 (object/accumulate-forces @world) object/accelerate object/reposition))))

  (it "collided?"
    (should= true (object/collided? @o1 @o2))
    (should= false (object/collided? @o1 @o3)))

  (it "merges"
    (let [o1 (object/make (position/make 1 1) 2 (vector/make 1 0) (vector/make 1 1) "o1")
          o2 (object/make (position/make 1 2) 3 (vector/make -1 0) (vector/make 1 1) "o2")
          om (object/merge o1 o2)]
      (should= "o2.o1" (:name om))
      (should= true (vector/equal (position/make 1 1.4) (:position om)))
      (should= 5 (:mass om))
      (should= true (vector/equal (vector/make -1/5 0) (:velocity om)))
      (should= true (vector/equal (vector/make 2 2) (:force om)))))

  (it "difference list"
    (should= [1 3] (object/difference-list [1 2 3 4] [2 4])))

  (it "collide-all"
    (let [[collisions collided-world] (object/collide-all @world)]
      (should= 2 (count collided-world))
      (should-contain (object/merge @o1 @o2) collided-world)
      (should-contain @o3 collided-world)
      (should= 1 (count collisions))
      (should= (:position (object/merge @o1 @o2)) (first collisions))))

  ;(it "close-enough?"
  ;  (should= true (object/close-enough? [0 0] [0 0]))
  ;  (should= true (object/close-enough? [0 29] [0 0]))
  ;  (should= true (object/close-enough? [29 0] [0 0]))
  ;  (should= true (object/close-enough? [0 0] [0 29]))
  ;  (should= true (object/close-enough? [0 0] [29 0]))
  ;  (should= true (object/close-enough? [0 0] [14 14]))
  ;  (should= false (object/close-enough? [0 0] [0 31]))
  ;  (should= false (object/close-enough? [0 0] [31 0]))
  ;  (should= false (object/close-enough? [31 0] [0 0]))
  ;  (should= false (object/close-enough? [0 31] [0 0]))
  ;  (should= false (object/close-enough? [0 0] [15 16])))

  (it "update-all"
    (let [[collisions new-world] (object/update-all @world)]
      (should= 1 (count collisions))
      (should= 2 (count new-world))
      (should= #{"o2.o1" "o3"} (set (map :name new-world)))
      (should= true (helper/vector-close-to? (world-momentum @world) (world-momentum new-world)))))
  )



