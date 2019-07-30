(ns bridges.util
  (:require [clojure.set]))

(defn strcmp [s1 s2]
  (compare (str s1) (str s2)))

(def str< (comp neg? strcmp))

(defn keys= [ks & args]
  (every? #(apply = (map % args)) ks))

(defn set= [& args]
  (apply = (map set args)))

(defn intersect [& args]
  (apply clojure.set/intersection (map set args)))

(defn union [& args]
  (apply clojure.set/union (map set args)))

(defn set-diff [& args]
  (apply clojure.set/difference (map set args)))

(defn pairwise [f [hd & tl]]
  (if (seq tl)
    (concat (map (partial f hd) tl)
            (pairwise f tl))
    nil))

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
