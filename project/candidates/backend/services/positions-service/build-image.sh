#!/bin/bash
set -e

mvn clean package -DskipTests
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
docker build -t applications/positions-service:1.0.0 .
