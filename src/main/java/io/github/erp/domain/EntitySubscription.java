package io.github.erp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A EntitySubscription.
 */
@Table("entity_subscription")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "entitysubscription")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EntitySubscription implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("subscription_token")
    private UUID subscriptionToken;

    @NotNull(message = "must not be null")
    @Column("start_date")
    private ZonedDateTime startDate;

    @NotNull(message = "must not be null")
    @Column("end_date")
    private ZonedDateTime endDate;

    @Transient
    @JsonIgnoreProperties(value = { "entitySubscriptions" }, allowSetters = true)
    private Institution institution;

    @Column("institution_id")
    private Long institutionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EntitySubscription id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getSubscriptionToken() {
        return this.subscriptionToken;
    }

    public EntitySubscription subscriptionToken(UUID subscriptionToken) {
        this.setSubscriptionToken(subscriptionToken);
        return this;
    }

    public void setSubscriptionToken(UUID subscriptionToken) {
        this.subscriptionToken = subscriptionToken;
    }

    public ZonedDateTime getStartDate() {
        return this.startDate;
    }

    public EntitySubscription startDate(ZonedDateTime startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return this.endDate;
    }

    public EntitySubscription endDate(ZonedDateTime endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public Institution getInstitution() {
        return this.institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
        this.institutionId = institution != null ? institution.getId() : null;
    }

    public EntitySubscription institution(Institution institution) {
        this.setInstitution(institution);
        return this;
    }

    public Long getInstitutionId() {
        return this.institutionId;
    }

    public void setInstitutionId(Long institution) {
        this.institutionId = institution;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EntitySubscription)) {
            return false;
        }
        return getId() != null && getId().equals(((EntitySubscription) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EntitySubscription{" +
            "id=" + getId() +
            ", subscriptionToken='" + getSubscriptionToken() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            "}";
    }
}
