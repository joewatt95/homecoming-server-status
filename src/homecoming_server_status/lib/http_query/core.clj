(ns homecoming-server-status.lib.http-query.core
  (:require [babashka.http-client :as http]
            [hickory.core :as hickory]
            [meander.epsilon :as m]))

(def ^:private server-status-url
  "https://forums.homecomingservers.com/status/")

(defn parse-as-hiccup [html-str]
  (-> html-str hickory/parse hickory/as-hiccup))

(defn get-server-status!
  ([] (get-server-status! server-status-url))

  ([server-status-url]
   (-> server-status-url
       http/get
       (m/rewrite
        {:body ?body & ?rest}
         {:body ~(parse-as-hiccup ?body) & ?rest}))))