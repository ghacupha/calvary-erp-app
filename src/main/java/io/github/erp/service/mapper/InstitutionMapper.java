package io.github.erp.service.mapper;

import io.github.erp.domain.Institution;
import io.github.erp.service.dto.InstitutionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Institution} and its DTO {@link InstitutionDTO}.
 */
@Mapper(componentModel = "spring")
public interface InstitutionMapper extends EntityMapper<InstitutionDTO, Institution> {
    @Mapping(target = "parentInstitution", source = "parentInstitution", qualifiedByName = "institutionInstitutionName")
    InstitutionDTO toDto(Institution s);

    @Named("institutionInstitutionName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "institutionName", source = "institutionName")
    InstitutionDTO toDtoInstitutionInstitutionName(Institution institution);
}
