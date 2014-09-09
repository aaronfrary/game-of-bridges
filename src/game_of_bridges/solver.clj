(ns game-of-bridges.solver
  (:require [game-of-bridges.logic :as l]
            [game-of-bridges.util :as util]))

(defn implicit-single-bridges [bridges]
  (->> bridges
       (map #(update-in % [:num] dec))
       (filter (comp pos? :num))))

(defn add-potential-bridges [state island current-bridges]
  (map (partial l/add-bridge current-bridges island)
       (l/neighbors island (update-in state [:bridges]
                                      l/merge-bridges current-bridges))))

(defn all-configurations [state island]
  (loop [n (l/num-occupied state island)
         configurations [(l/get-bridges state island)]]
    (if (= n (:num island))
      (->> configurations
           (map (partial sort util/strcmp))
           (set))
      (recur (inc n)
             (mapcat (partial add-potential-bridges state island)
                              configurations)))))

(defn intersect-bridges [bridge-sets]
  (if (pos? (count bridge-sets))
    (->> bridge-sets
         (map #(into % (implicit-single-bridges %)))
         (apply util/intersect))
    bridge-sets))

(defn required-moves [state island]
  (->> (all-configurations state island)
       (map #(util/set-diff % (:bridges state)))
       (remove #(l/isolating? (update-in state [:bridges] l/merge-bridges %)))
       (intersect-bridges)))

(defn next-move [state]
  (first (mapcat (partial required-moves state) (:islands state))))

