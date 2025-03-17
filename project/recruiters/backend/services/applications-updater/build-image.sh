#!/bin/bash
set -e

mvn clean package -DskipTests
docker build -f src/main/Docker/Dockerfile.jvm -t recruitment/applications-updater-service:1.0.0 .
