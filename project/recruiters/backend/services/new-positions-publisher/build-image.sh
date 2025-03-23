mvn clean package -DskipTests
docker build -f src/main/docker/Dockerfile.jvm -t recruitment/new-positions-publisher-service:1.0.0 .
