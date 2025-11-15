package io.github.erp.service.mapper.welfare;

import io.github.erp.domain.welfare.WelfareMembershipRegistration;
import io.github.erp.service.dto.welfare.WelfareMembershipRegistrationDTO;
import io.github.erp.service.mapper.EntityMapper;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WelfareMembershipRegistration} and its DTO {@link WelfareMembershipRegistrationDTO}.
 */
@Mapper(componentModel = "spring", uses = { WelfareMemberDependentMapper.class })
public interface WelfareMembershipRegistrationMapper
    extends EntityMapper<WelfareMembershipRegistrationDTO, WelfareMembershipRegistration> {
    @Override
    @Mapping(target = "dependents", source = "dependents")
    WelfareMembershipRegistrationDTO toDto(WelfareMembershipRegistration entity);

    @Override
    @Mapping(target = "dependents", source = "dependents")
    WelfareMembershipRegistration toEntity(WelfareMembershipRegistrationDTO dto);

    @AfterMapping
    default void linkDependents(
        WelfareMembershipRegistrationDTO dto,
        @MappingTarget WelfareMembershipRegistration entity
    ) {
        if (entity.getDependents() != null) {
            entity.setDependents(entity.getDependents());
        }
    }
}
