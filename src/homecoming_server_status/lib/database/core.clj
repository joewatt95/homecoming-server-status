(ns homecoming-server-status.lib.database.core
  (:require [datahike.api :as datahike]
            [homecoming-server-status.lib.database.database-config :as db-config]
            [homecoming-server-status.lib.database.datalog-ruleset.core :refer [combined-ruleset]]
            [meander.strategy.epsilon :as r]))

(def db-conn ^:private (atom nil))

(defn setup-db! []
  (when-not (datahike/database-exists? db-config/config)
    (datahike/create-database db-config/config))
  (reset! db-conn (datahike/connect db-config/config)))

(defn transact! [tx-data]
  (let [rewrite-shards-key
        (r/rewrite {:shards ?x & ?rest} {:shard ?x & ?rest})
        tx-data' (rewrite-shards-key tx-data)]
    (datahike/transact! @db-conn {:tx-data [tx-data']})))

(defn get-datoms! []
  (datahike/datoms @@db-conn :eavt))

(defn get-all-seen-shards! []
  (datahike/q '[:find ?shard
                :in $ %
                :where
                (is-shard-at-some-point-in-time ?shard)]
              @@db-conn
              combined-ruleset))

(defn get-most-populated-shard! []
  (datahike/q '[:find ?shard-name ?num-playing ?date-time
                :in $ % ?ruleset
                :where
                (is-the-latest-date-time ?date-time)
                (is-the-most-populated-shard-with-players-as-of ?ruleset ?shard ?num-playing ?date-time)
                [?shard :shard-name ?shard-name]]
              @@db-conn
              combined-ruleset
              combined-ruleset))