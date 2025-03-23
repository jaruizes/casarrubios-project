mvn clean package -DskipTests
docker build -f src/main/docker/Dockerfile.jvm -t recruitment/positions-manager-service:1.0.0 .
