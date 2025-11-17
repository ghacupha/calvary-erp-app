# Welfare Membership Questionnaire — User Stories

## Visitor submits a welfare membership request
- **Persona:** Community member seeking welfare support.
- **Trigger:** Follows a link to the public welfare membership questionnaire.
- **Steps:**
  1. Opens `/questionnaires/welfare-membership` from the site navigation.
  2. Reads the description and enters their personal details in the first section.
  3. Adds child entries as needed, completing names and other requested data.
  4. Reviews validation hints, correcting any highlighted errors.
  5. Submits the form.
- **Outcome:** A confirmation message appears and the payload is posted to the welfare membership intake endpoint for back-office processing.
