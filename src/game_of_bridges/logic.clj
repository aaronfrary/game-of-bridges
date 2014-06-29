(ns game-of-bridges.logic
  (:require [game-of-bridges.util :as util]
            [game-of-bridges.line :as line]))

(defn get-bridges [bridges island]
  (filter #(some #{island} [(:fst %) (:snd %)])
          bridges))

(defn bridge-between [bridges island-1 island-2]
  (first (util/intersect (get-bridges bridges island-1)
                         (get-bridges bridges island-2))))

(defn num-occupied [bridges island]
  (apply + (map :num (get-bridges bridges island))))

(defn full? [bridges island]
  (= (num-occupied bridges island) (:num island)))

(defn connected? [islands bridges]
  (= (count (util/connected-components islands bridges)) 1))

(defn game-won?
  "Return true if puzzle has been solved."
  [islands bridges]
  (and (every? (partial full? bridges) islands)
       (connected? islands bridges)))

(defn can-add-bridge? [bridges island-1 island-2]
  (not (or (full? bridges island-1)
           (full? bridges island-2)
           (case (:num (bridge-between bridges island-1 island-2))
             2 true, 1 false
             nil (line/get-blocked bridges island-1 island-2)))))

(defn make-item-getter
  "Function factory for getting nearest item to a square in a direction."
  [x-compare y-compare xy-key first-or-last]
  (fn getter ([{:keys [x y]} items] (getter x y items))
    ([x y items]
     (->> items
          (filter #(and (x-compare (:x %) x) (y-compare (:y %) y)))
          (sort-by xy-key)
          (first-or-last)))))

(defn get-item [direction & args]
  (apply (case direction
           :up    (make-item-getter = > :y first)
           :down  (make-item-getter = < :y last)
           :left  (make-item-getter < = :x last)
           :right (make-item-getter > = :x first)
           (fn [& _] nil))
         args))

(defn neighbors
  "Return islands to which a bridge to `island' can be added."
  [island islands bridges]
  (if (full? bridges island) []
    (->> [:up :down :left :right]
         (map #(get-item % island islands))
         (filter identity)
         (filter (partial can-add-bridge? bridges island)))))

(defn get-target [x y source islands bridges]
  (when-let [target (get-item (line/direction source {:x x :y y})
                              source islands)]
    (and (some #{target} (neighbors source islands bridges)) target)))

(defn get-island-at [x y islands]
  (some #(and (= [x y] [(:x %) (:y %)]) %) islands))

(defn get-bridge-at [x y islands bridges]
  (->> [:up :down :left :right]
       (map #(get-item % x y islands))
       (filter identity)
       (util/pairwise (partial bridge-between bridges))
       (filter identity)
       (first)))

(defn add-bridge [bridges island-1 island-2]
  (conj bridges {:fst island-1 :snd island-2 :num 1}))

(defn inc-bridge [bridges bridge]
  (let [bridges (remove #{bridge} bridges)]
    (if (> (:num bridge) 1) bridges
      (conj bridges (update-in bridge [:num] inc)))))

