(ns {{namespace}}.api-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [{{namespace}}.api :refer :all]
            [{{namespace}}.test-utils :as tu]))

(deftest wrap-midje-facts

  ;; Example of a test involving only API + DB, no HTTP server
  (facts "about get-all-memories"
    (tu/with-db [db]
      (tu/wipe-db db)
      (fact "Throws when :example-param is not set in :configuration"
        (get-all-memories {:db db :configuration {}} nil nil)
        => (throws IllegalArgumentException))))

  )
