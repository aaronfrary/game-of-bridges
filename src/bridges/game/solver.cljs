(ns bridges.game.solver
  (:require
   [bridges.game.logic :as l]
   [bridges.game.util :as util]))

(defn- implicit-single-bridges [bridges]
  (->> bridges
       (map #(update-in % [:num] dec))
       (filter (comp pos? :num))))

(defn- add-potential-bridges [state island current-bridges]
  (map (partial l/add-bridge current-bridges island)
       (l/neighbors island (update-in state [:bridges]
                                      l/merge-bridges current-bridges))))

(defn- all-configurations [state island]
  (loop [n (l/num-occupied (:bridges state) island)
         configurations [(l/get-bridges (:bridges state) island)]]
    (if (= n (:num island))
      (->> configurations
           (map (partial sort util/strcmp))
           (set))
      (recur (inc n)
             (mapcat (partial add-potential-bridges state island)
                              configurations)))))

(defn- intersect-bridges [bridge-sets]
  (if (pos? (count bridge-sets))
    (->> bridge-sets
         (map #(into % (implicit-single-bridges %)))
         (apply util/intersect))
    bridge-sets))

(defn- required-moves [state island]
  (->> (all-configurations state island)
       (map #(util/set-diff % (:bridges state)))
       (remove #(l/isolating? (update-in state [:bridges] l/merge-bridges %)))
       (intersect-bridges)))

(defn- required-8s [state]
  (let [centers (filter #(= (:num %) 8) (:islands state))
        neighbors (map #(l/neighbors % state) centers)]
    (mapcat (fn [c nbs] (map #(-> {:fst c :snd % :num 1}) nbs))
         centers neighbors)))

(defn- required-7s [state]
  (let [centers (filter #(= (:num %) 7) (:islands state))
        neighbors (map #(l/neighbors % state) centers)]
    (mapcat (fn [c nbs]
              (keep #(when (not (l/bridge-between (:bridges state) c %))
                      {:fst c :snd % :num 1}) nbs))
         centers neighbors)))

(defn next-move [state]
  (or (first (required-8s state))
      (first (required-7s state))
      (first (mapcat (partial required-moves state) (:islands state)))))
