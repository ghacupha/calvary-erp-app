package io.github.erp.service.mapper;

import io.github.erp.domain.Institution;
import io.github.erp.service.dto.InstitutionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Institution} and its DTO {@link InstitutionDTO}.
 */
@Mapper(componentModel = "spring")
public interface InstitutionMapper extends EntityMapper<InstitutionDTO, Institution> {}
