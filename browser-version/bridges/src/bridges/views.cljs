(ns bridges.views
  (:require
   [re-frame.core :as re-frame]
   [bridges.subs :as subs]
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


;;; Components

(defn island [{:keys [x y num]}]
  [:div.island {:style {:left (coord->px x) :top (coord->px y)}}
   num])

(defn make-island [i]
  ^{:key (island->key i)} [island i])

(defn v-bridge [{:keys [fst snd num]}]
  [:div.bridge.vertical
   {:style {:left (coord->px (:x fst))
            :top (coord->px (+ (min (:y fst) (:y snd)) 0.5))
            :height (coord->px (abs (- (:y fst) (:y snd))))}}
   [:div {:class (str "line line-x" num)}]])

(defn h-bridge [{:keys [fst snd num]}]
  [:div.bridge.horizontal
   {:style {:left (coord->px (+ (min (:x fst) (:x snd)) 0.5))
            :top (coord->px (:y fst))
            :width (coord->px (abs (- (:x fst) (:x snd))))}}
   [:div {:class (str "line line-x" num)}]])

(defn make-bridge [b]
  ^{:key (bridge->key b)} [(if (line/vertical? b) v-bridge h-bridge) b])

(defn islands []
  (let [islands @(re-frame/subscribe [::subs/islands])]
    [:<> (map make-island islands)]))

(defn bridges []
  (let [bridges @(re-frame/subscribe [::subs/bridges])]
    [:<> (map make-bridge bridges)]))

(defn game-board []
  (let [{:keys [width height]} @(re-frame/subscribe [::subs/board-size])]
    [:div#game-board {:style {:width (coord->px width) :height (coord->px height)}}
     [bridges]
     [islands]]))

(defn main-panel []
  [:div
   [:h1 "Bridges"]
   [game-board]])
