#!/usr/bin/env bash

source ./local-convert.sh

echo $SLIDES
echo $INPUTPATH
echo $OUTPUTPATH
# sudo chown ${USER}:${USER} dist

remote=$(git remote get-url origin)
cd "$OUTPUTPATH" || exit

rm -rf .git

git init

git add .

git commit -m "updating github pages"

git switch -c gh-pages

git remote add origin "$remote"

git push -f origin gh-pages
