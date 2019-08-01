(ns bridges.game.logic
  (:require
   [bridges.game.util :as util]
   [bridges.game.line :as line]))

(defn canonical-bridge [{:keys [fst snd num] :as bridge}]
  (if (util/str< snd fst) {:fst snd :snd fst :num num} bridge))

(defn bridge= [b1 b2]
  (= (dissoc (canonical-bridge b1) :num)
     (dissoc (canonical-bridge b2) :num)))

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
  [islands bridges]
  (and (every? (partial full? bridges) islands)
       (connected? islands bridges)))

(defn can-add-bridge? [bridges island-1 island-2]
  (not (or (full? bridges island-1)
           (full? bridges island-2)
           (case (:num (bridge-between bridges island-1 island-2))
             2 true, 1 false
             nil (line/get-blocked bridges island-1 island-2)))))

(defn neighbors
  "Return islands to which a bridge to `island' can be added."
  [island {:keys [islands bridges]}]
  (if (full? bridges island) []
    (->> [:up :down :left :right]
         (keep #(util/get-item % island islands))
         (filter (partial can-add-bridge? bridges island)))))

(defn potential-bridges
  "Return bridges that could be added to `island'."
  [island state]
    (->> (neighbors island state)
         (map #(identity {:fst island :snd % :num 0}))
         (filter #(not-any? (partial bridge= %) (:bridges state)))))

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
