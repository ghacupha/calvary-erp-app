package io.github.erp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.erp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EntitySubscriptionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EntitySubscriptionDTO.class);
        EntitySubscriptionDTO entitySubscriptionDTO1 = new EntitySubscriptionDTO();
        entitySubscriptionDTO1.setId(1L);
        EntitySubscriptionDTO entitySubscriptionDTO2 = new EntitySubscriptionDTO();
        assertThat(entitySubscriptionDTO1).isNotEqualTo(entitySubscriptionDTO2);
        entitySubscriptionDTO2.setId(entitySubscriptionDTO1.getId());
        assertThat(entitySubscriptionDTO1).isEqualTo(entitySubscriptionDTO2);
        entitySubscriptionDTO2.setId(2L);
        assertThat(entitySubscriptionDTO1).isNotEqualTo(entitySubscriptionDTO2);
        entitySubscriptionDTO1.setId(null);
        assertThat(entitySubscriptionDTO1).isNotEqualTo(entitySubscriptionDTO2);
    }
}
