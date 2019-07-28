(ns bridges.db)

(def default-db
  {:board
   {:size {:width 10 :height 10}
    :islands [{:x 0 :y 0 :num 2} {:x 9 :y 0 :num 2} {:x 0 :y 9 :num 2} {:x 9 :y 9 :num 2}]
    :bridges []}})
