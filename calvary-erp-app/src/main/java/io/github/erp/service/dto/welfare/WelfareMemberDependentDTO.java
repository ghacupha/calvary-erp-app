package io.github.erp.service.dto.welfare;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link io.github.erp.domain.welfare.WelfareMemberDependent} entity.
 */
public class WelfareMemberDependentDTO implements Serializable {

    private Long id;

    @NotBlank
    private String fullName;

    @NotBlank
    private String relationship;

    @NotNull
    private LocalDate dateOfBirth;

    private String notes;

    private Long registrationId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WelfareMemberDependentDTO)) {
            return false;
        }

        WelfareMemberDependentDTO that = (WelfareMemberDependentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "WelfareMemberDependentDTO{" +
            "id=" + getId() +
            ", fullName='" + getFullName() + '\'' +
            ", relationship='" + getRelationship() + '\'' +
            ", dateOfBirth=" + getDateOfBirth() +
            '}';
    }
}
