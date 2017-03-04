(ns dw-todo.features.add-todo
  (:require [clojure.test :refer :all]
            [ring.adapter.jetty :refer [run-jetty]]
            [clj-webdriver.taxi :refer :all]
            [dw-todo.handler :refer [app]]))

(def test-port 5744)
(def test-host "localhost")
(def test-base-url (str "http://" test-host ":" test-port "/"))

(defn start-server []
  (loop [server (run-jetty app {:port test-port, :join? false})]
    (if (.isStarted server)
      server
      (recur server))))

(defn stop-server [server]
  (.stop server))

(defn with-server [t]
  (let [server (start-server)]
    (try
      (t)
      (finally (stop-server server)))))

(defn start-browser []
  (set-driver! {:browser :phantomjs}))

(defn stop-browser []
  (quit))

(defn with-browser [t]
  (start-browser)
  (try
    (t)
    (finally (stop-browser))))

(use-fixtures :once with-server with-browser)

;; TODO: finish this test. Server must be erroring because the value is being entered into the input
;;       but the new text is not in the todo list
(deftest adding-a-todo
  (to test-base-url)
  (is (.contains (text "h2") "Todo:"))
  (focus "input.new-todo")
  (send-keys "input.new-todo" "write some feature specs")
  (click "input[value='Add']")
  #_(Thread/sleep 500)
  #_(take-screenshot :file "fail.png")
  (let [elem (last (find-elements {:css ".todo-item"}))]
    (is (= "write some feature specs" (text elem)))))
