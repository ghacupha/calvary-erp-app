package io.github.erp.domain;

import static io.github.erp.domain.InstitutionTestSamples.*;
import static io.github.erp.domain.InstitutionTestSamples.*;
import static io.github.erp.domain.InstitutionalSubscriptionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.erp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class InstitutionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Institution.class);
        Institution institution1 = getInstitutionSample1();
        Institution institution2 = new Institution();
        assertThat(institution1).isNotEqualTo(institution2);

        institution2.setId(institution1.getId());
        assertThat(institution1).isEqualTo(institution2);

        institution2 = getInstitutionSample2();
        assertThat(institution1).isNotEqualTo(institution2);
    }

    @Test
    void parentInstitutionTest() {
        Institution institution = getInstitutionRandomSampleGenerator();
        Institution institutionBack = getInstitutionRandomSampleGenerator();

        institution.setParentInstitution(institutionBack);
        assertThat(institution.getParentInstitution()).isEqualTo(institutionBack);

        institution.parentInstitution(null);
        assertThat(institution.getParentInstitution()).isNull();
    }

    @Test
    void institutionTest() {
        Institution institution = getInstitutionRandomSampleGenerator();
        Institution institutionBack = getInstitutionRandomSampleGenerator();

        institution.addInstitution(institutionBack);
        assertThat(institution.getInstitutions()).containsOnly(institutionBack);
        assertThat(institutionBack.getParentInstitution()).isEqualTo(institution);

        institution.removeInstitution(institutionBack);
        assertThat(institution.getInstitutions()).doesNotContain(institutionBack);
        assertThat(institutionBack.getParentInstitution()).isNull();

        institution.institutions(new HashSet<>(Set.of(institutionBack)));
        assertThat(institution.getInstitutions()).containsOnly(institutionBack);
        assertThat(institutionBack.getParentInstitution()).isEqualTo(institution);

        institution.setInstitutions(new HashSet<>());
        assertThat(institution.getInstitutions()).doesNotContain(institutionBack);
        assertThat(institutionBack.getParentInstitution()).isNull();
    }

    @Test
    void institutionalSubscriptionTest() {
        Institution institution = getInstitutionRandomSampleGenerator();
        InstitutionalSubscription institutionalSubscriptionBack = getInstitutionalSubscriptionRandomSampleGenerator();

        institution.addInstitutionalSubscription(institutionalSubscriptionBack);
        assertThat(institution.getInstitutionalSubscriptions()).containsOnly(institutionalSubscriptionBack);
        assertThat(institutionalSubscriptionBack.getInstitution()).isEqualTo(institution);

        institution.removeInstitutionalSubscription(institutionalSubscriptionBack);
        assertThat(institution.getInstitutionalSubscriptions()).doesNotContain(institutionalSubscriptionBack);
        assertThat(institutionalSubscriptionBack.getInstitution()).isNull();

        institution.institutionalSubscriptions(new HashSet<>(Set.of(institutionalSubscriptionBack)));
        assertThat(institution.getInstitutionalSubscriptions()).containsOnly(institutionalSubscriptionBack);
        assertThat(institutionalSubscriptionBack.getInstitution()).isEqualTo(institution);

        institution.setInstitutionalSubscriptions(new HashSet<>());
        assertThat(institution.getInstitutionalSubscriptions()).doesNotContain(institutionalSubscriptionBack);
        assertThat(institutionalSubscriptionBack.getInstitution()).isNull();
    }
}
