(ns game-of-bridges.logic
  (:require [game-of-bridges.util :as util]
            [game-of-bridges.line :as line]))

(defn canonical-bridge [{:keys [fst snd num] :as bridge}]
  (if (util/str< snd fst) {:fst snd :snd fst :num num} bridge))

(defn bridge= [b1 b2]
  (= (dissoc (canonical-bridge b1) :num)
     (dissoc (canonical-bridge b2) :num)))

(defn get-bridges [state island]
  (filter #(some #{island} [(:fst %) (:snd %)])
          (:bridges state)))

(defn bridge-between [state island-1 island-2]
  (first (util/intersect (get-bridges state island-1)
                         (get-bridges state island-2))))

(defn num-occupied [state island]
  (apply + (map :num (get-bridges state island))))

(defn full? [state island]
  (= (num-occupied state island) (:num island)))

(defn connected? [state]
  (= (count (util/connected-components (:islands state) (:bridges state))) 1))

(defn game-won?
  "Return true if puzzle has been solved."
  [state]
  (and (every? (partial full? state) (:islands state))
       (connected? state)))

(defn isolating?
  "Return true if state illegal due to isolation."
  [state]
  (and (some (partial every? (partial full? state))
             (util/connected-components (:islands state) (:bridges state)))
       (not (game-won? state))))

(defn can-add-bridge? [state island-1 island-2]
  (not (or (full? state island-1)
           (full? state island-2)
           (case (:num (bridge-between state island-1 island-2))
             2 true, 1 false
             nil (line/get-blocked (:bridges state) island-1 island-2)))))

(defn neighbors
  "Return islands to which a bridge to `island' can be added."
  [island state]
  (if (full? state island) []
    (->> [:up :down :left :right]
         (keep #(util/get-item % island (:islands state)))
         (filter (partial can-add-bridge? state island)))))

(defn get-target [{:keys [islands bridges source] :as state} pos]
  (when-let [target (util/get-item (line/direction source pos)
                              source islands)]
    (and (some #{target} (neighbors source state)) target)))

(defn get-island-at [state pos]
  (some #(and (util/keys= [:x :y] pos %) %) (:islands state)))

(defn get-bridge-at [state pos]
  (->> [:up :down :left :right]
       (keep #(util/get-item % pos (:islands state)))
       (util/pairwise (partial bridge-between state))
       (keep identity)
       (first)))

(defn force-add-bridge [bridges bridge]
  (conj (remove (partial bridge= bridge) bridges)
        (canonical-bridge bridge)))

(defn add-new-bridge [bridges {:keys [source target]}]
  (force-add-bridge bridges {:fst source :snd target :num 1}))

(defn inc-bridge [bridges bridge]
  (keep #(cond
           (not (bridge= % bridge)) %
           (>= (:num %) 2)          nil
           (full? bridges (:fst %)) nil
           (full? bridges (:snd %)) nil
           :else (update-in % [:num] inc))
        bridges))

(defn add-bridge
  ([bridges bridge]
   (if bridge
     (add-bridge bridges (:fst bridge) (:snd bridge))
     bridges))
  ([bridges island-1 island-2]
   (if-let [bridge (bridge-between bridges island-1 island-2)]
     (inc-bridge bridges bridge)
     (add-new-bridge bridges {:source island-1 :target island-2}))))

(defn merge-bridges [bridges-1 bridges-2]
  (reduce force-add-bridge bridges-1 bridges-2))

