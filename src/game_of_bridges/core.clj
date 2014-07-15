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
  (:require [quil.core :as q]
            [game-of-bridges.graphics :as g]
            [game-of-bridges.io :as io]
            [game-of-bridges.logic :as l])
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
                ["No" q/exit])))

(defn menu [title & items]
  (fn []
    (let [margin 1
          s-width  (g/px->coord (q/width))
          s-height (g/px->coord (q/height))
          menu-width (- s-width  (* margin 2))
          menu-height (+ (count items) 2) #_(- s-height (* margin 2))
          x-center (q/round (/ s-width 2))
          mouse-y (:y (g/get-mouse)) 
          clicked @click-atom]
      (reset! click-atom false)
      (g/draw-menu-box margin menu-width menu-height)
      (g/draw-text title x-center (inc margin))
      (loop [depth (+ margin 2)
             [[item-name callback] & items] items]
        (when (= mouse-y depth)
          (g/hilight-menu-item x-center depth menu-width)
          (when clicked (callback)))
        (g/draw-text item-name x-center depth)
        (when (seq items) (recur (inc depth) items))))))

(defn play-game "Main game loop." []
  (let [islands @islands-atom
        bridges @bridges-atom
        source-island @source-island-atom
        clicked @click-atom
        {:keys [x y]} (g/get-mouse)
        island (l/get-island-at x y islands)
        bridge (l/get-bridge-at x y islands bridges)]
    (reset! click-atom false)
    (g/clear-screen)
    ;; Conditional hilighting
    (cond bridge
            (if clicked
              (swap! bridges-atom l/inc-bridge bridge)
              (g/hilight-bridge bridge))
          island
            (do (reset! source-island-atom island)
                (g/hilight-island island)
                (doseq [i (l/neighbors island islands bridges)]
                       (g/hilight-island i)))
          source-island
            (if-let [target-island (l/get-target x y source-island islands bridges)]
              (if clicked
                (swap! bridges-atom l/add-bridge source-island target-island)
                (g/hilight-bridge {:fst source-island :snd target-island}))
              (reset! source-island-atom nil)))
    ;; Draw the rest
    (doseq [i (filter (partial l/full? bridges) islands)]
      (g/hilight-full-island i))
    (doseq [b bridges] (g/draw-bridge b))
    (doseq [i islands] (g/draw-island i))
    (when (l/game-won? islands bridges) (win-game))))

(defn -main [& args]
  (if-let [file-name (first args)]
    (let [islands (io/read-puzzle file-name)]
      (q/sketch
        :title "Game of Bridges"
        :setup (partial g/setup islands)
        :draw (fn [] (@screen-atom))
        :mouse-released (fn [] (reset! click-atom true))
        :size (g/puzzle-size islands))
      (new-game! islands)
      :ok)
    :missing-filename))

(defn -test-run []
  (-main "resources/puzzles/test-puzzle.txt"))

