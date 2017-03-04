(ns dw-todo.handler
  (:require [compojure.core :refer [GET defroutes context]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [dw-todo.middleware :refer [wrap-middleware]]
            [config.core :refer [env]]
            [dw-todo.model :as model]
            [datomic.api :as d :refer [db]]
            [ring.middleware.format :refer [wrap-restful-format]]))

(def mount-target
  [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
    (head)
    [:body {:class "body-container"}
     mount-target
     (include-js "/js/app.js")]))

(defn todo-list [conn]
  {:status 200
   :body (model/all-todos (db conn))})

(defroutes routes
  (GET "/" [] (loading-page))
  (GET "/about" [] (loading-page))

  (context "/api" []
           (GET "/todos" {conn :db-conn}
                (todo-list conn)))

  (resources "/")
  (not-found "Not Found"))

(def db-uri "datomic:mem://dw-todo")

(defn wrap-database [handler uri]
  (let [newly-created? (d/create-database uri)
        conn (d/connect db-uri)]
    (when newly-created?
      (model/load-schema conn)
      (model/load-fixture-data conn))
    (fn [request]
      (handler (assoc request :db-conn conn)))))

(def app
  (-> (wrap-middleware #'routes)
      (wrap-database db-uri)
      (wrap-restful-format :formats [:transit-json :json-kw])))
