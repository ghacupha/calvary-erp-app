package io.github.erp.service.criteria.welfare;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link io.github.erp.domain.welfare.WelfareMembershipRegistration} entity.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WelfareMembershipRegistrationCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private StringFilter applicantFirstName;
    private StringFilter applicantLastName;
    private StringFilter applicantEmail;
    private StringFilter phoneNumber;
    private StringFilter city;
    private StringFilter stateProvince;
    private StringFilter membershipType;
    private InstantFilter submittedAt;
    private LongFilter dependentsId;
    private Boolean distinct;

    public WelfareMembershipRegistrationCriteria() {}

    public WelfareMembershipRegistrationCriteria(WelfareMembershipRegistrationCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.applicantFirstName = other.optionalApplicantFirstName().map(StringFilter::copy).orElse(null);
        this.applicantLastName = other.optionalApplicantLastName().map(StringFilter::copy).orElse(null);
        this.applicantEmail = other.optionalApplicantEmail().map(StringFilter::copy).orElse(null);
        this.phoneNumber = other.optionalPhoneNumber().map(StringFilter::copy).orElse(null);
        this.city = other.optionalCity().map(StringFilter::copy).orElse(null);
        this.stateProvince = other.optionalStateProvince().map(StringFilter::copy).orElse(null);
        this.membershipType = other.optionalMembershipType().map(StringFilter::copy).orElse(null);
        this.submittedAt = other.optionalSubmittedAt().map(InstantFilter::copy).orElse(null);
        this.dependentsId = other.optionalDependentsId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public WelfareMembershipRegistrationCriteria copy() {
        return new WelfareMembershipRegistrationCriteria(this);
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public Optional<StringFilter> optionalApplicantFirstName() {
        return Optional.ofNullable(applicantFirstName);
    }

    public StringFilter getApplicantFirstName() {
        return applicantFirstName;
    }

    public void setApplicantFirstName(StringFilter applicantFirstName) {
        this.applicantFirstName = applicantFirstName;
    }

    public StringFilter applicantFirstName() {
        if (applicantFirstName == null) {
            applicantFirstName = new StringFilter();
        }
        return applicantFirstName;
    }

    public Optional<StringFilter> optionalApplicantLastName() {
        return Optional.ofNullable(applicantLastName);
    }

    public StringFilter getApplicantLastName() {
        return applicantLastName;
    }

    public void setApplicantLastName(StringFilter applicantLastName) {
        this.applicantLastName = applicantLastName;
    }

    public StringFilter applicantLastName() {
        if (applicantLastName == null) {
            applicantLastName = new StringFilter();
        }
        return applicantLastName;
    }

    public Optional<StringFilter> optionalApplicantEmail() {
        return Optional.ofNullable(applicantEmail);
    }

    public StringFilter getApplicantEmail() {
        return applicantEmail;
    }

    public void setApplicantEmail(StringFilter applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    public StringFilter applicantEmail() {
        if (applicantEmail == null) {
            applicantEmail = new StringFilter();
        }
        return applicantEmail;
    }

    public Optional<StringFilter> optionalPhoneNumber() {
        return Optional.ofNullable(phoneNumber);
    }

    public StringFilter getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(StringFilter phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public StringFilter phoneNumber() {
        if (phoneNumber == null) {
            phoneNumber = new StringFilter();
        }
        return phoneNumber;
    }

    public Optional<StringFilter> optionalCity() {
        return Optional.ofNullable(city);
    }

    public StringFilter getCity() {
        return city;
    }

    public void setCity(StringFilter city) {
        this.city = city;
    }

    public StringFilter city() {
        if (city == null) {
            city = new StringFilter();
        }
        return city;
    }

    public Optional<StringFilter> optionalStateProvince() {
        return Optional.ofNullable(stateProvince);
    }

    public StringFilter getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(StringFilter stateProvince) {
        this.stateProvince = stateProvince;
    }

    public StringFilter stateProvince() {
        if (stateProvince == null) {
            stateProvince = new StringFilter();
        }
        return stateProvince;
    }

    public Optional<StringFilter> optionalMembershipType() {
        return Optional.ofNullable(membershipType);
    }

    public StringFilter getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(StringFilter membershipType) {
        this.membershipType = membershipType;
    }

    public StringFilter membershipType() {
        if (membershipType == null) {
            membershipType = new StringFilter();
        }
        return membershipType;
    }

    public Optional<InstantFilter> optionalSubmittedAt() {
        return Optional.ofNullable(submittedAt);
    }

    public InstantFilter getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(InstantFilter submittedAt) {
        this.submittedAt = submittedAt;
    }

    public InstantFilter submittedAt() {
        if (submittedAt == null) {
            submittedAt = new InstantFilter();
        }
        return submittedAt;
    }

    public Optional<LongFilter> optionalDependentsId() {
        return Optional.ofNullable(dependentsId);
    }

    public LongFilter getDependentsId() {
        return dependentsId;
    }

    public void setDependentsId(LongFilter dependentsId) {
        this.dependentsId = dependentsId;
    }

    public LongFilter dependentsId() {
        if (dependentsId == null) {
            dependentsId = new LongFilter();
        }
        return dependentsId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WelfareMembershipRegistrationCriteria)) {
            return false;
        }
        WelfareMembershipRegistrationCriteria that = (WelfareMembershipRegistrationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(applicantFirstName, that.applicantFirstName) &&
            Objects.equals(applicantLastName, that.applicantLastName) &&
            Objects.equals(applicantEmail, that.applicantEmail) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(city, that.city) &&
            Objects.equals(stateProvince, that.stateProvince) &&
            Objects.equals(membershipType, that.membershipType) &&
            Objects.equals(submittedAt, that.submittedAt) &&
            Objects.equals(dependentsId, that.dependentsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            applicantFirstName,
            applicantLastName,
            applicantEmail,
            phoneNumber,
            city,
            stateProvince,
            membershipType,
            submittedAt,
            dependentsId,
            distinct
        );
    }

    @Override
    public String toString() {
        return "WelfareMembershipRegistrationCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (applicantFirstName != null ? "applicantFirstName=" + applicantFirstName + ", " : "") +
            (applicantLastName != null ? "applicantLastName=" + applicantLastName + ", " : "") +
            (applicantEmail != null ? "applicantEmail=" + applicantEmail + ", " : "") +
            (phoneNumber != null ? "phoneNumber=" + phoneNumber + ", " : "") +
            (city != null ? "city=" + city + ", " : "") +
            (stateProvince != null ? "stateProvince=" + stateProvince + ", " : "") +
            (membershipType != null ? "membershipType=" + membershipType + ", " : "") +
            (submittedAt != null ? "submittedAt=" + submittedAt + ", " : "") +
            (dependentsId != null ? "dependentsId=" + dependentsId + ", " : "") +
            (distinct != null ? "distinct=" + distinct : "") +
            '}';
    }
}
