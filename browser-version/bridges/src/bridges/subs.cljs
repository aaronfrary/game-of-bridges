(ns bridges.subs
  (:require
   [re-frame.core :as re-frame]))

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
