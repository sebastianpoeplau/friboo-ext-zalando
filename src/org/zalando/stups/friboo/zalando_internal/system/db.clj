(ns org.zalando.stups.friboo.zalando-internal.system.db
  (:require [com.stuartsierra.component :refer [Lifecycle]]
            [org.zalando.stups.friboo.config :refer [require-config]]
            [cheshire.generate]
            [com.netflix.hystrix.core :refer [defcommand]])
  (:import (org.postgresql.util PSQLException)))

(defn ignore-nonfatal-psqlexception
  "Do not close curcuits because PSQLException with non-fatal error was thrown."
  [e]
  (when (instance? PSQLException e)
    (when-not (.startsWith (.getMessage e) "FATAL:")
      "non-fatal postgresql message")))
