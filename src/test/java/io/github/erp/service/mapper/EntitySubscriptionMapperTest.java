package io.github.erp.service.mapper;

import static io.github.erp.domain.EntitySubscriptionAsserts.*;
import static io.github.erp.domain.EntitySubscriptionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EntitySubscriptionMapperTest {

    private EntitySubscriptionMapper entitySubscriptionMapper;

    @BeforeEach
    void setUp() {
        entitySubscriptionMapper = new EntitySubscriptionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEntitySubscriptionSample1();
        var actual = entitySubscriptionMapper.toEntity(entitySubscriptionMapper.toDto(expected));
        assertEntitySubscriptionAllPropertiesEquals(expected, actual);
    }
}
