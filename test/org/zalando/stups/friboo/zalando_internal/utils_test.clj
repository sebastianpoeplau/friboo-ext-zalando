(ns org.zalando.stups.friboo.zalando-internal.utils-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [org.zalando.stups.friboo.zalando-internal.utils :refer :all]))

(def test-string "foobar")
(def sha256 "c3ab8ff13720e8ad9047dd39466b3c8974e592c2fa383d4a3960714caef0c4f2")

(deftest test-digest
         (facts "digest"
                (fact "creates sha-256 hash"
                      (digest test-string) => sha256)
                (fact "throws when input is nil or blank or not a string"
                      (digest nil) => (throws AssertionError)
                      (digest "") => (throws AssertionError)
                      (digest {}) => (throws AssertionError)
                      (digest []) => (throws AssertionError)
                      (digest true) => (throws AssertionError)
                      (digest false) => (throws AssertionError))))

(deftest test-conpath
  (are [conpath-args expected-path]
    (= (apply conpath conpath-args) expected-path)

    ["https://example.com/" "test"] "https://example.com/test"
    ["https://example.com/" "/test"] "https://example.com/test"
    ["https://example.com" "/test"] "https://example.com/test"
    ["https://example.com" "test"] "https://example.com/test"
    ["https://example.com/" "test" "123"] "https://example.com/test/123"
    ["https://example.com/" 123 "test/foo" "bar"] "https://example.com/123/test/foo/bar"
    ["https://example.com/" 123 "/test"] "https://example.com/123/test"
    ["https://example.com/" nil "/test"] "https://example.com/test"))
