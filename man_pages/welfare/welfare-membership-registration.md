# Welfare Membership Registration Questionnaire (Backend)

## Overview
The welfare module introduces a dedicated persistence model that captures an applicant’s membership registration alongside any dependents that must be assessed with the request. Two entities were added:

* `WelfareMembershipRegistration` – stores top-level applicant information, free-form notes, and metadata about the submission.
* `WelfareMemberDependent` – represents zero or more dependents that are linked to the parent registration.

Both entities are persisted via Liquibase change-sets and exposed through dedicated DTOs, repositories, MapStruct mappers, and a query service to support filtered reporting.

## Data Model
* `welfare_membership_registration`
  * Required columns: `applicant_first_name`, `applicant_last_name`, `applicant_email`, `membership_type`, `submitted_at`.
  * Optional columns record contact information, household income, and narrative notes.
* `welfare_member_dependent`
  * Required columns: `full_name`, `relationship`, `date_of_birth`, `registration_id`.
  * Foreign key `fk_welfare_member_dependent__registration_id` enforces the parent-child relationship.

The Java entities live under `io.github.erp.domain.welfare` with bidirectional mapping and cascade rules so that dependents are created/deleted with their parent record.

## Service Layer
`WelfareMembershipRegistrationService` handles persistence and summary reporting. It automatically timestamps anonymous submissions when the payload omits `submittedAt`. The associated `WelfareMembershipRegistrationQueryService` extends the JHipster `QueryService` to expose criteria-based filtering across applicant fields and dependent identifiers.

## REST API
`WelfareQuestionnaireResource` publishes the endpoints:

| Method | Path | Description | Security |
|--------|------|-------------|----------|
| GET | `/api/erp/welfare/questionnaire` | Static definition describing form fields and dependent schema. | Anonymous |
| POST | `/api/erp/welfare/questionnaire/submissions` | Persists a new registration with dependents. | Anonymous |
| GET | `/api/erp/welfare/questionnaire/submissions` | Paginates stored submissions using criteria filters. | `ROLE_ADMIN` |
| GET | `/api/erp/welfare/questionnaire/submissions/count` | Returns a filtered count. | `ROLE_ADMIN` |
| GET | `/api/erp/welfare/questionnaire/submissions/{id}` | Retrieves a single submission. | `ROLE_ADMIN` |
| GET | `/api/erp/welfare/questionnaire/submissions/report` | Aggregates totals and membership-type breakdown. | `ROLE_ADMIN` |

Security rules were updated in `SecurityConfiguration` to whitelist the anonymous GET/POST routes while leaving management endpoints protected via `@PreAuthorize` guards.

## Testing
`WelfareQuestionnaireResourceIT` covers:
* fetching the questionnaire definition anonymously,
* anonymous submission persistence (including dependents),
* restricted access for unauthenticated listing attempts,
* administrative listing and reporting endpoints.

Liquibase change-sets and `.jhipster` descriptors ensure future JHipster regenerations remain in sync with the manual additions.
