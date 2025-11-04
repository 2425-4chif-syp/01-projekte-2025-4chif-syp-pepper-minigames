#!/usr/bin/env bash
set -e

pushd pep-caretaker/frontend
  npm install
  npx ng build PepperAngular --configuration production
popd

mkdir -p dist/frontend
cp -r pep-caretaker/frontend/dist/pepper-angular/* dist/frontend
