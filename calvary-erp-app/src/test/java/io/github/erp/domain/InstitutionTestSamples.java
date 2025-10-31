package io.github.erp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InstitutionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Institution getInstitutionSample1() {
        return new Institution().id(1L).name("name1");
    }

    public static Institution getInstitutionSample2() {
        return new Institution().id(2L).name("name2");
    }

    public static Institution getInstitutionRandomSampleGenerator() {
        return new Institution().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
