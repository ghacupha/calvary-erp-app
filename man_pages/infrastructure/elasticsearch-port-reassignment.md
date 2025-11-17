# Elasticsearch port reassignment for development and deployment

## Context

The dev profile previously pointed Elasticsearch clients at port `9301`. On the current host that port responds with `This is not an HTTP port`, preventing Spring Data Elasticsearch repositories from initializing. We are standardising on host port `9865` to avoid collisions with other services.

## Changes applied

- Switched the Spring Boot dev configuration to default to `http://localhost:9865`.
- Updated all Docker Compose definitions to publish Elasticsearch on `9865`, including health checks and application environment variables.
- Ensured both single-node and clustered Compose variants point application containers to the new port.

## Deployment note

The deployment environment relies on a hidden env file that is not under version control. Update every Elasticsearch-related URL in that file (e.g., `SPRING_ELASTICSEARCH_URIS`, `SPRING_DATA_JEST_URI`, and the DEV/PROD variants) to `http://10.60.27.22:9865` so deployments match the new port.

## How to verify

1. Start Elasticsearch via Docker and run `curl http://localhost:9865/_cluster/health` to confirm it responds.
2. Launch the application with the `dev` Maven profile and verify startup proceeds without the prior protocol exception.
