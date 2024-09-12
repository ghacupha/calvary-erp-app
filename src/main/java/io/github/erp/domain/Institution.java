package io.github.erp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Institution.
 */
@Table("institution")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "institution")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Institution implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("institution_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String institutionName;

    @Transient
    @JsonIgnoreProperties(value = { "parentInstitution", "institutions" }, allowSetters = true)
    private Institution parentInstitution;

    @Transient
    @JsonIgnoreProperties(value = { "parentInstitution", "institutions" }, allowSetters = true)
    private Set<Institution> institutions = new HashSet<>();

    @Column("parent_institution_id")
    private Long parentInstitutionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Institution id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInstitutionName() {
        return this.institutionName;
    }

    public Institution institutionName(String institutionName) {
        this.setInstitutionName(institutionName);
        return this;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public Institution getParentInstitution() {
        return this.parentInstitution;
    }

    public void setParentInstitution(Institution institution) {
        this.parentInstitution = institution;
        this.parentInstitutionId = institution != null ? institution.getId() : null;
    }

    public Institution parentInstitution(Institution institution) {
        this.setParentInstitution(institution);
        return this;
    }

    public Set<Institution> getInstitutions() {
        return this.institutions;
    }

    public void setInstitutions(Set<Institution> institutions) {
        if (this.institutions != null) {
            this.institutions.forEach(i -> i.setParentInstitution(null));
        }
        if (institutions != null) {
            institutions.forEach(i -> i.setParentInstitution(this));
        }
        this.institutions = institutions;
    }

    public Institution institutions(Set<Institution> institutions) {
        this.setInstitutions(institutions);
        return this;
    }

    public Institution addInstitution(Institution institution) {
        this.institutions.add(institution);
        institution.setParentInstitution(this);
        return this;
    }

    public Institution removeInstitution(Institution institution) {
        this.institutions.remove(institution);
        institution.setParentInstitution(null);
        return this;
    }

    public Long getParentInstitutionId() {
        return this.parentInstitutionId;
    }

    public void setParentInstitutionId(Long institution) {
        this.parentInstitutionId = institution;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Institution)) {
            return false;
        }
        return getId() != null && getId().equals(((Institution) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Institution{" +
            "id=" + getId() +
            ", institutionName='" + getInstitutionName() + "'" +
            "}";
    }
}
