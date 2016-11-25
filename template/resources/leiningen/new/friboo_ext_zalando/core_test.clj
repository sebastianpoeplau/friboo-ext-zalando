(ns {{namespace}}.core-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [{{namespace}}.core :refer :all]
            [{{namespace}}.utils :as u]
            [{{namespace}}.test-utils :as tu]
            [clj-http.client :as http]
            [com.stuartsierra.component :as component]))

(deftest test-core-system

  (facts "about run"
    (let [dev-config  (u/load-dev-config)
          test-config (merge {:http-port      (u/get-free-port)
                              :mgmt-http-port (u/get-free-port)}
                             dev-config)
          system      (run test-config)
          port        (-> system :http :configuration :port)]
      (try
        (tu/wipe-db (-> system :db))
        (facts "In the beginning there are no memories"
          (http/get (str "http://localhost:" port "/memories") {:as :json})
          => (contains {:status 200 :body []}))
        (facts "Nonexistent memories yield 404"
          (http/get (str "http://localhost:" port "/memories/foo") {:throw-exceptions false})
          => (contains {:status 404}))
        (facts "Can create a memory"
          (http/put (str "http://localhost:" port "/memories/foo") {:content-type :json
                                                                    :form-params  {:text "bar"}})
          => (contains {:status 200}))
        (facts "Existing memory can be retrieved by ID"
          (http/get (str "http://localhost:" port "/memories/foo") {:as :json})
          => (contains {:status 200 :body {:id "foo" :text "bar"}}))
        (facts "Can change a memory"
          (http/put (str "http://localhost:" port "/memories/foo") {:content-type :json
                                                                    :form-params  {:text "baz"}}))
        (facts "Changed memory is now different"
          (http/get (str "http://localhost:" port "/memories/foo") {:as :json})
          => (contains {:status 200 :body {:id "foo" :text "baz"}}))
        (facts "Can delete a memory"
          (http/delete (str "http://localhost:" port "/memories/foo"))
          => (contains {:status 200}))
        (facts "Deleted memory yields 404"
          (http/get (str "http://localhost:" port "/memories/foo") {:throw-exceptions false})
          => (contains {:status 404}))
        (finally
          (component/stop system)))))

  )
