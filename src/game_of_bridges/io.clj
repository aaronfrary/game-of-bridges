(ns game-of-bridges.io)

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
          {:x (inc x) :y (inc y) :num n}))
      s)))

(defn read-puzzle [file-name]
  (->> file-name (slurp)
       (clojure.string/split-lines)
       (map-indexed str->islands)
       (flatten)))

#_(def p (read-puzzle "src/game_of_bridges/test-puzzle.txt"))

#_(prn (map :x p))
