(defproject org.zalando.stups/friboo-ext-zalando "2.0.0-beta2-SNAPSHOT"
  :description "Extension library on top of friboo by Zalando"
  :url "https://github.com/zalando-incubator/friboo-ext-zalando"

  :license {:name         "Apache 2.0"
            :url          "http://www.apache.org/licenses/LICENSE-2.0"
            :distribution :repo}

  :scm {:url "git@github.com:zalando-incubator/friboo-ext-zalando.git"}

  :min-lein-version "2.0.0"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.zalando.stups/friboo "2.0.0-beta4"]
                 [org.postgresql/postgresql "9.4.1212"]]

  :plugins [[lein-cloverage "1.0.9"]
            [lein-environ "1.1.0"]
            [lein-marginalia "0.9.0"]]

  :aliases {"cloverage" ["with-profile" "test" "cloverage"]}

  :profiles {:test {:dependencies [[midje "1.8.3"]]}
             :dev  {:repl-options {:init-ns user}
                    :source-paths ["dev"]
                    :dependencies [[midje "1.8.3"]
                                   [org.clojure/tools.namespace "0.2.11"]
                                   [org.clojure/java.classpath "0.2.3"]]}}

  :deploy-repositories {"releases"  {:url "https://oss.sonatype.org/service/local/staging/deploy/maven2/" :creds :gpg}
                        "snapshots" {:url "https://oss.sonatype.org/content/repositories/snapshots/" :creds :gpg}})
