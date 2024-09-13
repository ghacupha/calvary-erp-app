package io.github.erp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A InstitutionalSubscription.
 */
@Table("institutional_subscription")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "institutionalsubscription")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InstitutionalSubscription implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("start_date")
    private LocalDate startDate;

    @NotNull(message = "must not be null")
    @Column("expiry_date")
    private LocalDate expiryDate;

    @NotNull(message = "must not be null")
    @Column("member_limit")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer memberLimit;

    @Transient
    @JsonIgnoreProperties(value = { "parentInstitution", "institutions", "institutionalSubscriptions" }, allowSetters = true)
    private Institution institution;

    @Column("institution_id")
    private Long institutionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InstitutionalSubscription id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public InstitutionalSubscription startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpiryDate() {
        return this.expiryDate;
    }

    public InstitutionalSubscription expiryDate(LocalDate expiryDate) {
        this.setExpiryDate(expiryDate);
        return this;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getMemberLimit() {
        return this.memberLimit;
    }

    public InstitutionalSubscription memberLimit(Integer memberLimit) {
        this.setMemberLimit(memberLimit);
        return this;
    }

    public void setMemberLimit(Integer memberLimit) {
        this.memberLimit = memberLimit;
    }

    public Institution getInstitution() {
        return this.institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
        this.institutionId = institution != null ? institution.getId() : null;
    }

    public InstitutionalSubscription institution(Institution institution) {
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
        if (!(o instanceof InstitutionalSubscription)) {
            return false;
        }
        return getId() != null && getId().equals(((InstitutionalSubscription) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InstitutionalSubscription{" +
            "id=" + getId() +
            ", startDate='" + getStartDate() + "'" +
            ", expiryDate='" + getExpiryDate() + "'" +
            ", memberLimit=" + getMemberLimit() +
            "}";
    }
}
