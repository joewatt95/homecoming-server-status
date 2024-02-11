(ns homecoming-server-status.lib.database.datalog-ruleset.core
  (:require [datalog-rules.api :as rules]
            [homecoming-server-status.lib.database.datalog-ruleset.date-time :as date-time-rules]))

(def combined-ruleset
  (->> [date-time-rules/ruleset]
       (eduction (mapcat rules/rules))
       (into [])))