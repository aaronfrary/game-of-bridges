(ns bridges.events
  (:require
   [re-frame.core :as re-frame]
   [bridges.db :as db]
   [bridges.game.logic :as l]
   [bridges.game.solver :as s]
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
  ::puzzle-input-change
  (fn [db [_ new-string]]
    (assoc db :puzzle-string new-string)))

(re-frame/reg-event-db
 ::set-source-island
 [(re-frame/path :board)]
 (fn [board [_ island]]
   (assoc board :source-island island)))

(re-frame/reg-event-db
 ::add-bridge
 (fn [db [_ bridge]]
   (-> db
       (update-in [:board :bridges] l/add-bridge bridge)
       (assoc-in [:board :hint] nil)
       (assoc :solver-failed false))))

(re-frame/reg-event-db
 ::toggle-hint
 (fn [db _]
   (if (get-in [:board :hint] db)
     (assoc-in db [:board :hint] nil)
     (if-let [move (s/next-move (:board db))]
       (assoc-in db [:board :hint] move)
       (assoc db :solver-failed true)))))

(re-frame/reg-event-fx
 ::solve-puzzle
 (fn [{:keys [db]} _]
   (if (:solve db)
     {:db (assoc db :solve false)}
     {:db (assoc db :solve true)
      :dispatch [::solve-next-step]})))

(re-frame/reg-event-fx
 ::solve-next-step
 (fn [{:keys [db]} _]
   (if (not (:solve db)) {:db db}
     (if-let [move (s/next-move (:board db))]
       {:db db
        :dispatch [::add-bridge move]
        :dispatch-later [{:ms 0 :dispatch [::solve-next-step]}]}
       {:db (assoc db :solve false :solver-failed true)}))))
