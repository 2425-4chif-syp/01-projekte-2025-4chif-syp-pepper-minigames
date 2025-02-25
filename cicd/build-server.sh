#!/usr/bin/env bash

set -e

pushd Backend_V2
  mvn -B clean package
popd

mkdir -p dist/backend

cp -r backend_v2/target/*-runner.jar dist/backend/backend.jar
