# How To: Capture Welfare Membership Questionnaire Submissions

1. Call `GET /api/erp/welfare/questionnaire` to retrieve the fields and option lists that should be rendered on the client form.
2. Collect applicant data along with any number of dependents. Each dependent must include a name, relationship, and date of birth.
3. Submit the completed payload to `POST /api/erp/welfare/questionnaire/submissions`. No authentication token is required for this request.
4. Back-office staff with `ROLE_ADMIN` can list submissions via `GET /api/erp/welfare/questionnaire/submissions` (supports pagination and filtering) and download aggregate counts via `/report`.
5. When viewing an individual submission (`/submissions/{id}`), dependents are embedded within the response so staff can review the full household composition.
