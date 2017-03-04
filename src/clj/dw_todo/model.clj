(ns dw-todo.model
  (:require [datomic.api :as d]))

(def schema
  [{:db/ident :todo/id
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity}

   {:db/ident :todo/description
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident :todo/complete
    :db/valueType :db.type/boolean
    :db/cardinality :db.cardinality/one}])

(defn load-schema
  [conn]
  (d/transact conn schema))

(defn all-todos
  ""
  [db]
  (mapv first
        (d/q '[:find (pull ?todo [:todo/id :todo/description :todo/complete])
               :where [?todo :todo/id]]
             db)))

(defn load-fixture-data [conn]
  (d/transact
   conn
   [{:todo/id (d/squuid) :todo/description "render todo items" :todo/complete false}
    {:todo/id (d/squuid) :todo/description "write feature test" :todo/complete false}
    {:todo/id (d/squuid) :todo/description "new todo item form" :todo/complete false}
    {:todo/id (d/squuid) :todo/description "mark todo as completed" :todo/complete false}
    {:todo/id (d/squuid) :todo/description "unmark todo as completed" :todo/complete false}
    {:todo/id (d/squuid) :todo/description "delete existing todos" :todo/complete false}]))

(comment
  (def uri "datomic:mem://dw-todo")
  (d/create-database uri)
  (def conn (d/connect uri))
  (load-schema conn)
  (d/transact
   conn
   [{:todo/id (d/squuid) :todo/description "render todo items" :todo/complete false}
    {:todo/id (d/squuid) :todo/description "write feature test" :todo/complete false}
    {:todo/id (d/squuid) :todo/description "new todo item form" :todo/complete false}
    {:todo/id (d/squuid) :todo/description "mark todo as completed" :todo/complete false}
    {:todo/id (d/squuid) :todo/description "unmark todo as completed" :todo/complete false}
    {:todo/id (d/squuid) :todo/description "delete existing todos" :todo/complete false}])

  (all-todos (d/db conn))
  )
