(ns {{namespace}}.sql
  (:require [yesql.core :refer [defqueries]]
            [org.zalando.stups.friboo.system.db :refer [generate-hystrix-commands]]
            [org.zalando.stups.friboo.zalando-internal.system.db :refer [ignore-nonfatal-psqlexception]]))

(defqueries "db/queries.sql")
;; Wrap every function already defined in this namespace with another one with `cmd-` name prefix
(generate-hystrix-commands :ignore-exception-fn? ignore-nonfatal-psqlexception)
