package io.github.erp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class InstitutionCriteriaTest {

    @Test
    void newInstitutionCriteriaHasAllFiltersNullTest() {
        var institutionCriteria = new InstitutionCriteria();
        assertThat(institutionCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void institutionCriteriaFluentMethodsCreatesFiltersTest() {
        var institutionCriteria = new InstitutionCriteria();

        setAllFilters(institutionCriteria);

        assertThat(institutionCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void institutionCriteriaCopyCreatesNullFilterTest() {
        var institutionCriteria = new InstitutionCriteria();
        var copy = institutionCriteria.copy();

        assertThat(institutionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(institutionCriteria)
        );
    }

    @Test
    void institutionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var institutionCriteria = new InstitutionCriteria();
        setAllFilters(institutionCriteria);

        var copy = institutionCriteria.copy();

        assertThat(institutionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(institutionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var institutionCriteria = new InstitutionCriteria();

        assertThat(institutionCriteria).hasToString("InstitutionCriteria{}");
    }

    private static void setAllFilters(InstitutionCriteria institutionCriteria) {
        institutionCriteria.id();
        institutionCriteria.name();
        institutionCriteria.entitySubscriptionId();
        institutionCriteria.distinct();
    }

    private static Condition<InstitutionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getEntitySubscriptionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<InstitutionCriteria> copyFiltersAre(InstitutionCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getEntitySubscriptionId(), copy.getEntitySubscriptionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
