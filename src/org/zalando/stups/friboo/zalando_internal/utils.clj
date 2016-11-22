(ns org.zalando.stups.friboo.zalando-internal.utils
  (:require [clojure.string :as s]
            [digest :as d]))

(defmacro xor
  [a b]
  `(or (and ~a (not ~b))
       (and (not ~a) ~b)))

(defn conpath
  "Concatenates path elements to an URL."
  [url & path]
  (let [[x & xs] path]
    (let [x                (str x)
          first-with-slash (.endsWith url "/")
          last-with-slash  (.startsWith x "/")
          url              (if (xor first-with-slash
                                    last-with-slash)
                             ; concat if exactly one of them has a /
                             (str url x)
                             (if (and first-with-slash
                                      last-with-slash)
                               ; if both have a /, remove one
                               (str url (s/replace-first x #"/" ""))
                               ; if none have a /, add one
                               (str url "/" x)))]
      (if xs
        (recur url xs)
        url))))

(defn digest
  ([input]
   {:pre [(string? input)
          (not (clojure.string/blank? input))]}
   (d/sha-256 input)))
