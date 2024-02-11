(ns homecoming-server-status.main
  "FIXME: my new org.corfield.new/scratch project."
  (:require [homecoming-server-status.lib.database.core :as database]
            [homecoming-server-status.lib.core :as lib])
  (:gen-class) 
  (:import [java.util Date]))

;; (defn favorite-food-info [foods-by-name user]
;;   (m/match {:user user
;;             :foods-by-name foods-by-name}
;;     {:user
;;      {:name ?name
;;       :favorite-food {:name ?food}}
;;      :foods-by-name {?food {:popularity ?popularity
;;                             :calories ?calories}}}
;;     {:name ?name
;;      :favorite {:food ?food
;;                 :popularity ?popularity
;;                 :calories ?calories}}))

;; (def foods-by-name
;;   {:nachos {:popularity :high
;;             :calories :lots}
;;    :smoothie {:popularity :high
;;               :calories :less}})

(defn exec
  "Invoke me with clojure -X homecoming-server-status.homecoming-server-status/exec"
  [opts]
  (println "exec with" opts))

(defn -main
  "Invoke me with clojure -M -m homecoming-server-status.homecoming-server-status"
  [& args]
  (database/setup-db!)
  (->> (lib/get-server-status!)
       ;; vector
       ;; (fs/write-lines (fs/path "out.edn"))
         database/transact!
         deref)
  (println (database/test-query!)))