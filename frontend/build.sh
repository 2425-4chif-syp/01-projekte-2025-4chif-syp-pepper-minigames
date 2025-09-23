#!/usr/bin/env bash
set -e

pushd frontend
  npm install
  npx ng build PepperAngular --configuration production
popd

mkdir -p dist/frontend
cp -r frontend/dist/PepperAngular/browser/* dist/frontend

