#!/usr/bin/env bash

set -e
rm -rf ./target && mkdir -p ./target
docker compose exec postgres pg_dump --dbname=keycloak --username=keycloak | gzip > ./target/keycloak.sql.gz
docker compose exec postgres pg_dump --dbname=demo --username=demo | gzip > ./target/demo.sql.gz

echo "backup done, see $PWD/target"
ls -l ./target

