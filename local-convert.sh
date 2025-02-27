#!/usr/bin/env bash

source config.sh

inputDir=$INPUTPATH
outputDir=$OUTPUTPATH
convertSlides=$SLIDES

echo "inputDir => $inputDir"
echo "outputDir => $outputDir"
echo "convertSlides => $convertSlides"

if [[ "$outputDir" == *\/* ]] || [[ "$outputDir" == *\\* ]]
then
  echo "you should use direct subdirectories of the current folder ... exiting"
  exit 1
fi


if [ -d "$inputDir" ];
then
  rm -rf $outputDir
  mkdir $outputDir
  echo "copying '$inputDir' to '$outputDir'"
  cp -r -v $inputDir/. $outputDir
  pwd
  ./$outputDir/scripts/docker-convert.sh $outputDir $convertSlides
else
  echo "Error: ${inputDir} not found. Cannot continue ..."
fi

