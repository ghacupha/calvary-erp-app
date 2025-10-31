package io.github.erp.service.mapper;

import static io.github.erp.domain.InstitutionAsserts.*;
import static io.github.erp.domain.InstitutionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstitutionMapperTest {

    private InstitutionMapper institutionMapper;

    @BeforeEach
    void setUp() {
        institutionMapper = new InstitutionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInstitutionSample1();
        var actual = institutionMapper.toEntity(institutionMapper.toDto(expected));
        assertInstitutionAllPropertiesEquals(expected, actual);
    }
}
