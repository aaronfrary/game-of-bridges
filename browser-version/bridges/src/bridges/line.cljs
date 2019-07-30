(ns bridges.line)

(defn direction [{x1 :x y1 :y} {x2 :x y2 :y}]
  (cond (and (= x1 x2) (< y1 y2)) ::up
        (and (= x1 x2) (> y1 y2)) ::down
        (and (< x1 x2) (= y1 y2)) ::right
        (and (> x1 x2) (= y1 y2)) ::left))

(defn vertical? [{:keys [fst snd]}] (#{::up ::down} (direction fst snd)))
