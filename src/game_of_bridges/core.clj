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

;;; Mutable game state
(def islands-atom (atom []))
(def bridges-atom (atom []))
(def source-island-atom (atom nil))
(def click-atom (atom false)) ; HACK

(defn draw "Main game loop." []
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
    (doseq [i islands] (draw-island i))))

(defn new-game! [new-islands]
  (reset! bridges-atom [])
  (reset! islands-atom new-islands))

(defn -main [& args]
  (sketch
    :title "Game of Bridges"
    :setup setup
    :draw draw
    :mouse-released (fn [] (reset! click-atom true))
    :size [640 480])
  ;; Sample setup for testing
  (new-game! [{:x 1 :y 1 :num 3} {:x 4 :y 1 :num 5}
              {:x 7 :y 1 :num 2} {:x 2 :y 2 :num 1}
              {:x 6 :y 2 :num 1} {:x 1 :y 3 :num 4}
              {:x 4 :y 4 :num 5} {:x 7 :y 4 :num 1}
              {:x 2 :y 6 :num 1} {:x 1 :y 7 :num 3}
              {:x 4 :y 7 :num 5} {:x 6 :y 7 :num 3}])
  :ok)

