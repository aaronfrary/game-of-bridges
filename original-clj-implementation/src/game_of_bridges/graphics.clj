(ns game-of-bridges.graphics
  (:require [quil.core :refer :all]
            [game-of-bridges.line :as line]))

(def master-scale 32)
(def minimum-screen-size 10)
(def text-scale 0.5)
(def text-bump 0.05)
(def island-scale 1)
(def hi-scale 1.15)
(def bridge-weight 2)
(def bridge-hi-weight 8)
(def double-bridge-hi-weight 13)
(def double-bridge-sep 6)
(def menu-item-hi-weight 26)
(def toolbar-height (* master-scale 2))

(defn bg-color []        (color 50))
(defn fg-color []        (color 255))
(defn accent-color []    (color 200))
(defn island-hi-color [] (color 120 120 180))
(defn bridge-hi-color [] (color 60 60 180))
(defn success-color []   (color 100 220 40))
(defn toolbar-color []   (color 80))
(defn button-hi-color [] (color 175))
(def button-color    accent-color)
(def island-color    fg-color)
(def line-color      accent-color)
(def bridge-color    line-color)
(def text-color      bg-color)
(def menu-color      fg-color)

(defmacro with-style [& body]
  `(do (push-style) ~@body (pop-style)))

(defn coord->px [x] (* master-scale x))

(defn px->coord [px] (quot (+ (/ master-scale 2) px) master-scale))

(defn to-scale [f & args] (apply f (map coord->px args)))

(defn mouse->coord [m]
  (-> m (update-in [:x] px->coord) (update-in [:y] px->coord)))

(defn get-mouse [] (mouse->coord {:x (mouse-x) :y (mouse-y)}))

(defn puzzle-max [dim islands]
  (if (seq islands)
    (->> islands (map dim) (apply max) (inc) (coord->px))
    (coord->px minimum-screen-size)))

(defn puzzle-size [islands]
  [(puzzle-max :x islands) (puzzle-max :y islands)])

(defn setup [islands]
  (ellipse-mode :center)
  (text-align :center :center)
  (to-scale text-size text-scale)
  (no-stroke))

(defn clear-screen [] (background (bg-color)))

(defn circle [x y scale]
  (to-scale ellipse x y scale scale))

(defn double-line
  ([x1 y1 x2 y2] (double-line x1 y1 x2 y2 double-bridge-sep))
  ([x1 y1 x2 y2 sep]
  (let [sep (/ sep 2)]
    (if (= x1 x2)
      (do (line (- x1 sep) y1 (- x2 sep) y2)
          (line (+ x1 sep) y1 (+ x2 sep) y2))
      (do (line x1 (- y1 sep) x2 (- y2 sep))
          (line x1 (+ y1 sep) x2 (+ y2 sep)))))))

(defn draw-text [s x y]
  (with-style (fill (text-color))
    (to-scale (partial text (str s)) x (- y text-bump))))

(defn draw-island [{:keys [x y num]}]
  (with-style (fill (island-color))
    (circle x y island-scale) (draw-text num x y)))

(defn hilight-island [{:keys [x y]}]
  (with-style (fill (island-hi-color))
    (circle x y hi-scale)))

(defn hilight-full-island [{:keys [x y]}]
  (with-style (fill (success-color))
    (circle x y hi-scale)))

(defn line-function-factory [line-fn color thickness]
  (fn [{{x1 :x y1 :y} :fst {x2 :x y2 :y} :snd}]
    (with-style (stroke (color)) (stroke-weight thickness)
      (to-scale line-fn x1 y1 x2 y2))))

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
(def hilight-hint
  (line-function-factory line success-color bridge-hi-weight))

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

(defn draw-menu-box [margin width height]
  (with-style (fill (menu-color))
    (stroke (accent-color)) (stroke-weight bridge-weight)
    (to-scale rect margin margin width height)))

(defn hilight-menu-item [x y w]
  ((line-function-factory line success-color menu-item-hi-weight)
     (line/line (- x (/ w 2) -1) y (+ x (/ w 2) -1) y)))

;;; Like the menu below, this toolbar is just sort of hacked together,
;;; since the user interface isn't the most important part of this
;;; project.

(defn on-hint-button? [{:keys [x y]}]
  (and (= y 1) (<= 1 x 3)))

(defn on-solve-button? [{:keys [x y]}]
  (and (= y 1) (<= 4 x 6)))

(defn draw-toolbar []
  (let [mouse (get-mouse)
        hint-color (if (on-hint-button? mouse) button-hi-color button-color)
        solve-color (if (on-solve-button? mouse) button-hi-color button-color)]
    (with-style (fill (toolbar-color))
      (stroke (accent-color)) (stroke-weight bridge-weight)
      (rect 0 0 (width) toolbar-height))
    (with-style (fill (hint-color))
      (to-scale rect 1 0.5 2 1) (draw-text "Hint" 2 1))
    (with-style (fill (solve-color))
      (to-scale rect 4 0.5 2 1) (draw-text "Solve" 5 1))))

;;; Menus!
;;; ...
;;; Not really sure where to put this, but it took up
;;; too much space in core. Can probably just get rid
;;; of this if we switch to ClojureScript.

(defn menu [title & items]
  (let [margin 1
        s-width  (px->coord (width))
        s-height (px->coord (height))
        menu-width (- s-width  (* margin 2))
        menu-height (+ (count items) 2) #_(- s-height (* margin 2))
        x-center (/ s-width 2)]
    {:draw (fn [state]
             (draw-menu-box margin menu-width menu-height)
             (draw-text title x-center (inc margin))
             (loop [depth (+ margin 2), [[item-name _] & items] items]
               (when (= (:y (get-mouse)) depth)
                 (hilight-menu-item x-center depth menu-width))
               (draw-text item-name x-center depth)
               (when (seq items) (recur (inc depth) items))))
     :click (fn [state event]
              (loop [depth (+ margin 2) [[_ callback] & items] items]
                (cond (= (:y (get-mouse)) depth) (callback)
                      (seq items) (recur (inc depth) items)
                      :else state)))}))

