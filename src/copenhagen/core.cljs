(ns copenhagen.core
  (:require
   [reagent.core :as reagent]
   [reagent.dom :as reagent-dom]
   [re-frame.core :as re-frame]
   
   [copenhagen.handlers :as handlers]
   [copenhagen.views.home :as home]))

;; -----------------
;; Application Entry
;; -----------------

(defn init! []
  (re-frame/clear-subscription-cache!)
  (re-frame/dispatch-sync [:app/initialize]))

(defn mount! []
  (reagent-dom/render [home/home-page] (js/document.getElementById "app")))

(defn ^:export ^:dev/after-load -main []
  (mount!)
  (init!))

