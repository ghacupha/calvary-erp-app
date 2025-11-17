# Welfare Membership Questionnaire Architecture (Client)

The Calvary ERP client exposes a public welfare membership questionnaire that is fully data-driven.

- **Definition retrieval:** `QuestionnaireService.fetchQuestionnaire(id)` loads JSON from `GET api/public/questionnaires/{id}`.
- **Submission:** `QuestionnaireService.submitQuestionnaire(id, payload)` POSTs the completed answers to `POST api/public/questionnaires/{id}/submissions`.
- **Dynamic form:** `QuestionnaireFormBuilderService` converts the JSON sections into `FormGroup` and `FormArray` structures, applying validators such as required, numeric ranges, length and regex patterns.
- **UI orchestration:** `WelfareQuestionnaireComponent` fetches the definition, renders sections/cards, manages repeatable section controls and handles submission/success/error flags. The route `/questionnaires/welfare-membership` is public and linked from the navigation bar.

This documentation mirrors the front-end specific behaviour; refer to the server module for any persistence or workflow orchestration notes.
