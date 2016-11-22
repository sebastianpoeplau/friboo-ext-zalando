(ns org.zalando.stups.friboo.zalando-internal.config
  (:require [org.zalando.stups.friboo.config :as config]
            [org.zalando.stups.friboo.config-decrypt :as decrypt]))

(defn load-config
  "Loads configuration with Zalando-specific tweaks (remapping and decryption) in place."
  [default-config additional-namespaces & [{:keys [mapping]}]]
  (decrypt/decrypt-config
    (config/load-config
      (merge {:metrics-metrics-prefix "zmon"}
             default-config)
      (concat [:global :oauth2] additional-namespaces)
      {:mapping (merge {:global-tokeninfo-url   :tokeninfo-url
                        :oauth2-credentials-dir :credentials-dir}
                       mapping)})))
