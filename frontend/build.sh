#!/usr/bin/env bash

set -e

pushd frontend
  npm install
  npx ng build --configuration production frontend
popd

mkdir -p dist/frontend

cp -r frontend/dist/frontend/browser/* dist/frontend
