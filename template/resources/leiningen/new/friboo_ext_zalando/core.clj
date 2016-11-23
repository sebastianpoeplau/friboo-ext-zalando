(ns {{namespace}}.core
  (:require [com.stuartsierra.component :as component]
            [org.zalando.stups.friboo.zalando-internal.config :as config]
            [org.zalando.stups.friboo.system :as system]
            [org.zalando.stups.friboo.log :as log]
            [org.zalando.stups.friboo.zalando-internal.system.http :as http]
            [org.zalando.stups.friboo.system.mgmt-http :as mgmt-http]
            [org.zalando.stups.friboo.system.db :as db]
            [org.zalando.stups.friboo.system.metrics :as metrics]
            [org.zalando.stups.friboo.zalando-internal.auth :as auth]
            [{{namespace}}.api :as api])
  (:gen-class))

(def default-http-config
  {:http-port 8080})

(def default-db-config
  {:db-classname       "org.postgresql.Driver"
   :db-subprotocol     "postgresql"
   :db-subname         "//localhost:5432/{{name}}"
   :db-user            "postgres"
   :db-password        "postgres"
   :db-init-sql        "SET search_path TO {{db-prefix}}_data, public"
   :db-auto-migration? true})

(def default-api-config
  {:api-example-param "bar"})

(defn run
  "Initializes and starts the whole system."
  [default-config]
  true
  (let [config (config/load-config
                 (merge default-db-config
                        default-http-config
                        default-api-config
                        default-config)
                 [:http :db :api :metrics :mgmt-http :auth])
        system (component/map->SystemMap
                 {:http      (component/using
                               (http/make-zalando-http
                                 "api.yaml"
                                 (:http config)
                                 (-> config :global :tokeninfo-url))
                               {:controller :api
                                :metrics    :metrics})
                  :api       (component/using
                               (api/map->Controller {:configuration (:api config)})
                               [:db :auth])
                  :auth      (auth/map->Authorizer {:configuration (:auth config)})
                  :db        (db/map->DB {:configuration (:db config)})
                  :metrics   (metrics/map->Metrics {:configuration (:metrics config)})
                  :mgmt-http (component/using
                               (mgmt-http/map->MgmtHTTP {:configuration (:mgmt-http config)})
                               [:metrics])})]
    (system/run config system)))

(defn -main
  "The actual main for our uberjar."
  [& args]
  (try
    (run {})
    (catch Exception e
      (log/error e "Could not start system because of %s." (str e))
      (System/exit 1))))
