(ns bridges.views
  (:require
   [re-frame.core :as re-frame]
   [bridges.subs :as subs]
   [bridges.events :as events]
   [bridges.puzzles :as puzzles]
   [bridges.line :as line]
   ))

;;; Constants

; NOTE: Should coordinate with CSS.
(def board-scale 28)


;;; Helper functions

(defn abs [n] (max (- n) n))

(defn coord->px [x] (str (* board-scale x) "px"))

(defn island->key [{:keys [x y]}] (str x ":" y))

(defn bridge->key [{:keys [fst snd]}]
  (str (island->key fst) "::" (island->key snd)))

(defn bridge-style [{:keys [fst snd] :as b}]
  (if (line/vertical? b)
    {:left (coord->px (:x fst))
     :top (coord->px (+ (min (:y fst) (:y snd)) 0.5))
     :height (coord->px (abs (- (:y fst) (:y snd))))}
    {:left (coord->px (+ (min (:x fst) (:x snd)) 0.5))
     :top (coord->px (:y fst))
     :width (coord->px (abs (- (:x fst) (:x snd))))}))


;;; Components

(defn island [{:keys [x y num] :as i}]
  (let [source-island @(re-frame/subscribe [::subs/source-island])
        target-island @(re-frame/subscribe [::subs/target-island])
        full-islands @(re-frame/subscribe [::subs/full-islands])
        selected-class (if (or (= source-island i) (= target-island i)) " island-selected" "")
        full-class (if (some #(= % i) full-islands) " island-full" "")]
    [:div {:class (str "island" selected-class full-class)
           :style {:left (coord->px x) :top (coord->px y)}
           :on-mouse-over #(re-frame/dispatch [::events/set-source-island i])}
     num]))

(defn make-island [i]
  ^{:key (island->key i)} [island i])

(defn bridge [{:keys [fst snd num] :as b}]
  [:div {:class (str "bridge " (if (line/vertical? b) "vertical" "horizontal"))
         :style (bridge-style b)
         :on-click #(re-frame/dispatch [::events/add-bridge b])}
   [:div.highlight-target]
   [:div {:class (str "line line-x" num)}]])

(defn make-bridge [b]
  ^{:key (bridge->key b)} [bridge b])

(defn islands []
  (let [islands @(re-frame/subscribe [::subs/islands])]
    [:<> (map make-island islands)]))

(defn bridges []
  (let [bridges @(re-frame/subscribe [::subs/bridges])]
    [:<> (map make-bridge bridges)]))

(defn potential-bridges []
  (let [bridges @(re-frame/subscribe [::subs/potential-bridges])]
    [:<> (map make-bridge bridges)]))

(defn game-board []
  (let [{:keys [width height]} @(re-frame/subscribe [::subs/board-size])]
    [:div#game-board {:style {:width (coord->px width) :height (coord->px height)}}
     [bridges]
     [potential-bridges]
     [islands]]))

(defn puzzle-status []
  (let [solved @(re-frame/subscribe [::subs/game-won])]
    [:div#puzzle-status (when solved "Puzzle solved!")]))

(defn puzzle-select-button [puzzle-string text]
  [:button.puzzle-select
   {:on-click #(re-frame/dispatch [::events/select-puzzle puzzle-string])}
   text])

(defn puzzle-input []
  (let [puzzle-string @(re-frame/subscribe [::subs/puzzle-string])
        {:keys [width height]} @(re-frame/subscribe [::subs/board-size])]
    [:textarea#puzzle-input {:value puzzle-string :rows height :cols width}]))

(defn main-panel []
  [:div#main-panel
   [:h1 "Bridges"]
   [:div#wrapper
    [:div#board-panel
     [game-board]
     [puzzle-status]]
    [:div#puzzle-select-panel
     [:h2 "Select a puzzle"]
     [puzzle-select-button puzzles/example-9x7 "Example 9x7 Puzzle"]
     [puzzle-select-button puzzles/example-10x10 "Example 10x10 Puzzle"]
     [puzzle-select-button puzzles/example-25x25 "Example 25x25 Puzzle"]
     [puzzle-input]]]])
