package io.github.erp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ApplicationUserCriteriaTest {

    @Test
    void newApplicationUserCriteriaHasAllFiltersNullTest() {
        var applicationUserCriteria = new ApplicationUserCriteria();
        assertThat(applicationUserCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void applicationUserCriteriaFluentMethodsCreatesFiltersTest() {
        var applicationUserCriteria = new ApplicationUserCriteria();

        setAllFilters(applicationUserCriteria);

        assertThat(applicationUserCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void applicationUserCriteriaCopyCreatesNullFilterTest() {
        var applicationUserCriteria = new ApplicationUserCriteria();
        var copy = applicationUserCriteria.copy();

        assertThat(applicationUserCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(applicationUserCriteria)
        );
    }

    @Test
    void applicationUserCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var applicationUserCriteria = new ApplicationUserCriteria();
        setAllFilters(applicationUserCriteria);

        var copy = applicationUserCriteria.copy();

        assertThat(applicationUserCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(applicationUserCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var applicationUserCriteria = new ApplicationUserCriteria();

        assertThat(applicationUserCriteria).hasToString("ApplicationUserCriteria{}");
    }

    private static void setAllFilters(ApplicationUserCriteria applicationUserCriteria) {
        applicationUserCriteria.id();
        applicationUserCriteria.username();
        applicationUserCriteria.firstName();
        applicationUserCriteria.lastName();
        applicationUserCriteria.email();
        applicationUserCriteria.activated();
        applicationUserCriteria.langKey();
        applicationUserCriteria.imageUrl();
        applicationUserCriteria.activationKey();
        applicationUserCriteria.resetKey();
        applicationUserCriteria.resetDate();
        applicationUserCriteria.systemUserId();
        applicationUserCriteria.institutionId();
        applicationUserCriteria.distinct();
    }

    private static Condition<ApplicationUserCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getUsername()) &&
                condition.apply(criteria.getFirstName()) &&
                condition.apply(criteria.getLastName()) &&
                condition.apply(criteria.getEmail()) &&
                condition.apply(criteria.getActivated()) &&
                condition.apply(criteria.getLangKey()) &&
                condition.apply(criteria.getImageUrl()) &&
                condition.apply(criteria.getActivationKey()) &&
                condition.apply(criteria.getResetKey()) &&
                condition.apply(criteria.getResetDate()) &&
                condition.apply(criteria.getSystemUserId()) &&
                condition.apply(criteria.getInstitutionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ApplicationUserCriteria> copyFiltersAre(
        ApplicationUserCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getUsername(), copy.getUsername()) &&
                condition.apply(criteria.getFirstName(), copy.getFirstName()) &&
                condition.apply(criteria.getLastName(), copy.getLastName()) &&
                condition.apply(criteria.getEmail(), copy.getEmail()) &&
                condition.apply(criteria.getActivated(), copy.getActivated()) &&
                condition.apply(criteria.getLangKey(), copy.getLangKey()) &&
                condition.apply(criteria.getImageUrl(), copy.getImageUrl()) &&
                condition.apply(criteria.getActivationKey(), copy.getActivationKey()) &&
                condition.apply(criteria.getResetKey(), copy.getResetKey()) &&
                condition.apply(criteria.getResetDate(), copy.getResetDate()) &&
                condition.apply(criteria.getSystemUserId(), copy.getSystemUserId()) &&
                condition.apply(criteria.getInstitutionId(), copy.getInstitutionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
