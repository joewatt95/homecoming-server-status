(ns homecoming-server-status.lib.database.datalog-ruleset.core
  (:require [datalog-rules.api :as rules]
            [homecoming-server-status.lib.database.datalog-ruleset.date-time :as date-time-rules]))

(def ruleset
  (->> [date-time-rules/rules]
       (eduction (mapcat rules/rules))
       (into [])))