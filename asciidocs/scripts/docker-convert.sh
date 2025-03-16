#!/usr/bin/env bash

inputPath="$1"
createSlides="$2"
source $inputPath/scripts/docker-convert-util.sh
ASCIIDOCTOR_VERSION="1.58"

echo "input => $inputPath"
echo "createSlides => $createSlides"
echo building html

if [ $createSlides = true ]; then
    convertFilesToSlides "$inputPath" $ASCIIDOCTOR_VERSION
fi

#inputPath="$inputPath"

convertFilesToHTML "$inputPath" $ASCIIDOCTOR_VERSION

# set permissions of output folder to the same as the input folder - fixes #1
#if [ -d "$inputPath" ] && [ -d "$outputPath" ]; then
#    chown $(stat "$inputPath" -c %u:%g) "$outputPath" -R
#fi
