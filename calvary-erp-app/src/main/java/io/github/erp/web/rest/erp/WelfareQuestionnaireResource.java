package io.github.erp.web.rest.erp;

import io.github.erp.security.AuthoritiesConstants;
import io.github.erp.service.criteria.welfare.WelfareMembershipRegistrationCriteria;
import io.github.erp.service.dto.welfare.WelfareMembershipRegistrationDTO;
import io.github.erp.service.dto.welfare.WelfareQuestionnaireReportDTO;
import io.github.erp.service.welfare.WelfareMembershipRegistrationQueryService;
import io.github.erp.service.welfare.WelfareMembershipRegistrationService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for handling the public welfare questionnaire and internal reporting endpoints.
 */
@RestController
@RequestMapping("/api/erp/welfare")
public class WelfareQuestionnaireResource {

    private static final Logger LOG = LoggerFactory.getLogger(WelfareQuestionnaireResource.class);
    private static final String ENTITY_NAME = "welfareMembershipRegistration";

    private final WelfareMembershipRegistrationService welfareMembershipRegistrationService;
    private final WelfareMembershipRegistrationQueryService welfareMembershipRegistrationQueryService;

    public WelfareQuestionnaireResource(
        WelfareMembershipRegistrationService welfareMembershipRegistrationService,
        WelfareMembershipRegistrationQueryService welfareMembershipRegistrationQueryService
    ) {
        this.welfareMembershipRegistrationService = welfareMembershipRegistrationService;
        this.welfareMembershipRegistrationQueryService = welfareMembershipRegistrationQueryService;
    }

    /**
     * {@code GET  /questionnaire} : expose the questionnaire definition for anonymous clients.
     */
    @GetMapping("/questionnaire")
    public ResponseEntity<Map<String, Object>> getQuestionnaireDefinition() {
        LOG.debug("REST request to fetch welfare questionnaire definition");
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("title", "Welfare Membership Registration Questionnaire");
        payload.put("description", "Provide applicant details and optional dependents to request welfare membership support.");

        List<Map<String, Object>> fields = new ArrayList<>();
        fields.add(field("applicantFirstName", "Applicant first name", "text", true));
        fields.add(field("applicantLastName", "Applicant last name", "text", true));
        fields.add(field("applicantEmail", "Email address", "email", true));
        fields.add(field("phoneNumber", "Phone number", "text", false));
        fields.add(field("addressLine1", "Address line 1", "text", false));
        fields.add(field("addressLine2", "Address line 2", "text", false));
        fields.add(field("city", "City", "text", false));
        fields.add(field("stateProvince", "State / Province", "text", false));
        fields.add(field("postalCode", "Postal code", "text", false));
        fields.add(field("membershipType", "Requested membership type", "select", true, List.of("Standard", "Senior", "Family")));
        fields.add(field("householdIncome", "Household income bracket", "text", false));
        fields.add(field("notes", "Additional information", "textarea", false));

        List<Map<String, Object>> dependentFields = new ArrayList<>();
        dependentFields.add(field("fullName", "Dependent full name", "text", true));
        dependentFields.add(field("relationship", "Relationship to applicant", "text", true));
        dependentFields.add(field("dateOfBirth", "Date of birth", "date", true));
        dependentFields.add(field("notes", "Notes", "textarea", false));

        payload.put("fields", fields);
        payload.put("dependentFields", dependentFields);
        payload.put("allowsDependents", true);

        return ResponseEntity.ok(payload);
    }

    /**
     * {@code POST  /questionnaire/submissions} : accept an anonymous questionnaire submission.
     */
    @PostMapping("/questionnaire/submissions")
    public ResponseEntity<WelfareMembershipRegistrationDTO> submitQuestionnaire(
        @Valid @RequestBody WelfareMembershipRegistrationDTO registrationDTO
    ) {
        LOG.debug("REST request to submit WelfareMembershipRegistration : {}", registrationDTO);
        registrationDTO.setId(null);
        if (registrationDTO.getSubmittedAt() == null) {
            registrationDTO.setSubmittedAt(Instant.now());
        }
        WelfareMembershipRegistrationDTO result = welfareMembershipRegistrationService.save(registrationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * {@code GET  /questionnaire/submissions} : get all submissions for authorised staff.
     */
    @GetMapping("/questionnaire/submissions")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<WelfareMembershipRegistrationDTO>> getAllSubmissions(
        WelfareMembershipRegistrationCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get WelfareMembershipRegistrations by criteria: {}", criteria);
        Page<WelfareMembershipRegistrationDTO> page = welfareMembershipRegistrationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /questionnaire/submissions/count} : count submissions for authorised staff.
     */
    @GetMapping("/questionnaire/submissions/count")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Long> countSubmissions(WelfareMembershipRegistrationCriteria criteria) {
        LOG.debug("REST request to count WelfareMembershipRegistrations by criteria: {}", criteria);
        return ResponseEntity.ok().body(welfareMembershipRegistrationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /questionnaire/submissions/:id} : get a single submission by id.
     */
    @GetMapping("/questionnaire/submissions/{id}")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<WelfareMembershipRegistrationDTO> getSubmission(@PathVariable Long id) {
        LOG.debug("REST request to get WelfareMembershipRegistration : {}", id);
        return ResponseUtil.wrapOrNotFound(welfareMembershipRegistrationService.findOne(id));
    }

    /**
     * {@code GET  /questionnaire/submissions/report} : aggregated reporting for staff users.
     */
    @GetMapping("/questionnaire/submissions/report")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<WelfareQuestionnaireReportDTO> getSubmissionReport() {
        LOG.debug("REST request to get WelfareMembershipRegistration summary report");
        return ResponseEntity.ok(welfareMembershipRegistrationService.buildSummaryReport());
    }

    private Map<String, Object> field(String name, String label, String type, boolean required) {
        Map<String, Object> descriptor = new LinkedHashMap<>();
        descriptor.put("name", name);
        descriptor.put("label", label);
        descriptor.put("type", type);
        descriptor.put("required", required);
        return descriptor;
    }

    private Map<String, Object> field(String name, String label, String type, boolean required, List<String> options) {
        Map<String, Object> descriptor = field(name, label, type, required);
        descriptor.put("options", options);
        return descriptor;
    }
}
