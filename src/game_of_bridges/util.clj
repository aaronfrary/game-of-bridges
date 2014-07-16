(ns game-of-bridges.util)

(defn keys= [ks & args]
  (every? #(apply = (map % args)) ks))

(defn set= [& args]
  (apply = (map set args)))

(defn intersect [& args]
  (apply clojure.set/intersection (map set args)))

(defn pairwise [f [hd & tl]]
  (if (seq tl)
    (concat (map (partial f hd) tl)
            (pairwise f tl))
    nil))

;;; Union Find algorithm for graph connectedness

(defn -find
  ([sets a] (some #(and (% a) %) sets))
  ([sets a & more]
   (clojure.set/union (-find sets a) (apply (partial -find sets) more))))

(defn -remove
  ([sets a] (set (filter (complement #(% a)) sets)))
  ([sets a & more] (apply (partial -remove (-remove sets a)) more)))

(defn -join
  ([sets a b] (conj (-remove sets a b) (-find sets a b)))
  ([sets {:keys [fst snd]}] (-join sets fst snd)))

(defn connected-components [nodes edges]
    (reduce -join (set (map hash-set nodes)) edges))

