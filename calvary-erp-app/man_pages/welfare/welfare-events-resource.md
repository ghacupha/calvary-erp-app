# Welfare events resource helpers

## Context

The server-side build was failing because the `WelfareEventsResource` referred to helper
classes (`WelfareEventsResourceNameFinder` and `WelfareEventsResourceDetailsMapper`) that
were never defined. To restore compilation and keep the API behaviour clear, lightweight
implementations now live alongside the resource.

## Implementation

- Added `WelfareEventsResourceNameFinder` to provide a canonical list of welfare event
  identifiers currently exposed by the API.
- Added `WelfareEventsResourceDetailsMapper` to translate each identifier into a small
  descriptive payload for responses.
- Added `WelfareEventsResource` endpoints under `/api/welfare-events` so callers can
  retrieve the available welfare event descriptors or fetch a single entry by name.

## Usage

- `GET /api/welfare-events` returns an array of event descriptors with `name` and
  `description` fields.
- `GET /api/welfare-events/{eventName}` returns the descriptor for a specific event or a
  404 status when the provided name is unknown.
