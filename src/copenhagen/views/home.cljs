(ns copenhagen.views.home
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame :refer [subscribe dispatch]]))

(defn home-page
  []
  [:div.home-page
   [:p {:class ["text-4xl"]} 
    "Hello World!"]])