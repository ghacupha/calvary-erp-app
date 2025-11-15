package io.github.erp.web.rest.erp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.erp.IntegrationTest;
import io.github.erp.domain.welfare.WelfareMemberDependent;
import io.github.erp.domain.welfare.WelfareMembershipRegistration;
import io.github.erp.repository.welfare.WelfareMemberDependentRepository;
import io.github.erp.repository.welfare.WelfareMembershipRegistrationRepository;
import io.github.erp.security.AuthoritiesConstants;
import io.github.erp.service.dto.welfare.WelfareMemberDependentDTO;
import io.github.erp.service.dto.welfare.WelfareMembershipRegistrationDTO;
import io.github.erp.service.mapper.welfare.WelfareMembershipRegistrationMapper;
import io.github.erp.web.rest.TestUtil;
import io.github.erp.web.rest.WithUnauthenticatedMockUser;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@AutoConfigureMockMvc
class WelfareQuestionnaireResourceIT {

    private static final String QUESTIONNAIRE_URL = "/api/erp/welfare/questionnaire";
    private static final String SUBMISSION_URL = "/api/erp/welfare/questionnaire/submissions";
    private static final String REPORT_URL = "/api/erp/welfare/questionnaire/submissions/report";

    @Autowired
    private MockMvc restMockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WelfareMembershipRegistrationRepository registrationRepository;

    @Autowired
    private WelfareMemberDependentRepository dependentRepository;

    @Autowired
    private WelfareMembershipRegistrationMapper registrationMapper;

    private WelfareMembershipRegistration registration;

    @BeforeEach
    void initTest() {
        registration = new WelfareMembershipRegistration()
            .applicantFirstName("Alice")
            .applicantLastName("Anderson")
            .applicantEmail("alice@example.com")
            .phoneNumber("555-0100")
            .membershipType("Standard")
            .submittedAt(Instant.now());
    }

    @AfterEach
    void cleanup() {
        dependentRepository.deleteAll();
        registrationRepository.deleteAll();
    }

    @Test
    @WithUnauthenticatedMockUser
    void anonymousUserCanRetrieveQuestionnaireDefinition() throws Exception {
        restMockMvc
            .perform(get(QUESTIONNAIRE_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Welfare Membership Registration Questionnaire"))
            .andExpect(jsonPath("$.allowsDependents").value(true));
    }

    @Test
    @Transactional
    @WithUnauthenticatedMockUser
    void anonymousSubmissionPersistsRegistrationAndDependents() throws Exception {
        long registrationsBefore = registrationRepository.count();
        long dependentsBefore = dependentRepository.count();

        WelfareMembershipRegistrationDTO dto = registrationMapper.toDto(registration);
        dto.setSubmittedAt(null);
        dto.setDependents(
            List.of(
                dependentDTO("Charlie", "Child", LocalDate.of(2015, 1, 20)),
                dependentDTO("Diana", "Spouse", LocalDate.of(1988, 5, 5))
            )
        );

        String response =
            restMockMvc
                .perform(post(SUBMISSION_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        WelfareMembershipRegistrationDTO returned = objectMapper.readValue(response, WelfareMembershipRegistrationDTO.class);
        assertThat(returned.getId()).isNotNull();

        assertThat(registrationRepository.count()).isEqualTo(registrationsBefore + 1);
        assertThat(dependentRepository.count()).isEqualTo(dependentsBefore + 2);

        Optional<WelfareMembershipRegistration> persisted = registrationRepository.findById(returned.getId());
        assertThat(persisted).isPresent();
        assertThat(persisted.get().getDependents()).hasSize(2);
    }

    @Test
    @WithUnauthenticatedMockUser
    void anonymousCannotListSubmissions() throws Exception {
        restMockMvc.perform(get(SUBMISSION_URL)).andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void staffCanListSubmissions() throws Exception {
        registrationRepository.saveAndFlush(registration);

        restMockMvc
            .perform(get(SUBMISSION_URL).param("sort", "id,desc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].applicantFirstName").value("Alice"));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void staffReportIncludesCounts() throws Exception {
        registration.addDependent(new WelfareMemberDependent().fullName("Charlie").relationship("Child").dateOfBirth(LocalDate.of(2015, 1, 20)));
        registrationRepository.saveAndFlush(registration);

        restMockMvc
            .perform(get(REPORT_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalRegistrations").value(1))
            .andExpect(jsonPath("$.totalDependents").value(1))
            .andExpect(jsonPath("$.membershipTypeBreakdown.Standard").value(1));
    }

    private WelfareMemberDependentDTO dependentDTO(String name, String relationship, LocalDate dob) {
        WelfareMemberDependentDTO dto = new WelfareMemberDependentDTO();
        dto.setFullName(name);
        dto.setRelationship(relationship);
        dto.setDateOfBirth(dob);
        return dto;
    }
}
