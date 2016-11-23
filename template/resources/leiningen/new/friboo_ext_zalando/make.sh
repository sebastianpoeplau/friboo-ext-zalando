#!/usr/bin/env bash
set -euo pipefail
IFS=$'\t\n'

DB_IMAGE=postgres:9.5
DB_CONTAINER={{name}}-db

cmd_db() {
    db_port=${1:-5432}
    docker rm -fv $DB_CONTAINER ||  true
    docker run -dt --name $DB_CONTAINER \
        -p $db_port:5432 \
        $DB_IMAGE
}

# For more examples check out https://github.com/dryewo/make-sh

# Print all defined cmd_
cmd_help() {
    compgen -A function cmd_
}

# Run multiple commands without args
cmd_mm() {
    for cmd in "$@"; do
        cmd_$cmd
    done
}

if [[ $# -eq 0 ]]; then
    echo Please provide a subcommand
    exit 1
fi

SUBCOMMAND=$1
shift

# Enable verbose mode
set -x
# Run the subcommand
cmd_${SUBCOMMAND} $@
