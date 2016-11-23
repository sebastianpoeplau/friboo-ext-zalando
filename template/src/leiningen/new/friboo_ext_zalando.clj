(ns leiningen.new.friboo-ext-zalando
    (:require [leiningen.new.templates :refer [renderer year date project-name
                                             ->files sanitize-ns name-to-path
                                             multi-segment sanitize]]
            [leiningen.core.main :as main]
            [clojure.string :as str]))

(defn db-prefix [name]
  (->> (str/split name #"(-|_)")
       (map first)
       (apply str)))

(defn prepare-data [name]
  (let [namespace (sanitize-ns name)]
    {:raw-name    name
     :name        (project-name name)
     :namespace   namespace
     :package     (sanitize namespace)
     :nested-dirs (name-to-path namespace)
     :db-prefix   (db-prefix (project-name name))
     :year        (year)
     :date        (date)}))

(defn friboo-ext-zalando
  "A Friboo project template"
  [name]
  (let [render (renderer "friboo-ext-zalando")
        data   (prepare-data name)]
    (main/debug "Template data:" data)
    (main/info "Generating a project called" name "based on the 'friboo-ext-zalando' template.")
    (->files data
             ["project.clj" (render "project.clj" data)]
             ["README.md" (render "README.md" data)]
             ["Dockerfile" (render "Dockerfile" data)]
             [".dockerignore" (render "dockerignore" data)]
             [".gitignore" (render "gitignore" data)]
             ["make.sh" (render "make.sh" data) :executable true]
             ["dev-config.edn" (render "dev-config.edn" data)]
             ["resources/api.yaml" (render "api.yaml" data)]
             ["resources/db/queries.sql" (render "queries.sql" data)]
             ["resources/db/migration/V1__initial_schema.sql" (render "schema.sql" data)]
             ["dev/user.clj" (render "user.clj" data)]
             ["src/{{nested-dirs}}/sql.clj" (render "sql.clj" data)]
             ["src/{{nested-dirs}}/api.clj" (render "api.clj" data)]
             ["src/{{nested-dirs}}/core.clj" (render "core.clj" data)]
             ["src/{{nested-dirs}}/utils.clj" (render "utils.clj" data)]
             ["test/{{nested-dirs}}/core_test.clj" (render "core_test.clj" data)]
             ["test/{{nested-dirs}}/api_test.clj" (render "api_test.clj" data)]
             ["test/{{nested-dirs}}/test_utils.clj" (render "test_utils.clj" data)]
             ["LICENSE" (render "LICENSE" data)]
             ;["CHANGELOG.md" (render "CHANGELOG.md" data)]
             "resources")))
