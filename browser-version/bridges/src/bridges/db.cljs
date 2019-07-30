(ns bridges.db)

;;; Data Structures:
;;;
;;; island := { :x <int>
;;;             :y <int>
;;;             :num <int> }
;;;
;;; bridge := { :fst <island>
;;;             :snd <island>
;;;             :num <int> }
;;;
;;;   N.B.
;;;     islands implement the "point" interface: { :x <int>, :y <int> }
;;;     bridges implement the "line" interface: { :fst <point>, :snd <point> }

(defn bridges-number [c]
  (case c
    \1 1 \2 2 \3 3 \4 4
    \5 5 \6 6 \7 7 \8 8
    nil))

(defn str->islands [y s]
  (filter identity
    (map-indexed
      (fn [x c]
        (when-let [n (bridges-number c)]
          {:x x :y y :num n}))
      s)))

(defn read-puzzle [s]
  (->> s
       (clojure.string/trim)
       (clojure.string/split-lines)
       (map-indexed str->islands)
       (flatten)))

(defn puzzle-size [islands]
  {:width (->> islands (map :x) (apply max) (inc))
   :height (->> islands (map :y) (apply max) (inc))})

(def default-puzzle
"
3.4...3..
.........
.........
5..5..5.2
.........
2..1.....
.1....3.2
")

(defn reset-db [puzzle]
  (let [islands (read-puzzle puzzle)]
    {:board
     {:size (puzzle-size islands)
      :islands islands
      :bridges [{:fst {:x 3 :y 3 :num 5} :snd {:x 3 :y 5 :num 1} :num 1}
                {:fst {:x 6 :y 3 :num 5} :snd {:x 8 :y 3 :num 2} :num 2}]}}))
