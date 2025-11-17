# Welfare events resource helpers

## Context

Compilation on the server was blocked because `WelfareEventsResource` referenced helper
components that were never implemented. The missing classes have been added to restore
build stability and to keep the welfare events API self-explanatory.

## Implementation

- `WelfareEventsResourceNameFinder` now supplies the canonical welfare event identifiers
  that the API exposes.
- `WelfareEventsResourceDetailsMapper` converts each identifier into a concise
  name/description payload.
- `WelfareEventsResource` provides endpoints under `/api/welfare-events` to list or look
  up these descriptors.

## Usage

- `GET /api/welfare-events` returns the available welfare event descriptors.
- `GET /api/welfare-events/{eventName}` returns the descriptor for a given event name or a
  404 when it is not recognised.
