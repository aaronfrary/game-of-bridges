(ns game-of-bridges.util)

(defn set= [& args]
  (apply = (map set args)))

(defn intersect [& args]
  (apply clojure.set/intersection (map set args)))

(defn pairwise [f [hd & tl]]
  (if (seq tl)
    (concat (map (partial f hd) tl)
            (pairwise f tl))
    nil))

#_(pairwise list '(1 2 3 4))
;((1 2) (1 3) (1 4) (2 3) (2 4) (3 4))
