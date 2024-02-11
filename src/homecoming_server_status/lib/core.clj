(ns homecoming-server-status.lib.core 
  (:require [homecoming-server-status.lib.http-query.core :as http-query]
            [homecoming-server-status.lib.web-scraper.core :as web-scraper]
            [homecoming-server-status.lib.database.core :as database]))

(def get-server-status!
  (comp web-scraper/transform-http-resp
        http-query/get-server-status!))