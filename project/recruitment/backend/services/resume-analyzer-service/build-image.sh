#!/bin/bash
set -e

mvn clean package -DskipTests
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
docker build -t recruitment/applications-analyzer-service:1.0.0 .
