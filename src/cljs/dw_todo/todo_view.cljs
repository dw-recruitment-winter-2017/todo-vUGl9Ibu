(ns dw-todo.todo-view
  (:require [reagent.core :as reagent :refer [atom]]
            [accountant.core :as accountant]
            [ajax.core :refer [GET POST PUT]]))

(defonce todos (atom []))


(defn handler [response]
  (reset! todos response))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn fetch-todos []
  (GET "/api/todos" {:handler handler :error-handler error-handler}))

(defn todo-list
  [{:keys [todos]}]
  [:ul
   (map (fn [item]
          [:li {:key (:todo/id item)} (:todo/description item)])
        @todos)])

(defn todo-page []
  (fetch-todos)
  (fn []
    [:div.page-content
     [:h2 "Todo:"]
     [todo-list {:todos todos}]
     [:div [:a {:href "/about"} "About this todo list"]]]))
