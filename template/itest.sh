#!/usr/bin/env bash
set -euo pipefail
IFS=$'\t\n'
set -x

# Build the template itself
lein do clean, test

# Generate a project based on the template and run tests in it
cd target
# We don't need to install it to ~/.m2, because it's already available on the classpath
DEBUG=1 lein new friboo-ext-zalando com.example/foo-bar

pushd foo-bar
    docker-compose up -d
    lein test
    lein uberjar
    touch scm-source.json
    docker build -t foo-bar .
    docker-compose down
popd

# Just in case we want to try it outside of target/
lein install
