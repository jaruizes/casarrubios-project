#!/bin/bash
set -e

mvn clean package -DskipTests
docker build -f src/main/Docker/Dockerfile.jvm -t recruitment/notifications-manager-service:1.0.0 .
