package io.github.erp.service.mapper;

import io.github.erp.domain.ApplicationUser;
import io.github.erp.domain.Institution;
import io.github.erp.domain.User;
import io.github.erp.service.dto.ApplicationUserDTO;
import io.github.erp.service.dto.InstitutionDTO;
import io.github.erp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ApplicationUser} and its DTO {@link ApplicationUserDTO}.
 */
@Mapper(componentModel = "spring")
public interface ApplicationUserMapper extends EntityMapper<ApplicationUserDTO, ApplicationUser> {
    @Mapping(target = "systemUser", source = "systemUser", qualifiedByName = "userLogin")
    @Mapping(target = "institution", source = "institution", qualifiedByName = "institutionName")
    ApplicationUserDTO toDto(ApplicationUser s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("institutionName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    InstitutionDTO toDtoInstitutionName(Institution institution);
}
