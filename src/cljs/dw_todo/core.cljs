(ns dw-todo.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [dw-todo.todo-view :refer [todo-page]]))

;; -------------------------
;; Views

(defn about-page []
  [:div.page-content
   [:h2 "About dw-todo"]
   [:p
    "Submission for the Democracy Works Anonymous Coding Exercise. Base project template is "
    [:a {:href "https://github.com/reagent-project/reagent-template"} "Reagent Template"]]
   [:p
    "This code is deployed to "
    [:a {:href "http://fathomless-savannah-40879.herokuapp.com"} "Heroku"]]
   [:p [:a {:href "/"} "Back to Todo List"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'todo-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
