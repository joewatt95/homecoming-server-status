(ns homecoming-server-status.lib.database.datalog-ruleset.player-stats
  (:require [datalog-rules.api :as rules]))

(def ruleset (rules/ruleset {}))

(rules/unirule ruleset
 "∀ ?shard ?num_playing ?date_time,
    is_the_most_populated_shard_with_players_as_of(?shard, ?num_playing, ?date_time) ←
      is_shard_as_of(?shard, ?date_time) ∧
      is_the_largest_such_that(
        ?num_playing, ?num_playing', (
          ∃ ?shard',
            is_shard_as_of(?shard', ?date_time) ∧
            playing(?shard', ?num_playing')
        )
      ) ∧
      playing(?shard, ?num_playing)"
 '[(is-the-most-populated-shard-with-players-as-of ?ruleset ?shard ?num-playing ?date-time)
   (is-shard-as-of ?shard ?date-time)
   ;; Note: Need to explicitly pass ruleset to inner queries in this way.
   ;; They don't inherit it implicitly like with data in the database.
   [(q '[:find (max ?num-playing)
         :in $ % ?date-time
         :where
         (is-shard-as-of ?shard' ?date-time)
         [?shard' :playing ?num-playing]]
       $ ?ruleset ?date-time)
    [[?num-playing]]]
   [?shard :playing ?num-playing]])