#!/usr/bin/env bash

set -e

rm -rf target
mvn -B clean package -DskipTests
docker build --tag ghcr.io/2425-4chif-syp/backend --file ./src/main/docker/Dockerfile .
