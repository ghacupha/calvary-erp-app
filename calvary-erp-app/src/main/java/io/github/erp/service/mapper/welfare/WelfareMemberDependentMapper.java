package io.github.erp.service.mapper.welfare;

import io.github.erp.domain.welfare.WelfareMemberDependent;
import io.github.erp.domain.welfare.WelfareMembershipRegistration;
import io.github.erp.service.dto.welfare.WelfareMemberDependentDTO;
import io.github.erp.service.mapper.EntityMapper;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WelfareMemberDependent} and its DTO {@link WelfareMemberDependentDTO}.
 */
@Mapper(componentModel = "spring")
public interface WelfareMemberDependentMapper extends EntityMapper<WelfareMemberDependentDTO, WelfareMemberDependent> {
    @Mapping(target = "registrationId", source = "registration.id")
    WelfareMemberDependentDTO toDto(WelfareMemberDependent entity);

    @Mapping(target = "registration", ignore = true)
    WelfareMemberDependent toEntity(WelfareMemberDependentDTO dto);

    @Named("registrationFromId")
    default WelfareMembershipRegistration fromId(Long id) {
        if (id == null) {
            return null;
        }
        WelfareMembershipRegistration registration = new WelfareMembershipRegistration();
        registration.setId(id);
        return registration;
    }
}
