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

(defn draw "Main game loop." []
  (let [islands @islands-atom
        bridges @bridges-atom
        {:keys [x y]} (get-mouse)]
    (clear-screen)
    (when-let [bridge (logic/get-bridge-at x y islands bridges)]
      (hilight-bridge bridge))
    (when-let [island (logic/get-island-at x y islands)]
      (when-let [neighbors (and ((comp not logic/full?) bridges island)
                                (logic/get-neighbors island islands bridges))]
        (hilight-island island)
        (doseq [i neighbors] (hilight-island i))))
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
    :size [640 480])
  ;; Sample setup for testing
  (new-game! [{:x 1 :y 1 :num 3}
              {:x 4 :y 1 :num 5}
              {:x 7 :y 1 :num 2}
              {:x 2 :y 2 :num 1}
              {:x 6 :y 2 :num 1}
              {:x 1 :y 3 :num 4}
              {:x 4 :y 4 :num 5}
              {:x 7 :y 4 :num 1}
              {:x 2 :y 6 :num 1}
              {:x 1 :y 7 :num 3}
              {:x 4 :y 7 :num 5}
              {:x 6 :y 7 :num 3}])
  (let [islands @islands-atom]
    (reset! bridges-atom [{:fst (nth islands 6) :snd (nth islands 7) :num 1}
                          {:fst (nth islands 6) :snd (nth islands 10) :num 1}
                          {:fst (nth islands 1) :snd (nth islands 2) :num 2}]))
  :ok)

