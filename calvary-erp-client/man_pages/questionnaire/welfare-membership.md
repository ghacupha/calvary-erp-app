# Welfare Membership Questionnaire Architecture

## Overview
The welfare membership questionnaire is a public-facing workflow that lets visitors supply structured household data for on-boarding into the Calvary ERP welfare module. The front-end composes itself entirely from a JSON definition delivered by the back-end so that future questionnaires can reuse the same infrastructure.

## Data Contracts
- **QuestionnaireDefinition** — describes the questionnaire metadata and ordered sections.
- **QuestionnaireSection** — groups questions and can optionally allow repeatable entries (e.g. children).
- **QuestionnaireQuestion** — describes an individual prompt including the input control type, placeholders and validation rules.
- **QuestionnaireSubmissionPayload** — payload sent back to the API containing the questionnaire id plus the entire form value tree.

The definition is fetched from `GET api/public/questionnaires/{id}` and the completed payload is POSTed to `POST api/public/questionnaires/{id}/submissions`.

## Form Composition
`QuestionnaireFormBuilderService` consumes a `QuestionnaireDefinition` and builds an Angular reactive `FormGroup` tree:
- Non-repeatable sections become nested `FormGroup` instances.
- Repeatable sections become `FormArray` instances seeded with a single entry that honours minimum and maximum limits.
- Each question maps to a control with validators generated from the JSON rules (required, numeric bounds, length, pattern).

Helper methods (`addSectionItem`, `removeSectionItem`) manage repeatable sections while respecting their bounds.

## Presentation Flow
`WelfareQuestionnaireComponent` orchestrates the experience:
1. Fetches the welfare membership definition on initialisation and builds the form through the form builder service.
2. Renders section cards and delegates input rendering to structural directives that decide between `<input>`, `<textarea>` or `<select>`.
3. Exposes add/remove affordances for repeatable sections and centralises validation messaging.
4. Submits `QuestionnaireSubmissionPayload` objects via `QuestionnaireService`, toggling success and error banners according to the HTTP response.

The route is accessible at `/questionnaires/welfare-membership` without requiring authentication and is linked from the public navigation bar.
