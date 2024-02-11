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
  (let [rewrite-servers-key
        (r/rewrite {:servers ?x & ?rest} {:server ?x & ?rest})
        tx-data' (rewrite-servers-key tx-data)]
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

(defn test-query! []
  (datahike/q '[:find ?date-time .
                :in $ %
                :where
                (is-the-latest-date-time ?date-time)]
              @@db-conn
              combined-ruleset))