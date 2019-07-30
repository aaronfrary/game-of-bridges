(ns bridges.subs
  (:require
   [re-frame.core :as re-frame]
   [bridges.logic :as l]
   ))


;;; Simple subscriptions (re-frame Layer 2)

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
