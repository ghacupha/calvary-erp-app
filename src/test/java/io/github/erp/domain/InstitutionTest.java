package io.github.erp.domain;

import static io.github.erp.domain.EntitySubscriptionTestSamples.*;
import static io.github.erp.domain.InstitutionTestSamples.*;
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
    void entitySubscriptionTest() {
        Institution institution = getInstitutionRandomSampleGenerator();
        EntitySubscription entitySubscriptionBack = getEntitySubscriptionRandomSampleGenerator();

        institution.addEntitySubscription(entitySubscriptionBack);
        assertThat(institution.getEntitySubscriptions()).containsOnly(entitySubscriptionBack);
        assertThat(entitySubscriptionBack.getInstitution()).isEqualTo(institution);

        institution.removeEntitySubscription(entitySubscriptionBack);
        assertThat(institution.getEntitySubscriptions()).doesNotContain(entitySubscriptionBack);
        assertThat(entitySubscriptionBack.getInstitution()).isNull();

        institution.entitySubscriptions(new HashSet<>(Set.of(entitySubscriptionBack)));
        assertThat(institution.getEntitySubscriptions()).containsOnly(entitySubscriptionBack);
        assertThat(entitySubscriptionBack.getInstitution()).isEqualTo(institution);

        institution.setEntitySubscriptions(new HashSet<>());
        assertThat(institution.getEntitySubscriptions()).doesNotContain(entitySubscriptionBack);
        assertThat(entitySubscriptionBack.getInstitution()).isNull();
    }
}
