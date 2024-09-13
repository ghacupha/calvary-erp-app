package io.github.erp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link io.github.erp.domain.InstitutionalSubscription} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InstitutionalSubscriptionDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private LocalDate startDate;

    @NotNull(message = "must not be null")
    private LocalDate expiryDate;

    @NotNull(message = "must not be null")
    private Integer memberLimit;

    @NotNull
    private InstitutionDTO institution;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getMemberLimit() {
        return memberLimit;
    }

    public void setMemberLimit(Integer memberLimit) {
        this.memberLimit = memberLimit;
    }

    public InstitutionDTO getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionDTO institution) {
        this.institution = institution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InstitutionalSubscriptionDTO)) {
            return false;
        }

        InstitutionalSubscriptionDTO institutionalSubscriptionDTO = (InstitutionalSubscriptionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, institutionalSubscriptionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InstitutionalSubscriptionDTO{" +
            "id=" + getId() +
            ", startDate='" + getStartDate() + "'" +
            ", expiryDate='" + getExpiryDate() + "'" +
            ", memberLimit=" + getMemberLimit() +
            ", institution=" + getInstitution() +
            "}";
    }
}
