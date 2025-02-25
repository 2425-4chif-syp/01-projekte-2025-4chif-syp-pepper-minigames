#!/usr/bin/env bash

set -e

pushd backend_v2
  mvn -B clean package
popd

mkdir -p dist/backend

cp -r backend_v2/target/*-runner.jar dist/backend/backend.jar
