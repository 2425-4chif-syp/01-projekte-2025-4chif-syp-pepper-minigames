#!/bin/bash
POSTGRES_VERSION="17.0"
URL="http://edufs.edu.htl-leonding.ac.at/~t.stuetz/download/nvs/scripts/postgres-$POSTGRES_VERSION/"
FILE="./src/main/docker/docker-compose-postgres.yaml"
if [ ! -f "$FILE" ]; then   # if docker-compose-postgres.yaml exists NOT in local folder
	curl ${URL}docker-compose-postgres.yaml \
		 -o ./src/main/docker/docker-compose-postgres.yaml \
		 --create-dirs
fi

curl ${URL}postgres-create-db.sh -o postgres-create-db.sh
curl ${URL}postgres-run-in-docker.sh -o postgres-run-in-docker.sh
curl ${URL}postgres-start.sh -o postgres-start.sh
curl ${URL}postgres-stop.sh -o postgres-stop.sh
curl ${URL}application.properties -o application.properties
curl ${URL}datasource.txt -o datasource.txt
chmod +x ./postgres-create-db.sh
chmod +x ./postgres-run-in-docker.sh
chmod +x ./postgres-start.sh
chmod +x ./postgres-stop.sh
