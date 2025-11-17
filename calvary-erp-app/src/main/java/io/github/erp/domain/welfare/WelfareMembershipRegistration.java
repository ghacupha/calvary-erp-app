package io.github.erp.domain.welfare;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A WelfareMembershipRegistration.
 */
@Entity
@Table(name = "welfare_membership_registration")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WelfareMembershipRegistration implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Column(name = "applicant_first_name", nullable = false)
    private String applicantFirstName;

    @NotBlank
    @Column(name = "applicant_last_name", nullable = false)
    private String applicantLastName;

    @Email
    @NotBlank
    @Column(name = "applicant_email", nullable = false)
    private String applicantEmail;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address_line_1")
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @Column(name = "city")
    private String city;

    @Column(name = "state_province")
    private String stateProvince;

    @Column(name = "postal_code")
    private String postalCode;

    @NotBlank
    @Column(name = "membership_type", nullable = false)
    private String membershipType;

    @Column(name = "household_income")
    private String householdIncome;

    @Column(name = "notes", length = 2000)
    private String notes;

    @NotNull
    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt = Instant.now();

    @OneToMany(mappedBy = "registration", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "registration" }, allowSetters = true)
    private Set<WelfareMemberDependent> dependents = new HashSet<>();

    public Long getId() {
        return this.id;
    }

    public WelfareMembershipRegistration id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicantFirstName() {
        return this.applicantFirstName;
    }

    public WelfareMembershipRegistration applicantFirstName(String applicantFirstName) {
        this.setApplicantFirstName(applicantFirstName);
        return this;
    }

    public void setApplicantFirstName(String applicantFirstName) {
        this.applicantFirstName = applicantFirstName;
    }

    public String getApplicantLastName() {
        return this.applicantLastName;
    }

    public WelfareMembershipRegistration applicantLastName(String applicantLastName) {
        this.setApplicantLastName(applicantLastName);
        return this;
    }

    public void setApplicantLastName(String applicantLastName) {
        this.applicantLastName = applicantLastName;
    }

    public String getApplicantEmail() {
        return this.applicantEmail;
    }

    public WelfareMembershipRegistration applicantEmail(String applicantEmail) {
        this.setApplicantEmail(applicantEmail);
        return this;
    }

    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public WelfareMembershipRegistration phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddressLine1() {
        return this.addressLine1;
    }

    public WelfareMembershipRegistration addressLine1(String addressLine1) {
        this.setAddressLine1(addressLine1);
        return this;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return this.addressLine2;
    }

    public WelfareMembershipRegistration addressLine2(String addressLine2) {
        this.setAddressLine2(addressLine2);
        return this;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return this.city;
    }

    public WelfareMembershipRegistration city(String city) {
        this.setCity(city);
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateProvince() {
        return this.stateProvince;
    }

    public WelfareMembershipRegistration stateProvince(String stateProvince) {
        this.setStateProvince(stateProvince);
        return this;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public WelfareMembershipRegistration postalCode(String postalCode) {
        this.setPostalCode(postalCode);
        return this;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getMembershipType() {
        return this.membershipType;
    }

    public WelfareMembershipRegistration membershipType(String membershipType) {
        this.setMembershipType(membershipType);
        return this;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public String getHouseholdIncome() {
        return this.householdIncome;
    }

    public WelfareMembershipRegistration householdIncome(String householdIncome) {
        this.setHouseholdIncome(householdIncome);
        return this;
    }

    public void setHouseholdIncome(String householdIncome) {
        this.householdIncome = householdIncome;
    }

    public String getNotes() {
        return this.notes;
    }

    public WelfareMembershipRegistration notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getSubmittedAt() {
        return this.submittedAt;
    }

    public WelfareMembershipRegistration submittedAt(Instant submittedAt) {
        this.setSubmittedAt(submittedAt);
        return this;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Set<WelfareMemberDependent> getDependents() {
        return this.dependents;
    }

    public void setDependents(Set<WelfareMemberDependent> dependents) {
        if (this.dependents != null) {
            this.dependents.forEach(dependent -> dependent.setRegistration(null));
        }
        if (dependents != null) {
            dependents.forEach(dependent -> dependent.setRegistration(this));
        }
        this.dependents = dependents;
    }

    public WelfareMembershipRegistration dependents(Set<WelfareMemberDependent> dependents) {
        this.setDependents(dependents);
        return this;
    }

    public WelfareMembershipRegistration addDependent(WelfareMemberDependent dependent) {
        this.dependents.add(dependent);
        dependent.setRegistration(this);
        return this;
    }

    public WelfareMembershipRegistration removeDependent(WelfareMemberDependent dependent) {
        this.dependents.remove(dependent);
        dependent.setRegistration(null);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WelfareMembershipRegistration)) {
            return false;
        }
        return getId() != null && getId().equals(((WelfareMembershipRegistration) o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "WelfareMembershipRegistration{" +
            "id=" + getId() +
            ", applicantFirstName='" + getApplicantFirstName() + '\'' +
            ", applicantLastName='" + getApplicantLastName() + '\'' +
            ", applicantEmail='" + getApplicantEmail() + '\'' +
            ", membershipType='" + getMembershipType() + '\'' +
            ", submittedAt=" + getSubmittedAt() +
            '}';
    }
}
