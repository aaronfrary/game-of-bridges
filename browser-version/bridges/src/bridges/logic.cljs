(ns bridges.logic)

(defn get-bridges [bridges island]
  (filter #(some #{island} [(:fst %) (:snd %)])
          bridges))

(defn num-occupied [bridges island]
  (apply + (map :num (get-bridges bridges island))))

(defn full? [bridges island]
  (= (num-occupied bridges island) (:num island)))
