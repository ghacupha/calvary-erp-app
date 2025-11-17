# Elasticsearch port reassignment for development and deployment

## Context

While running the ERP server with the `dev` Maven profile, the application attempted to talk to Elasticsearch on port `9301`. The node on that port replied with `This is not an HTTP port`, causing Spring Data Elasticsearch to fail during repository initialization. We need a port that is free on developer machines and on the deployment host, so the Elasticsearch endpoint has been moved to `9865`.

## Changes applied

- Updated the Spring Boot dev profile to default to `http://localhost:9865` for Elasticsearch URIs.
- Adjusted Docker Compose definitions (single-node services, clustered setup, and standalone Elasticsearch) to publish port `9865` instead of `9301` and to probe health checks on the new port.
- Updated Compose environment variables passed to the application containers so they target Elasticsearch on port `9865`.

## Follow-up for ops

There is a private environment file outside version control that still lists the old `9301` URLs (e.g., `SPRING_ELASTICSEARCH_URIS`, `SPRING_DATA_JEST_URI_*`). Update those entries to `http://10.60.27.22:9865` so the deployed service and local dev profiles align with the new port.

## Verification notes

After applying the change, start the Docker Elasticsearch service and confirm `curl http://localhost:9865/_cluster/health` returns JSON. Then run the application with the `dev` profile to ensure repository initialization completes without the previous protocol error.
