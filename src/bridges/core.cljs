(ns bridges.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [bridges.events :as events]
   [bridges.views :as views]
   [bridges.config :as config]
   ))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
