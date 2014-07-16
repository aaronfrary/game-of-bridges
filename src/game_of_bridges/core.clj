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
            [quil.middleware :as qm]
            [game-of-bridges.graphics :as g]
            [game-of-bridges.io :as io]
            [game-of-bridges.logic :as l])
  (:gen-class))

(declare play-game)
;(declare menu)

(defn new-game [new-islands]
  {:screen play-game
   :islands new-islands
   :bridges []
   :source nil
   :target nil})

(defn check-game-won [state]
  (if (l/game-won? state)
    (do (prn "win") (q/exit))
    #_(menu "Correct! Play again?"
          ["Yes" (partial new-game! @islands-atom)]
          ["No" q/exit])
    state))

#_(defn menu [title & items]
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
  [state mouse]
  (let [bridge (l/get-bridge-at state (g/mouse->coord mouse))]
    (check-game-won
      (cond bridge (update-in state [:bridges] l/inc-bridge bridge)
            (and (:source state) (:target state))
            (update-in state [:bridges] l/add-bridge state)))))

(defn game-draw
  "Main game draw loop."
  [{:keys [islands bridges source target] :as state}]
  (let [mouse (g/get-mouse)
        island (l/get-island-at state mouse)
        bridge (l/get-bridge-at state mouse)]
    (g/clear-screen)
    ;; Conditional hilighting
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

(defn -main [& args]
  (if-let [file-name (first args)]
    (let [islands (io/read-puzzle file-name)]
      (q/sketch
        :title "Game of Bridges"
        :middleware [qm/fun-mode]
        :setup (fn [] (g/setup islands) (new-game islands))
        :draw game-draw
        ;:draw (fn [state] ((:screen state) state event))
        :mouse-moved track-source-island
        :mouse-released game-click
        :size (g/puzzle-size islands))
      :ok)
    :TODO-help-text))

(defn -test-run []
  (-main "resources/puzzles/test-puzzle.txt"))

