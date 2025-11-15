# User Story: Submit a Welfare Membership Questionnaire

**Persona:** Community member seeking support

**Goal:** Provide the information needed to apply for welfare membership, including any household dependents, without creating an account.

**Happy Path:**
1. Browse to the public questionnaire endpoint in the client.
2. Review the guidance text and the list of required applicant fields (first name, last name, email, membership type).
3. Enter optional contact details (phone, address, notes) as needed.
4. Add dependent rows for each household member, providing their name, relationship, and date of birth.
5. Submit the form anonymously.
6. Receive confirmation that the questionnaire was submitted.

**Acceptance Criteria:**
* The questionnaire definition is publicly available (no login required).
* Submissions persist the applicant data and each dependent under the same registration record.
* Dependents remain associated to the registration when staff review the submission later.
