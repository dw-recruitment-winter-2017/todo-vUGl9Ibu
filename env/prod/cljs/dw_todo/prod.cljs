(ns dw-todo.prod
  (:require [dw-todo.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
