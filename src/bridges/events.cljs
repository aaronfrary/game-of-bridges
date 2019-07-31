(ns bridges.events
  (:require
   [re-frame.core :as re-frame]
   [bridges.db :as db]
   [bridges.logic :as l]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   (db/reset-db db/default-puzzle)))

(re-frame/reg-event-db
  ::select-puzzle
  (fn [_ [_ puzzle-string]]
    (db/reset-db puzzle-string)))

(re-frame/reg-event-db
  ::load-input-puzzle
  (fn [db _]
    (db/reset-db (:puzzle-string db))))

(re-frame/reg-event-db
 ::set-source-island
 [(re-frame/path :board)]
 (fn [board [_ island]]
   (assoc board :source-island island)))

(re-frame/reg-event-db
 ::add-bridge
 [(re-frame/path :board)]
 (fn [board [_ bridge]]
   (update-in board [:bridges] l/add-bridge bridge)))
