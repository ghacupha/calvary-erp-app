package io.github.erp.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.github.erp.domain.EntitySubscription} entity. This class is used
 * in {@link io.github.erp.web.rest.EntitySubscriptionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /entity-subscriptions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EntitySubscriptionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private UUIDFilter subscriptionToken;

    private ZonedDateTimeFilter startDate;

    private ZonedDateTimeFilter endDate;

    private LongFilter institutionId;

    private Boolean distinct;

    public EntitySubscriptionCriteria() {}

    public EntitySubscriptionCriteria(EntitySubscriptionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.subscriptionToken = other.optionalSubscriptionToken().map(UUIDFilter::copy).orElse(null);
        this.startDate = other.optionalStartDate().map(ZonedDateTimeFilter::copy).orElse(null);
        this.endDate = other.optionalEndDate().map(ZonedDateTimeFilter::copy).orElse(null);
        this.institutionId = other.optionalInstitutionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public EntitySubscriptionCriteria copy() {
        return new EntitySubscriptionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public UUIDFilter getSubscriptionToken() {
        return subscriptionToken;
    }

    public Optional<UUIDFilter> optionalSubscriptionToken() {
        return Optional.ofNullable(subscriptionToken);
    }

    public UUIDFilter subscriptionToken() {
        if (subscriptionToken == null) {
            setSubscriptionToken(new UUIDFilter());
        }
        return subscriptionToken;
    }

    public void setSubscriptionToken(UUIDFilter subscriptionToken) {
        this.subscriptionToken = subscriptionToken;
    }

    public ZonedDateTimeFilter getStartDate() {
        return startDate;
    }

    public Optional<ZonedDateTimeFilter> optionalStartDate() {
        return Optional.ofNullable(startDate);
    }

    public ZonedDateTimeFilter startDate() {
        if (startDate == null) {
            setStartDate(new ZonedDateTimeFilter());
        }
        return startDate;
    }

    public void setStartDate(ZonedDateTimeFilter startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTimeFilter getEndDate() {
        return endDate;
    }

    public Optional<ZonedDateTimeFilter> optionalEndDate() {
        return Optional.ofNullable(endDate);
    }

    public ZonedDateTimeFilter endDate() {
        if (endDate == null) {
            setEndDate(new ZonedDateTimeFilter());
        }
        return endDate;
    }

    public void setEndDate(ZonedDateTimeFilter endDate) {
        this.endDate = endDate;
    }

    public LongFilter getInstitutionId() {
        return institutionId;
    }

    public Optional<LongFilter> optionalInstitutionId() {
        return Optional.ofNullable(institutionId);
    }

    public LongFilter institutionId() {
        if (institutionId == null) {
            setInstitutionId(new LongFilter());
        }
        return institutionId;
    }

    public void setInstitutionId(LongFilter institutionId) {
        this.institutionId = institutionId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EntitySubscriptionCriteria that = (EntitySubscriptionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(subscriptionToken, that.subscriptionToken) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(endDate, that.endDate) &&
            Objects.equals(institutionId, that.institutionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, subscriptionToken, startDate, endDate, institutionId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EntitySubscriptionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalSubscriptionToken().map(f -> "subscriptionToken=" + f + ", ").orElse("") +
            optionalStartDate().map(f -> "startDate=" + f + ", ").orElse("") +
            optionalEndDate().map(f -> "endDate=" + f + ", ").orElse("") +
            optionalInstitutionId().map(f -> "institutionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
