(ns bridges.db
  (:require [bridges.puzzles :as puzzles]))

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
       (clojure.string/split-lines)
       (map-indexed str->islands)
       (flatten)))

(defn puzzle-size [islands]
  {:width (->> islands (map :x) (apply max) (inc))
   :height (->> islands (map :y) (apply max) (inc))})

(def default-puzzle puzzles/example-10x10)

(defn reset-db [puzzle-string]
  (let [islands (read-puzzle puzzle-string)]
    {:puzzle-string puzzle-string
     :board {:size (puzzle-size islands)
             :islands islands
             :bridges []
             :source-island nil
             :target-island nil}}))
