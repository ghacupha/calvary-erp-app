package io.github.erp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link io.github.erp.domain.Institution} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InstitutionDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String institutionName;

    private InstitutionDTO parentInstitution;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public InstitutionDTO getParentInstitution() {
        return parentInstitution;
    }

    public void setParentInstitution(InstitutionDTO parentInstitution) {
        this.parentInstitution = parentInstitution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InstitutionDTO)) {
            return false;
        }

        InstitutionDTO institutionDTO = (InstitutionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, institutionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InstitutionDTO{" +
            "id=" + getId() +
            ", institutionName='" + getInstitutionName() + "'" +
            ", parentInstitution=" + getParentInstitution() +
            "}";
    }
}
