package io.github.erp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.erp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InstitutionalSubscriptionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InstitutionalSubscriptionDTO.class);
        InstitutionalSubscriptionDTO institutionalSubscriptionDTO1 = new InstitutionalSubscriptionDTO();
        institutionalSubscriptionDTO1.setId(1L);
        InstitutionalSubscriptionDTO institutionalSubscriptionDTO2 = new InstitutionalSubscriptionDTO();
        assertThat(institutionalSubscriptionDTO1).isNotEqualTo(institutionalSubscriptionDTO2);
        institutionalSubscriptionDTO2.setId(institutionalSubscriptionDTO1.getId());
        assertThat(institutionalSubscriptionDTO1).isEqualTo(institutionalSubscriptionDTO2);
        institutionalSubscriptionDTO2.setId(2L);
        assertThat(institutionalSubscriptionDTO1).isNotEqualTo(institutionalSubscriptionDTO2);
        institutionalSubscriptionDTO1.setId(null);
        assertThat(institutionalSubscriptionDTO1).isNotEqualTo(institutionalSubscriptionDTO2);
    }
}
