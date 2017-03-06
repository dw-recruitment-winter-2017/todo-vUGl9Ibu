(ns dw-todo.features.add-todo
  (:require [clojure.test :refer :all]
            [ring.adapter.jetty :refer [run-jetty]]
            [clj-webdriver.taxi :refer :all]
            [dw-todo.handler :refer [make-app]]
            [datomic.api :as d]
            [clojure.tools.logging :refer [info error]]))

(def test-port 5744)
(def test-host "localhost")
(def test-base-url (str "http://" test-host ":" test-port "/"))
(def test-db-uri "datomic:mem://feature-spec")

(defn start-server []
  (d/create-database test-db-uri)
  (loop [server (run-jetty (make-app {:db-uri test-db-uri})
                           {:port test-port, :join? false})]
    (if (.isStarted server)
      server
      (recur server))))

(defn stop-server [server]
  (info "stopping server")
  (d/delete-database test-db-uri)
  (.stop server))

(defn with-server [t]
  (let [server (start-server)]
    (try
      (t)
      (catch Throwable e (error e "Exception raised in with-server"))
      (finally (stop-server server)))))

(defn start-browser []
  (set-driver! {:browser :phantomjs}))

(defn stop-browser []
  (info "stopping browser")
  (quit))

(defn with-browser [t]
  (start-browser)
  (try
    (t)
    (catch Throwable e (error e "Exception raised in with-browser"))
    (finally (stop-browser))))

(use-fixtures :once with-server with-browser)

(deftest adding-a-todo
  (to test-base-url)
  (is (.contains (text "h2") "Todo:"))
  (focus "input.new-todo")
  (let [todo-text "write some feature specs"]
    (testing "todo is added to the end of the list"
      (send-keys "input.new-todo" todo-text)
      (click "input[value='Add']")
      (is (not (nil? (find-element {:text todo-text})))))
    (testing "new todo is persisted and consistently sorted"
      (refresh)
      (is (not (nil? (find-element {:text todo-text})))))))
