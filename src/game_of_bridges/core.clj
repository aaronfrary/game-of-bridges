;;;; Bridges: A Logic Puzzle Game

;;; Game state:
;;;
;;; island := { :x <int>
;;;             :y <int>
;;;             :num <int> }
;;;
;;; bridge := { :fst <island>
;;;             :snd <island>
;;;             :num <int> }
;;;
;;; Note:
;;;
;;; islands implement the "point" interface: { :x <int>, :y <int> }
;;;
;;; bridges implement the "line" interface: { :fst <point>, :snd <point> }

(ns game-of-bridges.core
  (:require [quil.core :refer :all]
            [game-of-bridges.graphics :refer :all]
            [game-of-bridges.logic :as logic])
  (:gen-class))

(declare play-game)
(declare menu)

;;; Mutable game state
(def screen-atom (atom play-game))
(def islands-atom (atom []))
(def bridges-atom (atom []))
(def source-island-atom (atom nil))
(def click-atom (atom false)) ; HACK

(defn new-game! [new-islands]
  (reset! bridges-atom [])
  (reset! islands-atom new-islands)
  (reset! source-island-atom nil)
  (reset! click-atom false)
  (reset! screen-atom play-game))

(defn win-game []
  (reset! screen-atom
          (menu "Correct! Play again?"
                ["Yes" (partial new-game! @islands-atom)]
                ["No" exit])))

(defn menu [title & items]
  (fn []
    (let [margin 2
          s-width  (px->coord (width))
          s-height (px->coord (height))
          menu-width (- s-width  (* margin 2))
          menu-height (+ (count items) 2) #_(- s-height (* margin 2))
          x-center (round (/ s-width 2))
          mouse-y (:y (get-mouse)) 
          clicked @click-atom]
      (reset! click-atom false)
      (draw-menu-box margin menu-width menu-height)
      (draw-text title x-center (inc margin))
      (loop [depth (+ margin 2)
             [[item-name callback] & items] items]
        (when (= mouse-y depth)
          (hilight-menu-item x-center depth menu-width)
          (when clicked (callback)))
        (draw-text item-name x-center depth)
        (when (seq items) (recur (inc depth) items))))))

(defn play-game "Main game loop." []
  (let [islands @islands-atom
        bridges @bridges-atom
        source-island @source-island-atom
        clicked @click-atom
        {:keys [x y]} (get-mouse)
        island (logic/get-island-at x y islands)
        bridge (logic/get-bridge-at x y islands bridges)]
    (reset! click-atom false)
    (clear-screen)
    ;; Conditional hilighting
    (cond bridge
            (if clicked
              (swap! bridges-atom logic/inc-bridge bridge)
              (hilight-bridge bridge))
          island
            (do (reset! source-island-atom island)
                (hilight-island island)
                (doseq [i (logic/neighbors island islands bridges)]
                       (hilight-island i)))
          source-island
            (if-let [target-island (logic/get-target x y source-island islands bridges)]
              (if clicked
                (swap! bridges-atom logic/add-bridge source-island target-island)
                (hilight-bridge {:fst source-island :snd target-island}))
              (reset! source-island-atom nil)))
    ;; Draw the rest
    (doseq [i (filter (partial logic/full? bridges) islands)]
      (hilight-full-island i))
    (doseq [b bridges] (draw-bridge b))
    (doseq [i islands] (draw-island i))
    (when (logic/game-won? islands bridges) (win-game))))

(defn -main [& args]
  (sketch
    :title "Game of Bridges"
    :setup setup
    :draw (fn [] (@screen-atom))
    :mouse-released (fn [] (reset! click-atom true))
    :size [640 480])
  ;; Sample setup for testing
  (new-game! [{:x 1 :y 1 :num 3} {:x 4 :y 1 :num 5}
              {:x 7 :y 1 :num 2} {:x 8 :y 2 :num 1}
              {:x 1 :y 3 :num 4} {:x 4 :y 4 :num 5}
              {:x 7 :y 4 :num 1} {:x 1 :y 7 :num 3}
              {:x 4 :y 7 :num 5} {:x 8 :y 7 :num 3}
              {:x 4 :y 9 :num 1} {:x 8 :y 9 :num 1}])
  :ok)

