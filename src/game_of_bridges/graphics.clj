(ns game-of-bridges.graphics
  (:require [quil.core :refer :all]))

;;; TODO: Make this whole module less kludgy

(def master-scale 40)
(def text-scale 0.5)
(def text-bump 0.05)
(def island-scale 1)
(def island-hi-scale 1.15)
(def bridge-weight 2)
(def bridge-hi-weight 8)
(def double-bridge-hi-weight 13)
(def double-bridge-sep 6)

(defn bg-color []        (color 50))
(defn fg-color []        (color 255))
(defn accent-color []    (color 200))
(defn island-hi-color [] (color 120 120 180))
(defn bridge-hi-color [] (color 60 60 180))
(defn success-color []   (color 100 220 40))
(def island-color    fg-color)
(def line-color      accent-color)
(def bridge-color    line-color)
(def text-color      bg-color)

(defmacro to-scale [f & args]
  `(~f ~@(map (partial list '* master-scale) args)))

(defn setup []
  (ellipse-mode :center)
  (text-align :center :center)
  (to-scale text-size text-scale)
  (no-stroke))

(defn clear-screen [] (background (bg-color)))

(defn px->coord [px] (quot (+ (/ master-scale 2) px) master-scale))

(defn get-mouse [] {:x (px->coord (mouse-x)), :y (px->coord (mouse-y))})

(defn circle [x y scale]
  (to-scale ellipse x y scale scale))

(defn draw-num [x y n]
  (fill (text-color)) (to-scale (->> n (str) (partial text)) x (- y text-bump)))

(defn draw-island [{:keys [x y num]}]
  (fill (island-color)) (circle x y island-scale) (draw-num x y num))

(defn hilight-island [{:keys [x y]}]
  (fill (island-hi-color)) (circle x y island-hi-scale))

(defn hilight-full-island [{:keys [x y]}]
  (fill (success-color)) (circle x y island-hi-scale))

(defn double-line
  ([x1 y1 x2 y2] (double-line x1 y1 x2 y2 double-bridge-sep))
  ([x1 y1 x2 y2 sep]
  (let [sep (/ sep 2)]
    (if (= x1 x2)
      (do (line (- x1 sep) y1 (- x2 sep) y2)
          (line (+ x1 sep) y1 (+ x2 sep) y2))
      (do (line x1 (- y1 sep) x2 (- y2 sep))
          (line x1 (+ y1 sep) x2 (+ y2 sep)))))))

(defn line-function-factory [line-fn color thickness]
  (fn [{{x1 :x y1 :y} :fst {x2 :x y2 :y} :snd}]
    (stroke (color)) (stroke-weight thickness)
    (to-scale line-fn x1 y1 x2 y2)
    (no-stroke)))

(def single-bridge
  (line-function-factory line bridge-color bridge-weight))
(def double-bridge
  (line-function-factory double-line bridge-color bridge-weight))
(def hilight-single-bridge
  (line-function-factory line bridge-hi-color bridge-hi-weight))
(def hilight-double-bridge
  (line-function-factory line bridge-hi-color double-bridge-hi-weight))
(def hilight-potential-bridge
  (line-function-factory line island-hi-color bridge-hi-weight))

(defn hilight-bridge [{:keys [fst snd num] :as bridge}]
  (case num
    2 (hilight-double-bridge bridge)
    1 (hilight-single-bridge bridge)
    nil (hilight-potential-bridge bridge))
  (hilight-island fst)
  (hilight-island snd))

(defn draw-bridge [{:keys [num] :as bridge}]
  (case num
    1 (single-bridge bridge)
    2 (double-bridge bridge)))

