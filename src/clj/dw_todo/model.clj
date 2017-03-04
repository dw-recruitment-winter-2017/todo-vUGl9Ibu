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

(defn create-todo
  "Create a new todo item, returning a map representation of the entity"
  [conn params]
  (let [filtered-params (select-keys params [:todo/description :todo/complete])
        tempid (d/tempid :db.part/user)
        attributes (assoc filtered-params :todo/id (d/squuid) :db/id tempid)
        result @(d/transact conn [attributes])
        db (:db-after result)]
    (d/pull db
            [:todo/id :todo/description :todo/complete]
            (d/resolve-tempid db (:tempids result) tempid))))

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


  (create-todo conn {:todo/description "more stuff"})

  (all-todos (d/db conn))
  )
