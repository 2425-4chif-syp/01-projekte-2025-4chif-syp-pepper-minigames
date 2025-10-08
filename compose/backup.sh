#!/usr/bin/env bash

set -e
rm -rf ./target && mkdir -p ./target
docker compose exec postgres pg_dump --dbname=db --username=app | gzip > ./target/demo.sql.gz

echo "backup done, see $PWD/target"
ls -l ./target

