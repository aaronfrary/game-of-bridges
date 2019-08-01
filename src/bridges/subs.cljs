(ns bridges.subs
  (:require
   [re-frame.core :as re-frame]
   [bridges.game.logic :as l]
   ))


;;; Simple subscriptions (re-frame Layer 2)

(re-frame/reg-sub
  ::puzzle-string
  (fn [db]
    (:puzzle-string db)))

(re-frame/reg-sub
  ::board-size
  (fn [db]
    (get-in db [:board :size])))

(re-frame/reg-sub
  ::islands
  (fn [db]
    (get-in db [:board :islands])))

(re-frame/reg-sub
  ::bridges
  (fn [db]
    (get-in db [:board :bridges])))

(re-frame/reg-sub
  ::source-island
  (fn [db]
    (get-in db [:board :source-island])))

(re-frame/reg-sub
  ::target-island
  (fn [db]
    (get-in db [:board :target-island])))


;;; Computed subscriptions (re-frame Layer 3)

(re-frame/reg-sub
  ::full-islands
  :<- [::islands]
  :<- [::bridges]
  (fn [[islands bridges] _]
    (filter (partial l/full? bridges) islands)))

(re-frame/reg-sub
  ::potential-bridges
  :<- [::islands]
  :<- [::bridges]
  :<- [::source-island]
  (fn [[islands bridges source] _]
    (->> (l/neighbors source bridges islands)
         (map #(identity {:fst source :snd % :num 0}))
         (filter #(not-any? (partial l/bridge= %) bridges)))))

(re-frame/reg-sub
  ::game-won
  :<- [::islands]
  :<- [::bridges]
  (fn [[islands bridges] _]
    (l/game-won? islands bridges)))
