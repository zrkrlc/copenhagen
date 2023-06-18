(ns copenhagen.handlers
  (:require [re-frame.core :as re-frame :refer [subscribe dispatch dispatch-sync]]))

(re-frame/reg-event-db
 :app/initialize
 (fn [db _] db))