(ns org.zalando.stups.friboo.zalando-internal.system.oauth2
  (:require [org.zalando.stups.friboo.config :refer [require-config]]
            [com.stuartsierra.component :as component]
            [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (org.zalando.stups.tokens Tokens ClientCredentialsProvider ClientCredentials UserCredentialsProvider UserCredentials CredentialsUnavailableException AccessTokensBuilder AccessTokens AccessTokenUnavailableException)
           (java.net URI)))

;; TODO move this ns to friboo-ext-zalando

(defn make-client-credentials-provider [file]
  (reify ClientCredentialsProvider
    (get [_]
      (let [{:strs [client_id client_secret]} (json/parse-string (slurp file))]
        (reify ClientCredentials
          (getId [_] client_id)
          (getSecret [_] client_secret))))))

(defn make-user-credentials-provider [file]
  (reify UserCredentialsProvider
    (get [_]
      (let [{:strs [application_username application_password]} (json/parse-string (slurp file))]
        (reify UserCredentials
          (getUsername [_] application_username)
          (getPassword [_] application_password))))))

(defn AccessTokensBuilder-start
  "Just for mocking out."
  [^AccessTokensBuilder tokens-builder]
  (.start tokens-builder))

(defn start-refresher
  "Sets up and starts Tokens/AccessTokens object that refreshes tokens in the background."
  [access-token-url credentials-dir tokens]
  ;; Provide custom implementations to prevent the Tokens library from using System.getenv("CREDENTIALS_DIR")
  (let [tokens-builder (-> (Tokens/createAccessTokensWithUri (URI. access-token-url))
                           (.usingClientCredentialsProvider
                             (make-client-credentials-provider (io/file credentials-dir "client.json")))
                           (.usingUserCredentialsProvider
                             (make-user-credentials-provider (io/file credentials-dir "user.json"))))]

    ; register all scopes
    (doseq [[token-id scopes] tokens]
      (let [token (.manageToken tokens-builder token-id)]
        (doseq [scope scopes]
          (.addScope token scope))
        (.done token)))

    (AccessTokensBuilder-start tokens-builder)))

(defn parse-static-tokens
  "Takes a string like 'foo=nfjhsjaieu2023rbfl,bar=q923r023bh2i4943'
  and parses it to {:foo \"nfjhsjaieu2023rbfl\" :bar \"q923r023bh2i4943\"}"
  [access-tokens-str]
  (into {} (for [name=value (str/split access-tokens-str #",")
                 :let [[n v] (str/split name=value #"=" 2)]
                 :when v]
             [(keyword n) v])))

; configuration has to be given, contains links to IAM solution and timings
; tokens has to be given, contains map of tokenids to list of required scopes
; token-storage will be an atom containing the current access token
(defrecord OAuth2TokenRefresher [;; Input parameters
                                 configuration
                                 tokens
                                 ;; Set by start
                                 token-storage
                                 static-tokens]
  component/Lifecycle

  (start [this]
    (let [{:keys [access-token-url credentials-dir access-tokens]} configuration]
      (merge
        this
        (when (and access-token-url credentials-dir (seq tokens))
          {:token-storage (start-refresher access-token-url credentials-dir tokens)})
        ;; Not relying on AccessTokenRefresher/initializeFixedTokensFromEnvironment,
        ;; because it reads OAUTH2_ACCESS_TOKENS env var directly, not compatible with reloaded workflow.
        (when access-tokens
          {:static-tokens (parse-static-tokens access-tokens)}))))

  (stop [this]
    (when token-storage
      (.stop token-storage))
    (assoc this :token-storage nil)))

(defn access-token
  "Returns the valid access token of the given ID."
  [token-id {:keys [token-storage static-tokens]}]
  (or
    (if-let [static-token (get static-tokens token-id)]
      static-token
      (when token-storage
        (.get token-storage token-id)))
    (throw (AccessTokenUnavailableException. "Token was not registered."))))
