(ns homecoming-server-status.lib.database.database-config)

(def ^:private schema
  [{:db/ident :date-time
    :db/valueType :db.type/instant
    :db/cardinality :db.cardinality/one}

   {:db/ident :shard
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many}

   {:db/ident :shard-name
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/one}

   {:db/ident :status
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/one}

   {:db/ident :playing
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one}

   {:db/ident :queued
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one}

   {:db/ident :heroes
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one}

   {:db/ident :villains
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one}

   {:db/ident :maps
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one}])

(def config
  {:name "homecoming-server-status"
   :store {:backend :file :path "db"}
   :schema-flexibility :write
   :initial-tx schema})