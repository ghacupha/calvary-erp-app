package io.github.erp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class EntitySubscriptionCriteriaTest {

    @Test
    void newEntitySubscriptionCriteriaHasAllFiltersNullTest() {
        var entitySubscriptionCriteria = new EntitySubscriptionCriteria();
        assertThat(entitySubscriptionCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void entitySubscriptionCriteriaFluentMethodsCreatesFiltersTest() {
        var entitySubscriptionCriteria = new EntitySubscriptionCriteria();

        setAllFilters(entitySubscriptionCriteria);

        assertThat(entitySubscriptionCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void entitySubscriptionCriteriaCopyCreatesNullFilterTest() {
        var entitySubscriptionCriteria = new EntitySubscriptionCriteria();
        var copy = entitySubscriptionCriteria.copy();

        assertThat(entitySubscriptionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(entitySubscriptionCriteria)
        );
    }

    @Test
    void entitySubscriptionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var entitySubscriptionCriteria = new EntitySubscriptionCriteria();
        setAllFilters(entitySubscriptionCriteria);

        var copy = entitySubscriptionCriteria.copy();

        assertThat(entitySubscriptionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(entitySubscriptionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var entitySubscriptionCriteria = new EntitySubscriptionCriteria();

        assertThat(entitySubscriptionCriteria).hasToString("EntitySubscriptionCriteria{}");
    }

    private static void setAllFilters(EntitySubscriptionCriteria entitySubscriptionCriteria) {
        entitySubscriptionCriteria.id();
        entitySubscriptionCriteria.subscriptionToken();
        entitySubscriptionCriteria.startDate();
        entitySubscriptionCriteria.endDate();
        entitySubscriptionCriteria.institutionId();
        entitySubscriptionCriteria.distinct();
    }

    private static Condition<EntitySubscriptionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getSubscriptionToken()) &&
                condition.apply(criteria.getStartDate()) &&
                condition.apply(criteria.getEndDate()) &&
                condition.apply(criteria.getInstitutionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<EntitySubscriptionCriteria> copyFiltersAre(
        EntitySubscriptionCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getSubscriptionToken(), copy.getSubscriptionToken()) &&
                condition.apply(criteria.getStartDate(), copy.getStartDate()) &&
                condition.apply(criteria.getEndDate(), copy.getEndDate()) &&
                condition.apply(criteria.getInstitutionId(), copy.getInstitutionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
