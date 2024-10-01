package io.github.erp.domain;

import static io.github.erp.domain.ApplicationUserTestSamples.*;
import static io.github.erp.domain.InstitutionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.erp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ApplicationUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ApplicationUser.class);
        ApplicationUser applicationUser1 = getApplicationUserSample1();
        ApplicationUser applicationUser2 = new ApplicationUser();
        assertThat(applicationUser1).isNotEqualTo(applicationUser2);

        applicationUser2.setId(applicationUser1.getId());
        assertThat(applicationUser1).isEqualTo(applicationUser2);

        applicationUser2 = getApplicationUserSample2();
        assertThat(applicationUser1).isNotEqualTo(applicationUser2);
    }

    @Test
    void institutionTest() {
        ApplicationUser applicationUser = getApplicationUserRandomSampleGenerator();
        Institution institutionBack = getInstitutionRandomSampleGenerator();

        applicationUser.setInstitution(institutionBack);
        assertThat(applicationUser.getInstitution()).isEqualTo(institutionBack);

        applicationUser.institution(null);
        assertThat(applicationUser.getInstitution()).isNull();
    }
}
