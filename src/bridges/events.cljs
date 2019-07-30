(ns bridges.events
  (:require
   [re-frame.core :as re-frame]
   [bridges.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   (db/reset-db db/default-puzzle)))

(re-frame/reg-event-db
 ::inc-bridge
 [(re-frame/path :board)]
 (fn [board [_ bridge]]
   (println "bridge clicked: " bridge)
   board))
