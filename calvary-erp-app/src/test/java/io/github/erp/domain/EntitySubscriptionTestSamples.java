package io.github.erp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EntitySubscriptionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static EntitySubscription getEntitySubscriptionSample1() {
        return new EntitySubscription().id(1L).subscriptionToken(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static EntitySubscription getEntitySubscriptionSample2() {
        return new EntitySubscription().id(2L).subscriptionToken(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static EntitySubscription getEntitySubscriptionRandomSampleGenerator() {
        return new EntitySubscription().id(longCount.incrementAndGet()).subscriptionToken(UUID.randomUUID());
    }
}
