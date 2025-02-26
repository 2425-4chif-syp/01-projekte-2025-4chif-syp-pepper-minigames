#!/usr/bin/env bash

set -e

pushd PepperAngular
  npm install
  npx ng build --configuration production PepperAngular
popd

mkdir -p dist/frontend

cp -r PepperAngular/dist/frontend/browser/* dist/frontend/
