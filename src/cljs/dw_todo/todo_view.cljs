(ns dw-todo.todo-view
  (:require [reagent.core :as reagent :refer [atom]]
            [accountant.core :as accountant]
            [ajax.core :refer [GET POST PUT]]
            [clojure.string :refer [blank?]]))

(defonce todos (atom []))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn fetch-todos []
  (let [handler (fn [response]
                  (reset! todos response))]
    (GET "/api/todos" {:handler handler :error-handler error-handler})))

(defn todo-list []
  [:div#todo-list
   (map (fn [item]
          [:div.row.todo-item {:key (:todo/id item)}
           [:div.one.column
            [:input {:type :checkbox :name :todo/complete :value true :defaultChecked false}]]
           [:div.eleven.columns (:todo/description item)]])
        @todos)])

(defn add-todo [text]
  (when-not (blank? text)
    (POST "/api/todos"
          {:params {:todo/description text}
           :handler (fn [response]
                      (swap! todos conj response))
           :error-handler error-handler})))

(defn todo-form []
  (let [text (atom "")
        handle-submit
        (fn [e]
          (.preventDefault e)
          (add-todo @text)
          (reset! text ""))]
    (fn []
      [:div.row
       [:form {:on-submit handle-submit}
        [:div.ten.columns
         [:input.new-todo.u-full-width {:type "text"
                                        :placeholder "Add a new todo"
                                        :value @text
                                        :auto-focus true
                                        :on-change #(reset! text (.. % -target -value))}]]
        [:div.two.columns
         [:input {:type :submit :value "Add"}]]]])))

(defn todo-page []
  (fetch-todos)
  (fn []
    [:div.container
     [:h2 "Todo:"]
     [todo-list]
     [todo-form]
     [:div.row.much-space [:a {:href "/about"} "About this todo list"]]]))
