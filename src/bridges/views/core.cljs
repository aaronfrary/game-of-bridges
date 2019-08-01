(ns bridges.views.core
  (:require
   [re-frame.core :as rf]
   [bridges.subs :as subs]
   [bridges.events :as events]
   [bridges.game.puzzles :as puzzles]
   [bridges.views.board :as board]))


;;; Components

(defn solver-buttons []
  (let [solving @(rf/subscribe [::subs/solve])]
     [:div#solver-button-segment {:class (if solving "solving" "")}
      [:button.solver.hint-button
       {:on-click #(rf/dispatch [::events/toggle-hint])
        :disabled solving}
       "Show Hint"]
      [:button.solver.solve-button
       {:on-click #(rf/dispatch [::events/solve-puzzle])}
       "Solve"]]))

(defn puzzle-status []
  [:div#puzzle-status
   (cond
     @(rf/subscribe [::subs/game-won]) "Puzzle solved!")
     @(rf/subscribe [::subs/solver-failed]) "Could not compute next move."])

(defn puzzle-select-button [puzzle-string text]
  [:button.puzzle-select
   {:on-click #(rf/dispatch [::events/select-puzzle puzzle-string])}
   text])

(defn puzzle-input []
  (let [{:keys [width height]} @(rf/subscribe [::subs/board-size])]
    [:textarea#puzzle-input
     {:value @(rf/subscribe [::subs/puzzle-string])
      :on-change #(rf/dispatch [::events/select-puzzle (-> % .-target .-value)])
      :rows (max height 12)
      :cols (max width 24)}]))

(defn main-panel []
  [:div#main-panel
   {:on-click #(rf/dispatch [::events/set-source-island nil])}
   [:h1 "Bridges"]
   [:div#wrapper
    [:div#board-panel
     [solver-buttons]
     [board/game-board]
     [puzzle-status]]
    [:div#puzzle-select-panel
     [:h2 "Select a puzzle"]
     [puzzle-select-button puzzles/example-9x7 "Example 9x7 Puzzle"]
     [puzzle-select-button puzzles/example-10x10 "Example 10x10 Puzzle"]
     [puzzle-select-button puzzles/example-25x25 "Example 25x25 Puzzle"]]
    [:div#puzzle-input-panel
     [:h2 "...or create your own."]
     [puzzle-input]]]])
