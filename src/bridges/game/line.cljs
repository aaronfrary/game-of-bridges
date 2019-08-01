(ns bridges.game.line)

;;;; Functions pertaining to the "line" and "point" interfaces

;;; point := { :x <int>, :y <int> }
;;; line  := { :fst <point>, :snd <point> }

(defn- line
  ([{:keys [fst snd]}]            (line fst snd))
  ([{x1 :x y1 :y} {x2 :x y2 :y}]  (line x1 y1 x2 y2))
  ([x1 y1 x2 y2]                  {:fst {:x x1 :y y1}
                                   :snd {:x x2 :y y2}}))

(defn- line* [arg-list] (apply line arg-list))

(defn- make-edge-getter [choice-fn xy-key]
  (fn [ln] (choice-fn (get-in ln [:fst xy-key])
                      (get-in ln [:snd xy-key]))))

(def ^:private left   (make-edge-getter min :x))
(def ^:private right  (make-edge-getter max :x))
(def ^:private bottom (make-edge-getter min :y))
(def ^:private top    (make-edge-getter max :y))

(defn- intersecting? [ln-1 ln-2]
  (or (and (<= (left   ln-1) (left   ln-2) (right ln-2) (right ln-1))
           (<= (bottom ln-2) (bottom ln-1) (top   ln-1) (top   ln-2)))
      (and (<= (left   ln-2) (left   ln-1) (right ln-1) (right ln-2))
           (<= (bottom ln-1) (bottom ln-2) (top   ln-2) (top   ln-1)))
      (and (= (top ln-1) (bottom ln-1) (top ln-2) (bottom ln-2))
           (<= (left  ln-1) (right ln-2))
           (>= (right ln-1) (left  ln-2)))
      (and (= (left ln-1) (right ln-1) (left ln-2) (right ln-2))
           (<= (top    ln-1) (bottom ln-2))
           (>= (bottom ln-1) (top    ln-2)))))

(defn- clip-ends
  ([ln] (clip-ends 1 ln))
  ([n ln]
   (let [l (left ln), r (right ln), b (bottom ln), t (top ln)]
     (cond ((complement neg?) (- r l n n)) (line (+ l n) b (- r n) t)
           ((complement neg?) (- t b n n)) (line l (+ b n) r (- t n))
           :else ln))))

(defn get-blocked [lines & ln-args]
  (some #(and (intersecting? (clip-ends 1 (line* ln-args)) %) %)
        lines))

(defn direction [{x1 :x y1 :y} {x2 :x y2 :y}]
  (cond (and (= x1 x2) (< y1 y2)) ::up
        (and (= x1 x2) (> y1 y2)) ::down
        (and (< x1 x2) (= y1 y2)) ::right
        (and (> x1 x2) (= y1 y2)) ::left))

(defn vertical? [{:keys [fst snd]}] (#{::up ::down} (direction fst snd)))
