package io.github.erp.service.mapper;

import io.github.erp.domain.EntitySubscription;
import io.github.erp.service.dto.EntitySubscriptionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EntitySubscription} and its DTO {@link EntitySubscriptionDTO}.
 */
@Mapper(componentModel = "spring")
public interface EntitySubscriptionMapper extends EntityMapper<EntitySubscriptionDTO, EntitySubscription> {}
