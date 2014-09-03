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
  (:use clojure.pprint)
  (:require [quil.core                :as q]
            [quil.middleware          :as qm]
            [clojure.pprint           :refer [pprint]]
            [game-of-bridges.graphics :as g]
            [game-of-bridges.io       :as io]
            [game-of-bridges.logic    :as l]
            [game-of-bridges.solver   :as s])
  (:gen-class))

(declare game-draw)
(declare game-click)

(defn new-game [new-islands] ; FIXME: Doesn't resize on new game 
  {:screen {:draw game-draw, :click game-click}
   :islands new-islands
   :bridges []
   :source nil
   :target nil
   :hint nil
   :solve false
   })

(defn check-game-won [state]
  (if (l/game-won? state)
    (let [puzzle-names (take 5 (io/puzzle-dir))]
      (->> puzzle-names
           (map #(partial new-game (io/read-puzzle %)))
           (map vector (map io/strip-name puzzle-names)) ; (map vector %) == (zip %)
           (reverse)
           (cons ["No thanks!" q/exit])
           (reverse)
           (apply (partial g/menu "Correct! Play again?"))
           (assoc state :screen)))
    state))

(defn track-source-island
  "TODO: docstring"
  [state mouse]
  (let [island (l/get-island-at state (g/mouse->coord mouse))]
    (cond island (-> state (assoc :source island) (assoc :target nil))
          (:source state) (assoc state :target
                                 (l/get-target state (g/mouse->coord mouse)))
          :else state)))

(defn game-click
  "TODO: docstring"
  [{:keys [source target hint] :as state}, mouse]
  (let [m (g/mouse->coord mouse)]
    (-> state
        (update-in [:bridges] l/add-bridge
          (some identity [(l/get-bridge-at state m)
                          (l/get-bridge-at (assoc state :bridges [hint]) m)
                          (and source target {:fst source :snd target})]))
        (assoc :hint nil)
        (check-game-won))))

(defn game-draw
  "Main game draw loop."
  [{:keys [islands bridges source target hint] :as state}]
  (let [mouse (g/get-mouse)
        island (l/get-island-at state mouse)
        bridge (l/get-bridge-at (update-in state [:bridges] conj hint) mouse)]
    (g/clear-screen)
    ;; Conditional hilighting
    (when hint (g/hilight-hint hint))
    (cond bridge (g/hilight-bridge bridge)
          island (do (g/hilight-island island)
                     (doseq [i (l/neighbors island state)]
                       (g/hilight-island i)))
          (and source target) (g/hilight-bridge {:fst source :snd target}))
    (doseq [i (filter (partial l/full? bridges) islands)]
      (g/hilight-full-island i))
    ;; Draw the rest
    (doseq [b bridges] (g/draw-bridge b))
    (doseq [i islands] (g/draw-island i))))

(defn hint [state]
  (assoc state :hint (if (:hint state) nil
                       (s/next-move state))))

(defn solve [state]
  (assoc state :solve true))

(defn -main [& args]
  (if-let [file-name (first args)]
    (let [islands (io/read-puzzle file-name)]
      (q/sketch
        :title "Game of Bridges"
        :middleware [qm/fun-mode]
        :setup (fn [] (g/setup islands) (new-game islands))
        :draw (fn [state] ((get-in state [:screen :draw]) state))
        :update (fn [state] (if (:solve state)
                              (if-let [b (s/next-move state)]
                                (update-in state [:bridges] l/add-bridge b) 
                                (assoc state :solve false))
                              state))
        :mouse-moved track-source-island
        :mouse-released (fn [state event]
                          ((get-in state [:screen :click]) state event))
        :key-typed (fn [state event]
                     (case (:key event)
                       :h (hint state)
                       :s (solve state)
                       state))
        :size (g/puzzle-size islands))
      :ok)
    :TODO-help-text))

(defn -test-run []
  (-main "resources/puzzles/test-puzzle-1.txt"))

