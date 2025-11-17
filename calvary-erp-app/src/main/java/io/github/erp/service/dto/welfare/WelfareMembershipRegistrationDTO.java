package io.github.erp.service.dto.welfare;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the {@link io.github.erp.domain.welfare.WelfareMembershipRegistration} entity.
 */
public class WelfareMembershipRegistrationDTO implements Serializable {

    private Long id;

    @NotBlank
    private String applicantFirstName;

    @NotBlank
    private String applicantLastName;

    @Email
    @NotBlank
    private String applicantEmail;

    private String phoneNumber;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String stateProvince;

    private String postalCode;

    @NotBlank
    private String membershipType;

    private String householdIncome;

    private String notes;

    private Instant submittedAt;

    @Valid
    private List<WelfareMemberDependentDTO> dependents = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicantFirstName() {
        return applicantFirstName;
    }

    public void setApplicantFirstName(String applicantFirstName) {
        this.applicantFirstName = applicantFirstName;
    }

    public String getApplicantLastName() {
        return applicantLastName;
    }

    public void setApplicantLastName(String applicantLastName) {
        this.applicantLastName = applicantLastName;
    }

    public String getApplicantEmail() {
        return applicantEmail;
    }

    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public String getHouseholdIncome() {
        return householdIncome;
    }

    public void setHouseholdIncome(String householdIncome) {
        this.householdIncome = householdIncome;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public List<WelfareMemberDependentDTO> getDependents() {
        return dependents;
    }

    public void setDependents(List<WelfareMemberDependentDTO> dependents) {
        this.dependents = dependents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WelfareMembershipRegistrationDTO)) {
            return false;
        }

        WelfareMembershipRegistrationDTO that = (WelfareMembershipRegistrationDTO) o;
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
        return "WelfareMembershipRegistrationDTO{" +
            "id=" + getId() +
            ", applicantFirstName='" + getApplicantFirstName() + '\'' +
            ", applicantLastName='" + getApplicantLastName() + '\'' +
            ", applicantEmail='" + getApplicantEmail() + '\'' +
            ", membershipType='" + getMembershipType() + '\'' +
            '}';
    }
}
