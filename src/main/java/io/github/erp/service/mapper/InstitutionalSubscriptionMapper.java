package io.github.erp.service.mapper;

import io.github.erp.domain.Institution;
import io.github.erp.domain.InstitutionalSubscription;
import io.github.erp.service.dto.InstitutionDTO;
import io.github.erp.service.dto.InstitutionalSubscriptionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InstitutionalSubscription} and its DTO {@link InstitutionalSubscriptionDTO}.
 */
@Mapper(componentModel = "spring")
public interface InstitutionalSubscriptionMapper extends EntityMapper<InstitutionalSubscriptionDTO, InstitutionalSubscription> {
    @Mapping(target = "institution", source = "institution", qualifiedByName = "institutionInstitutionName")
    InstitutionalSubscriptionDTO toDto(InstitutionalSubscription s);

    @Named("institutionInstitutionName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "institutionName", source = "institutionName")
    InstitutionDTO toDtoInstitutionInstitutionName(Institution institution);
}
