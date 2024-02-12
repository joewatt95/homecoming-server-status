(ns homecoming-server-status.lib.database.core
  (:require [datalevin.core :as datalevin]
            [homecoming-server-status.lib.database.database-config :as db-config]
            [homecoming-server-status.lib.database.datalog-ruleset.core :refer [combined-ruleset]]
            [meander.strategy.epsilon :as r]))

(def db-conn ^:private (atom nil))

(defn setup-db! []
  (reset! db-conn (datalevin/get-conn db-config/directory db-config/schema)))

(defn transact-async! [tx-data]
  (let [rewrite-servers-key
        (r/rewrite {:servers ?x & ?rest} {:server ?x & ?rest})
        tx-data' (rewrite-servers-key tx-data)]
    (datalevin/transact-async @db-conn [tx-data'])))

(defn get-datoms! []
  (datalevin/datoms @@db-conn :eav))

(defn get-all-seen-shards! []
  (datalevin/q '[:find ?shard
                 :in $ %
                 :where
                 (is-shard-at-some-point-in-time ?shard)]
               @@db-conn
               combined-ruleset))

(defn test-query! []
  (datalevin/q '[:find ?date-time .
                 :in $ %
                 :where
                 (is-the-latest-date-time ?date-time)]
               @@db-conn
               combined-ruleset))