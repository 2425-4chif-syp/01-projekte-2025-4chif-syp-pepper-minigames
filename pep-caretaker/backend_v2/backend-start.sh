#!/bin/bash

# Projektverzeichnis auf Basis des aktuellen Skriptpfads bestimmen
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# mvn clean install ausführen
mvn clean install --skipTests

# Docker Image bauen
docker build -t pepper/backend .

# Postgres stoppen (relativer Pfad zur postgres-stop.sh)
"$SCRIPT_DIR/postgres-stop.sh"

# Postgres starten (relativer Pfad zur postgres-start.sh)
"$SCRIPT_DIR/postgres-start.sh"
