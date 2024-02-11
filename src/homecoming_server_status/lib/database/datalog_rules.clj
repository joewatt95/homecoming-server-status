(ns homecoming-server-status.lib.database.datalog-rules)

#_(def is-the-earliest-date-time
  "∀ ?shard ?date_time,
     is_the_earliest_date_time(?date_time) ←
       ∃ ?state,
         (?state ⊧ date_time(?date_time)) ∧
         ~ ∃ ?state' ?date-time',
           (?state' ⊧ date_time(?date_time')) ∧
           ?date_time' < ?date_time"
  '[(is-the-earliest-date-time ?date-time)
    [_ :date-time ?date-time]
    (not [_ :date-time ?date-time']
         [(< ?date-time' ?date-time)])])

(def is-the-earliest-date-time
  "∀ ?shard ?date_time,
     is_the_earliest_date_time(?date_time) ←
     is_the_smallest_such_that(
       ?date_time, ?date_time', (∃ ?state, ?state ⊧ date_time(date_time'))
     )"
  '[(is-the-earliest-date-time ?date-time)
    [(q '[:find (min ?date-time)
          :where [_ :date-time ?date-time]]
        $) [[?date-time]]]])

#_(def is-the-latest-date-time
  "∀ ?shard ?date_time,
     is_the_latest_date_time(?date_time) ←
       ∃ ?state,
         (?state ⊧ date_time(?date_time))) ∧
         ~ ∃ ?state' ?date_time',
           (?state' ⊧ date_time(?date_time')) ∧
           ?date_time' > ?date_time"
  '[(is-the-latest-date-time ?date-time)
    [_ :date-time ?date-time]
    (not [_ :date-time ?date-time']
         [(> ?date-time' ?date-time)])])

(def is-the-latest-date-time
  "∀ ?shard ?date_time,
     is_the_earliest_date_time(?date_time) ←
     is_the_largest_such_that(
       ?date_time, ?date_time', (∃ ?state, ?state ⊧ date_time(date_time'))
     )"
  '[(is-the-latest-date-time ?date-time)
    [(q '[:find (max ?date-time)
          :where [_ :date-time ?date-time]]
        $) [[?date-time]]]])

(def is-shard-as-of-date-time
  "∀ ?shard ?date_time,
     is_shard_as_of_date_time(?shard, ?date_time) ←
       ∃ ?server ?state,
         ?state ⊧ (
            date_time(?date_time) ∧
            server(?server) ∧
            shard_name(?server, ?shard)
         )"
  '[(is-shard-as-of-date-time ?shard ?date-time)
    [?state :date-time ?date-time]
    [?state :server ?server]
    [?server :shard-name ?shard]])

(def is-shard-at-some-point-in-time
  "∀ ?shard,
     is_shard_at_some_point_in_time(?shard) ←
       ∃ ?date_time,
         is_shard_as_of_date_time(?shard, ?date_time)"
  '[(is-shard-at-some-point-in-time ?shard)
    (is-shard-as-of-date-time ?shard _)])

(def rules
  [is-shard-as-of-date-time
   is-shard-at-some-point-in-time
   is-the-earliest-date-time
   is-the-latest-date-time])

;; (def rules
;;   (->> rule-set (apply concat) (into [])))

;; (println rules)