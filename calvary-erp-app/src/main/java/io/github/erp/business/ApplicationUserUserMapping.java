package io.github.erp.business;

import io.github.erp.domain.ApplicationUser;
import io.github.erp.domain.User;
import org.springframework.stereotype.Component;

@Component
public class ApplicationUserUserMapping implements Mapping<ApplicationUser, User> {

    @Override
    public User toValue2(ApplicationUser value2) {
        return User.builder()
            .login(value2.getUsername())
            .firstName(value2.getFirstName())
            .lastName(value2.getLastName())
            .resetKey(value2.getResetKey())
            .email(value2.getEmail())
            .activationKey(value2.getActivationKey())
            .langKey(value2.getLangKey())
            .resetDate(value2.getResetDate())
            .activated(value2.getActivated())
            .build();
    }

    @Override
    public ApplicationUser toValue1(User user) {
        var applicationUser = new ApplicationUser();
        applicationUser
            .username(user.getLogin())
            .firstName(user.getFirstName())
            .email(user.getEmail())
            .langKey(user.getLangKey())
            .activated(user.isActivated())
            .activationKey(user.getActivationKey())
            .resetKey(user.getResetKey())
            .resetDate(user.getResetDate())
            .lastName(user.getLastName());
        applicationUser.setSystemUser(user);
        return applicationUser;
    }
}
