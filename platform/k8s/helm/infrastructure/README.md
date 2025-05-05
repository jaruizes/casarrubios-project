# Infrastructure Helm Chart

This Helm chart deploys the infrastructure services required for the application.

## Introduction

This chart deploys the following infrastructure components:

- PgAdmin - PostgreSQL administration tool
- Qdrant - Vector database
- MinIO - Object storage
- Zookeeper - Coordination service for Kafka
- Kafka Broker - Message broker
- Kafka UI - Web interface for Kafka
- CDC Service - Change Data Capture service
- OpenTelemetry Collector - Telemetry collection and processing
- Jaeger - Distributed tracing system

## Prerequisites

- Kubernetes 1.19+
- Helm 3.2.0+
- PV provisioner support in the underlying infrastructure (if persistence is enabled)

## Installing the Chart

To install the chart with the release name `infrastructure`:

```bash
helm install infrastructure ./infrastructure
```

## Configuration

The following table lists the configurable parameters of the chart and their default values.

### Global Parameters

| Parameter | Description | Default |
|-----------|-------------|---------|
| `global.storageClass` | Storage class for persistent volumes | `standard` |
| `global.namespace` | Namespace for the infrastructure services | `infrastructure` |

### PostgreSQL Parameters

| Parameter | Description | Default |
|-----------|-------------|---------|
| `postgresql.enabled` | Enable PostgreSQL | `true` |
| `postgresql.image.repository` | PostgreSQL image repository | `postgres` |
| `postgresql.image.tag` | PostgreSQL image tag | `14` |
| `postgresql.service.port` | PostgreSQL service port | `5432` |
| `postgresql.persistence.enabled` | Enable persistence for PostgreSQL | `true` |
| `postgresql.persistence.size` | Size of PostgreSQL data volume | `1Gi` |
| `postgresql.env.POSTGRES_PASSWORD` | PostgreSQL password | `postgres` |
| `postgresql.env.POSTGRES_USER` | PostgreSQL username | `postgres` |
| `postgresql.env.POSTGRES_DB` | PostgreSQL database name | `applications` |

### PgAdmin Parameters

| Parameter | Description | Default |
|-----------|-------------|---------|
| `pgadmin.enabled` | Enable PgAdmin | `true` |
| `pgadmin.image.repository` | PgAdmin image repository | `dpage/pgadmin4` |
| `pgadmin.image.tag` | PgAdmin image tag | `latest` |
| `pgadmin.service.port` | PgAdmin service port | `80` |
| `pgadmin.ingress.enabled` | Enable ingress for PgAdmin | `false` |
| `pgadmin.ingress.host` | Hostname for PgAdmin ingress | `pgadmin.local` |
| `pgadmin.env.PGADMIN_DEFAULT_EMAIL` | PgAdmin default email | `admin@admin.com` |
| `pgadmin.env.PGADMIN_DEFAULT_PASSWORD` | PgAdmin default password | `admin` |

For more configuration options, see the [values.yaml](values.yaml) file.

## Persistence

This chart supports persistence for several components. To disable persistence for a specific component, set `<component>.persistence.enabled` to `false`.

## Accessing Services

### Internal Access

Services are accessible within the cluster using their service names:

- PostgreSQL: `infrastructure-postgresql:5432`
- PgAdmin: `infrastructure-pgadmin:80`
- Qdrant: `infrastructure-qdrant:6333` (HTTP), `infrastructure-qdrant:6334` (gRPC)
- MinIO: `infrastructure-minio:9000` (API), `infrastructure-minio:9001` (Console)
- Zookeeper: `infrastructure-zookeeper:2181`
- Kafka Broker: `infrastructure-broker:29092` (Internal), `infrastructure-broker:9092` (External)
- Kafka UI: `infrastructure-kafka-ui:8001`
- CDC Service: `infrastructure-cdc-service:8083`
- OpenTelemetry Collector: `infrastructure-otel-collector:4317` (gRPC), `infrastructure-otel-collector:4318` (HTTP)
- Jaeger: `infrastructure-jaeger:16686`

### External Access

For external access, you can enable ingress for the web interfaces (PgAdmin, Kafka UI, Jaeger) by setting `<component>.ingress.enabled` to `true` and configuring the ingress host.

## Uninstalling the Chart

To uninstall/delete the `infrastructure` deployment:

```bash
helm uninstall infrastructure
```

This removes all the Kubernetes components associated with the chart and deletes the release.
