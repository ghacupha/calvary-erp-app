package io.github.erp.domain;

import static io.github.erp.domain.AssertUtils.zonedDataTimeSameInstant;
import static org.assertj.core.api.Assertions.assertThat;

public class EntitySubscriptionAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertEntitySubscriptionAllPropertiesEquals(EntitySubscription expected, EntitySubscription actual) {
        assertEntitySubscriptionAutoGeneratedPropertiesEquals(expected, actual);
        assertEntitySubscriptionAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertEntitySubscriptionAllUpdatablePropertiesEquals(EntitySubscription expected, EntitySubscription actual) {
        assertEntitySubscriptionUpdatableFieldsEquals(expected, actual);
        assertEntitySubscriptionUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertEntitySubscriptionAutoGeneratedPropertiesEquals(EntitySubscription expected, EntitySubscription actual) {
        assertThat(expected)
            .as("Verify EntitySubscription auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertEntitySubscriptionUpdatableFieldsEquals(EntitySubscription expected, EntitySubscription actual) {
        assertThat(expected)
            .as("Verify EntitySubscription relevant properties")
            .satisfies(e -> assertThat(e.getSubscriptionToken()).as("check subscriptionToken").isEqualTo(actual.getSubscriptionToken()))
            .satisfies(e ->
                assertThat(e.getStartDate())
                    .as("check startDate")
                    .usingComparator(zonedDataTimeSameInstant)
                    .isEqualTo(actual.getStartDate())
            )
            .satisfies(e ->
                assertThat(e.getEndDate()).as("check endDate").usingComparator(zonedDataTimeSameInstant).isEqualTo(actual.getEndDate())
            );
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertEntitySubscriptionUpdatableRelationshipsEquals(EntitySubscription expected, EntitySubscription actual) {
        assertThat(expected)
            .as("Verify EntitySubscription relationships")
            .satisfies(e -> assertThat(e.getInstitution()).as("check institution").isEqualTo(actual.getInstitution()));
    }
}
