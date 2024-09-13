package io.github.erp.service.mapper;

import static io.github.erp.domain.InstitutionalSubscriptionAsserts.*;
import static io.github.erp.domain.InstitutionalSubscriptionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstitutionalSubscriptionMapperTest {

    private InstitutionalSubscriptionMapper institutionalSubscriptionMapper;

    @BeforeEach
    void setUp() {
        institutionalSubscriptionMapper = new InstitutionalSubscriptionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInstitutionalSubscriptionSample1();
        var actual = institutionalSubscriptionMapper.toEntity(institutionalSubscriptionMapper.toDto(expected));
        assertInstitutionalSubscriptionAllPropertiesEquals(expected, actual);
    }
}
