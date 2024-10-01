package io.github.erp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link io.github.erp.domain.EntitySubscription} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EntitySubscriptionDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private UUID subscriptionToken;

    @NotNull(message = "must not be null")
    private ZonedDateTime startDate;

    @NotNull(message = "must not be null")
    private ZonedDateTime endDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getSubscriptionToken() {
        return subscriptionToken;
    }

    public void setSubscriptionToken(UUID subscriptionToken) {
        this.subscriptionToken = subscriptionToken;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EntitySubscriptionDTO)) {
            return false;
        }

        EntitySubscriptionDTO entitySubscriptionDTO = (EntitySubscriptionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, entitySubscriptionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EntitySubscriptionDTO{" +
            "id=" + getId() +
            ", subscriptionToken='" + getSubscriptionToken() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            "}";
    }
}
