(ns homecoming-server-status.lib.database.datalog-ruleset.core
  (:require [datalog-rules.api :as rules]
            [homecoming-server-status.lib.database.datalog-ruleset.date-time :as date-time]
            [homecoming-server-status.lib.database.datalog-ruleset.player-stats :as player-stats]))

(def combined-ruleset
  (->> [date-time/ruleset
        player-stats/ruleset]
       (eduction (mapcat rules/rules))
       (into [])))