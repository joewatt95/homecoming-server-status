(ns homecoming-server-status.lib.database.datalog-ruleset.date-time
  (:require [datalog-rules.api :as rules]))

(def ruleset (rules/ruleset {}))

(rules/unirule ruleset
  "∀ ?shard ?date_time,
      is_the_earliest_date_time(?date_time) ←
      is_the_smallest_such_that(
        ?date_time, ?date_time', (∃ ?state, date_time(?state, ?date_time'))
      )"
  '[(is-the-earliest-date-time ?date-time)
    [(q '[:find (min ?date-time)
          :where [_ :date-time ?date-time]]
        $) [[?date-time]]]])

(rules/unirule ruleset
  "∀ ?shard ?date_time,
     is_the_earliest_date_time(?date_time) ←
     is_the_largest_such_that(
       ?date_time, ?date_time', (∃ ?state, date_time(?state, ?date_time'))
     )"
  '[(is-the-latest-date-time ?date-time)
    [(q '[:find (max ?date-time)
          :where [_ :date-time ?date-time]]
        $) [[?date-time]]]])

(rules/unirule ruleset
  "∀ ?shard ?date_time,
     is_shard_as_of(?shard, ?date_time) ←
       ∃ ?state, (
          date_time(?state, ?date_time) ∧
          shard(?state, ?shard)
       )"
  '[(is-shard-as-of ?shard ?date-time)
    [?state :date-time ?date-time]
    [?state :shard ?shard]])

(rules/unirule ruleset
  "∀ ?shard,
     is_shard_at_some_point_in_time(?shard) ←
       ∃ ?date_time,
         is_shard_as_of_date_time(?shard, ?date_time)"
  '[(is-shard-at-some-point-in-time ?shard)
    (is-shard-as-of ?shard _)])