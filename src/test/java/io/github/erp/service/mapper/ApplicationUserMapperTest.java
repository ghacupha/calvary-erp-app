package io.github.erp.service.mapper;

import static io.github.erp.domain.ApplicationUserAsserts.*;
import static io.github.erp.domain.ApplicationUserTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApplicationUserMapperTest {

    private ApplicationUserMapper applicationUserMapper;

    @BeforeEach
    void setUp() {
        applicationUserMapper = new ApplicationUserMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getApplicationUserSample1();
        var actual = applicationUserMapper.toEntity(applicationUserMapper.toDto(expected));
        assertApplicationUserAllPropertiesEquals(expected, actual);
    }
}
