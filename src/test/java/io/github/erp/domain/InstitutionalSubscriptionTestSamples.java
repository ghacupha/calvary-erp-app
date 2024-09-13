package io.github.erp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class InstitutionalSubscriptionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static InstitutionalSubscription getInstitutionalSubscriptionSample1() {
        return new InstitutionalSubscription().id(1L).memberLimit(1);
    }

    public static InstitutionalSubscription getInstitutionalSubscriptionSample2() {
        return new InstitutionalSubscription().id(2L).memberLimit(2);
    }

    public static InstitutionalSubscription getInstitutionalSubscriptionRandomSampleGenerator() {
        return new InstitutionalSubscription().id(longCount.incrementAndGet()).memberLimit(intCount.incrementAndGet());
    }
}
