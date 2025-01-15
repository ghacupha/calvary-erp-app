package io.github.erp.service.mapper;

import io.github.erp.domain.EntitySubscription;
import io.github.erp.domain.Institution;
import io.github.erp.service.dto.EntitySubscriptionDTO;
import io.github.erp.service.dto.InstitutionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EntitySubscription} and its DTO {@link EntitySubscriptionDTO}.
 */
@Mapper(componentModel = "spring")
public interface EntitySubscriptionMapper extends EntityMapper<EntitySubscriptionDTO, EntitySubscription> {
    @Mapping(target = "institution", source = "institution", qualifiedByName = "institutionName")
    EntitySubscriptionDTO toDto(EntitySubscription s);

    @Named("institutionName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    InstitutionDTO toDtoInstitutionName(Institution institution);
}
