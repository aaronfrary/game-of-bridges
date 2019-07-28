(ns bridges.views
  (:require
   [re-frame.core :as re-frame]
   [bridges.subs :as subs]
   ))

;;; Constants

; NOTE: Should coordinate with CSS.
(def board-scale 28)


;;; Helper functions

(defn coord->px [x] (str (* board-scale x) "px"))


;;; Components

(defn island [{:keys [x y num]}]
  [:div.island {:style {:left (coord->px x) :top (coord->px y)}}
   num])

(defn make-island [{:keys [x y] :as i}]
  ^{:key (str x ":" y)} [island i])

(defn islands []
  (let [islands @(re-frame/subscribe [::subs/islands])]
    [:<> (map make-island islands)]))

(defn game-board []
  (let [{:keys [width height]} @(re-frame/subscribe [::subs/board-size])]
    [:div#game-board {:style {:width (coord->px width) :height (coord->px height)}}
     [islands]]))

(defn main-panel []
  [:div
   [:h1 "Bridges"]
   [game-board]])
