(ns homecoming-server-status.lib.database.database-config)

(def ^:private schema
  {:date-time {:db/valueType :db.type/instant
               :db/cardinality :db.cardinality/one
               :db/doc "Date-time of the world"}

   :shard {:db/valueType :db.type/ref
           :db/cardinality :db.cardinality/many
           :db/doc "Entity representing a Homecoming shard"}

   :shard-name {:db/valueType :db.type/keyword
                :db/cardinality :db.cardinality/one
                :db/doc "Name of a shard"}

   :status {:db/valueType :db.type/keyword
            :db/cardinality :db.cardinality/one
            :db/doc "Shard status"}

   :playing {:db/valueType :db.type/long
             :db/cardinality :db.cardinality/one
             :db/doc "Number of players currently online on a shard"}

   :queued {:db/valueType :db.type/long
            :db/cardinality :db.cardinality/one
            :db/doc "Number of players waiting in queue to enter a shard"}

   :heroes {:db/valueType :db.type/long
            :db/cardinality :db.cardinality/one
            :db/doc "Number of players on blueside maps"}

   :villains {:db/valueType :db.type/long
              :db/cardinality :db.cardinality/one
              :db/doc "Number of players on redside maps"}

   :maps {:db/valueType :db.type/long
          :db/cardinality :db.cardinality/one
          :db/doc "Number of active maps"}})

(def config
  {:name "homecoming-server-status"
   :store {:backend :file :path "db"}
   :schema-flexibility :write
   :initial-tx schema})