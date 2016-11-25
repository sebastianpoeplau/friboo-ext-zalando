(ns {{namespace}}.api
  (:require [org.zalando.stups.friboo.ring :refer :all]
            [org.zalando.stups.friboo.log :as log]
            [com.stuartsierra.component :as component]
            [org.zalando.stups.friboo.config :refer [require-config]]
            [ring.util.response :as r]
            [{{namespace}}.sql :as sql]))


(defrecord Controller [;; Set on creation
                       configuration
                       ;; Injected by the framework
                       db]
  component/Lifecycle
  (start [this]
    (log/info "Starting API controller.")
    this)
  (stop [this]
    (log/info "Stopping API controller.")
    this))

(defn get-all-memories
  "Returns a collection of all memories"
  [{:as this :keys [db configuration]} params request]
  (log/info "API example param is %s" (require-config configuration :example-param))
  (let [rows (sql/cmd-get-all-memories {} {:connection db})]
    (r/response rows)))

(defn get-memory
  "Returns one memory"
  [{:as this :keys [db]} {:keys [memory_id]} request]
  (if-let [row (first (sql/cmd-get-memory {:id memory_id} {:connection db}))]
    (r/response row)
    (r/not-found nil)))

(defn put-memory
  "Creates or updates a memory by ID"
  [{:as this :keys [db]} {:keys [memory_id memory]} request]
  (log/info "Writing a memory with ID %s" memory_id)
  (let [memory-data {:id   memory_id
                     :text (:text memory)}]
    (sql/cmd-create-or-update-memory! memory-data {:connection db}))
  (r/response nil))

(defn delete-memory
  [{:as this :keys [db]} {:keys [memory_id]} request]
  (log/info "Deleting a memory with ID %s" memory_id)
  (sql/cmd-delete-memory! {:id memory_id} {:connection db})
  (r/response nil))
