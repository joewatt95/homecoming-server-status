(ns homecoming-server-status.lib.web-scraper.core
  (:require [camel-snake-kebab.core :as csk]
            [meander.epsilon :as m]
            [meander.strategy.epsilon :as r]
            [tick.core :as time])
  (:import [java.time.format DateTimeFormatter]
           [java.util Date]))

(def resp-body->shards
  (let [str->keyword csk/->kebab-case-keyword]
    (r/pipe
     (r/rewrites
      ;; Extract deeply nested shard data to the top-most evaluation context.
      ;; (Here we take every HTML element to be a valid evaluation context.)
      ;;
      ;;   E ≠ [·]
      ;; ----------------------------------------------------
      ;; E[<div data-shard = ?shard> ... ?div_i ... </div>]
      ;; ⟶₁ <div data-shard = ?shard> ... ?div_i ... </div>
      ;;
      ;;  (str-keyword ?shard) = ?shard'
      ;; -----------------------------------------------------------------------------------------
      ;; <div data-shard = ?shard> ... ?div_i ... </div>
      ;; ⟶₂ (make shard status [?shard' (keep some in [... (convert ?div_i to key value pair) ...] then convert to map)])
      (m/$ [:div {:data-shard (m/some ?shard)} . !divs ...])
      (make shard status
            ~(str->keyword ?shard)
            (keep some? in [(convert !divs to key value pair) ...] then convert to map)))

     ;; Bottom-up rewriting to restrict small step evaluation to apply only
     ;; in call-by-value contexts.
     (r/bottom-up
      (r/rewrite
       ;; (str-keyword ?k) ⇓ ?k'
       ;; ------------------------------------------------------------------------------------------
       ;; (convert <div class="ipsGrid_span1 shard-status"> <span style = ?sty> ?val </span> </div>
       ;;  to key value pair)
       ;; ⟶₃ [?k' ?val]
       ;;
       ;; (str "ipsGrid_span1 shard-" ?k) ⇓ ?x    (parse-long ?val) ⇓ ?val'
       ;; (str-keyword ?k) ⇓ ?k'                              ?val'∈ Z
       ;; -------------------------------------------------------------------------
       ;; (convert <div class = ?x> ?val </div> to key value pair) ⟶₃ [?k' ?val']
       (convert [:div {:class (m/some (m/re #"^ipsGrid_span1 shard-(.+)$"
                                            [_ ?k]))}
                 (m/or (m/and (m/guard (= ?k "status"))
                              [_span _style (m/app str->keyword ?val)])
                       (m/and (m/guard (not= ?k "status"))
                              (m/app parse-long (m/some ?val))))]
                to key value pair)
       [~(str->keyword ?k) ?val]

       ;; ∄ ?k ≠ "", (str "ipsGrid_span1 shard-" ?k) ⇓ ?x
       ;; ------------------------------------------------------------
       ;; (convert <div class=?x>?val</div> to key value pair) ⟶₃ nil
       (convert _ to key value pair) nil

       ;; ----------------------------------------------------------------------
       ;; (keep some? in ... [?k_0 ?v_0] ... [?k_n ?v_n] ... then convert to map)
       ;; ⟶₄ {?k_0 ?v_0 ... ?k_n ?v_n}
       (keep some? in (m/gather (m/some !xs)) then convert to map) {& [!xs ...]}

       ;; -----------------------------------------------------
       ;; (make shard status ?shard {?k_0 ?v_0 ... ?k_n ?v_n})
       ;; ⟶₅ {:shard-name ?shard ?k_0 ?v_0 ... ?k_n ?v_n}
       (make shard status ?shard ?status) {:shard-name ?shard & ?status}

       ?x ?x)))))

(defn- parse-date-time-str [date-time-str]
  (let [date-time-formatter
        (time/formatter (. DateTimeFormatter -RFC_1123_DATE_TIME))]
    (-> date-time-str
        (time/parse-zoned-date-time date-time-formatter)
        time/instant
        Date/from)))

(defn transform-http-resp [http-resp]
  (m/match http-resp
    {:headers {"date" ?date-time} :body ?body}
    {:date-time (parse-date-time-str ?date-time)
     :shards (-> ?body resp-body->shards set)}))