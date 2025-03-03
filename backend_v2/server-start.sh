#!/bin/bash

./mvnw clean package
java -jar target/*-runner.jar
