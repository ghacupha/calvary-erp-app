package io.github.erp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ApplicationUserTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ApplicationUser getApplicationUserSample1() {
        return new ApplicationUser()
            .id(1L)
            .username("username1")
            .firstName("firstName1")
            .lastName("lastName1")
            .email("email1")
            .langKey("langKey1")
            .imageUrl("imageUrl1")
            .activationKey("activationKey1")
            .resetKey("resetKey1");
    }

    public static ApplicationUser getApplicationUserSample2() {
        return new ApplicationUser()
            .id(2L)
            .username("username2")
            .firstName("firstName2")
            .lastName("lastName2")
            .email("email2")
            .langKey("langKey2")
            .imageUrl("imageUrl2")
            .activationKey("activationKey2")
            .resetKey("resetKey2");
    }

    public static ApplicationUser getApplicationUserRandomSampleGenerator() {
        return new ApplicationUser()
            .id(longCount.incrementAndGet())
            .username(UUID.randomUUID().toString())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .langKey(UUID.randomUUID().toString())
            .imageUrl(UUID.randomUUID().toString())
            .activationKey(UUID.randomUUID().toString())
            .resetKey(UUID.randomUUID().toString());
    }
}
