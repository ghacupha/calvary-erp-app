package io.github.erp.domain;

import static io.github.erp.domain.EntitySubscriptionTestSamples.*;
import static io.github.erp.domain.InstitutionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.erp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EntitySubscriptionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EntitySubscription.class);
        EntitySubscription entitySubscription1 = getEntitySubscriptionSample1();
        EntitySubscription entitySubscription2 = new EntitySubscription();
        assertThat(entitySubscription1).isNotEqualTo(entitySubscription2);

        entitySubscription2.setId(entitySubscription1.getId());
        assertThat(entitySubscription1).isEqualTo(entitySubscription2);

        entitySubscription2 = getEntitySubscriptionSample2();
        assertThat(entitySubscription1).isNotEqualTo(entitySubscription2);
    }

    @Test
    void institutionTest() {
        EntitySubscription entitySubscription = getEntitySubscriptionRandomSampleGenerator();
        Institution institutionBack = getInstitutionRandomSampleGenerator();

        entitySubscription.setInstitution(institutionBack);
        assertThat(entitySubscription.getInstitution()).isEqualTo(institutionBack);

        entitySubscription.institution(null);
        assertThat(entitySubscription.getInstitution()).isNull();
    }
}
