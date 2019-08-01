(ns bridges.views.board
  (:require
   [re-frame.core :as rf]
   [bridges.subs :as subs]
   [bridges.events :as events]
   [bridges.game.line :as line]))

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
  (let [source-island @(rf/subscribe [::subs/source-island])
        target-island @(rf/subscribe [::subs/target-island])
        full-islands @(rf/subscribe [::subs/full-islands])
        selected-class (if (or (= source-island i) (= target-island i)) " island-selected" "")
        full-class (if (some #(= % i) full-islands) " island-full" "")]
    [:div {:class (str "island" selected-class full-class)
           :style {:left (coord->px x) :top (coord->px y)}
           :on-mouse-over #(rf/dispatch [::events/set-source-island i])}
     num]))

(defn make-island [i]
  ^{:key (island->key i)} [island i])

(defn bridge [{:keys [fst snd num] :as b}]
  [:div {:class (str "bridge " (if (line/vertical? b) "vertical" "horizontal"))
         :style (bridge-style b)
         :on-click #(rf/dispatch [::events/add-bridge b])}
   [:div.highlight-target]
   [:div {:class (str "line line-x" num)}]])

(defn make-bridge [b]
  ^{:key (bridge->key b)} [bridge b])

(defn hint []
  (if-let [hint @(rf/subscribe [::subs/hint])]
    [:div.hint-wrapper [bridge (assoc hint :num 0)]]))

(defn islands []
  (let [islands @(rf/subscribe [::subs/islands])]
    [:<> (map make-island islands)]))

(defn bridges []
  (let [bridges @(rf/subscribe [::subs/bridges])]
    [:<> (map make-bridge bridges)]))

(defn potential-bridges []
  (let [bridges @(rf/subscribe [::subs/potential-bridges])]
    [:<> (map make-bridge bridges)]))

(defn game-board []
  (let [{:keys [width height]} @(rf/subscribe [::subs/board-size])]
    [:div#game-board {:style {:width (coord->px width) :height (coord->px height)}}
     [potential-bridges]
     [hint]
     [bridges]
     [islands]]))
