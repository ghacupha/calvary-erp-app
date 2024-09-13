package io.github.erp.domain;

import static io.github.erp.domain.InstitutionTestSamples.*;
import static io.github.erp.domain.InstitutionalSubscriptionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.erp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InstitutionalSubscriptionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InstitutionalSubscription.class);
        InstitutionalSubscription institutionalSubscription1 = getInstitutionalSubscriptionSample1();
        InstitutionalSubscription institutionalSubscription2 = new InstitutionalSubscription();
        assertThat(institutionalSubscription1).isNotEqualTo(institutionalSubscription2);

        institutionalSubscription2.setId(institutionalSubscription1.getId());
        assertThat(institutionalSubscription1).isEqualTo(institutionalSubscription2);

        institutionalSubscription2 = getInstitutionalSubscriptionSample2();
        assertThat(institutionalSubscription1).isNotEqualTo(institutionalSubscription2);
    }

    @Test
    void institutionTest() {
        InstitutionalSubscription institutionalSubscription = getInstitutionalSubscriptionRandomSampleGenerator();
        Institution institutionBack = getInstitutionRandomSampleGenerator();

        institutionalSubscription.setInstitution(institutionBack);
        assertThat(institutionalSubscription.getInstitution()).isEqualTo(institutionBack);

        institutionalSubscription.institution(null);
        assertThat(institutionalSubscription.getInstitution()).isNull();
    }
}
