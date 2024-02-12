(ns homecoming-server-status.lib.database.database-config)

(def schema
  {:date-time {:db/valueType :db.type/instant
               :db/cardinality :db.cardinality/one}

   :server {:db/valueType :db.type/ref
            :db/cardinality :db.cardinality/many}

   :shard-name {:db/valueType :db.type/keyword
                :db/cardinality :db.cardinality/one}

   :status {:db/valueType :db.type/keyword
            :db/cardinality :db.cardinality/one}

   :playing {:db/valueType :db.type/long
             :db/cardinality :db.cardinality/one}

   :queued {:db/valueType :db.type/long
            :db/cardinality :db.cardinality/one}

   :heroes {:db/valueType :db.type/long
            :db/cardinality :db.cardinality/one}

   :villains {:db/valueType :db.type/long
              :db/cardinality :db.cardinality/one}

   :maps {:db/valueType :db.type/long
          :db/cardinality :db.cardinality/one}})

(def directory
  "db")

#_(def config
  {:name "homecoming-server-status"
   :store {:backend :file :path "db"}
   :schema-flexibility :write
   :initial-tx schema})