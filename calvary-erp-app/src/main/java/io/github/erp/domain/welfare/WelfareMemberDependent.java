package io.github.erp.domain.welfare;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A WelfareMemberDependent.
 */
@Entity
@Table(name = "welfare_member_dependent")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WelfareMemberDependent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotBlank
    @Column(name = "relationship", nullable = false)
    private String relationship;

    @NotNull
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "notes", length = 2000)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "dependents" }, allowSetters = true)
    private WelfareMembershipRegistration registration;

    public Long getId() {
        return this.id;
    }

    public WelfareMemberDependent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return this.fullName;
    }

    public WelfareMemberDependent fullName(String fullName) {
        this.setFullName(fullName);
        return this;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRelationship() {
        return this.relationship;
    }

    public WelfareMemberDependent relationship(String relationship) {
        this.setRelationship(relationship);
        return this;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public LocalDate getDateOfBirth() {
        return this.dateOfBirth;
    }

    public WelfareMemberDependent dateOfBirth(LocalDate dateOfBirth) {
        this.setDateOfBirth(dateOfBirth);
        return this;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNotes() {
        return this.notes;
    }

    public WelfareMemberDependent notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public WelfareMembershipRegistration getRegistration() {
        return this.registration;
    }

    public void setRegistration(WelfareMembershipRegistration registration) {
        this.registration = registration;
    }

    public WelfareMemberDependent registration(WelfareMembershipRegistration registration) {
        this.setRegistration(registration);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WelfareMemberDependent)) {
            return false;
        }
        return getId() != null && getId().equals(((WelfareMemberDependent) o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "WelfareMemberDependent{" +
            "id=" + getId() +
            ", fullName='" + getFullName() + '\'' +
            ", relationship='" + getRelationship() + '\'' +
            ", dateOfBirth=" + getDateOfBirth() +
            '}';
    }
}
