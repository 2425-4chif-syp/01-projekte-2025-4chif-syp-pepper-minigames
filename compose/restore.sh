#!/usr/bin/env bash
set -e
export FILENAME=sql.gz
export PGPASSWORD=app
psql -f delete-all-tables.sql -h localhost -p 5432 -U app -d db
gunzip -c target/$FILENAME | psql -h localhost -p 5432 -U app -d db
