package io.github.erp.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.github.erp.domain.ApplicationUser} entity. This class is used
 * in {@link io.github.erp.web.rest.ApplicationUserResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /application-users?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApplicationUserCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter username;

    private StringFilter firstName;

    private StringFilter lastName;

    private StringFilter email;

    private BooleanFilter activated;

    private StringFilter langKey;

    private StringFilter imageUrl;

    private StringFilter activationKey;

    private StringFilter resetKey;

    private InstantFilter resetDate;

    private LongFilter systemUserId;

    private LongFilter institutionId;

    private Boolean distinct;

    public ApplicationUserCriteria() {}

    public ApplicationUserCriteria(ApplicationUserCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.username = other.optionalUsername().map(StringFilter::copy).orElse(null);
        this.firstName = other.optionalFirstName().map(StringFilter::copy).orElse(null);
        this.lastName = other.optionalLastName().map(StringFilter::copy).orElse(null);
        this.email = other.optionalEmail().map(StringFilter::copy).orElse(null);
        this.activated = other.optionalActivated().map(BooleanFilter::copy).orElse(null);
        this.langKey = other.optionalLangKey().map(StringFilter::copy).orElse(null);
        this.imageUrl = other.optionalImageUrl().map(StringFilter::copy).orElse(null);
        this.activationKey = other.optionalActivationKey().map(StringFilter::copy).orElse(null);
        this.resetKey = other.optionalResetKey().map(StringFilter::copy).orElse(null);
        this.resetDate = other.optionalResetDate().map(InstantFilter::copy).orElse(null);
        this.systemUserId = other.optionalSystemUserId().map(LongFilter::copy).orElse(null);
        this.institutionId = other.optionalInstitutionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ApplicationUserCriteria copy() {
        return new ApplicationUserCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getUsername() {
        return username;
    }

    public Optional<StringFilter> optionalUsername() {
        return Optional.ofNullable(username);
    }

    public StringFilter username() {
        if (username == null) {
            setUsername(new StringFilter());
        }
        return username;
    }

    public void setUsername(StringFilter username) {
        this.username = username;
    }

    public StringFilter getFirstName() {
        return firstName;
    }

    public Optional<StringFilter> optionalFirstName() {
        return Optional.ofNullable(firstName);
    }

    public StringFilter firstName() {
        if (firstName == null) {
            setFirstName(new StringFilter());
        }
        return firstName;
    }

    public void setFirstName(StringFilter firstName) {
        this.firstName = firstName;
    }

    public StringFilter getLastName() {
        return lastName;
    }

    public Optional<StringFilter> optionalLastName() {
        return Optional.ofNullable(lastName);
    }

    public StringFilter lastName() {
        if (lastName == null) {
            setLastName(new StringFilter());
        }
        return lastName;
    }

    public void setLastName(StringFilter lastName) {
        this.lastName = lastName;
    }

    public StringFilter getEmail() {
        return email;
    }

    public Optional<StringFilter> optionalEmail() {
        return Optional.ofNullable(email);
    }

    public StringFilter email() {
        if (email == null) {
            setEmail(new StringFilter());
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public BooleanFilter getActivated() {
        return activated;
    }

    public Optional<BooleanFilter> optionalActivated() {
        return Optional.ofNullable(activated);
    }

    public BooleanFilter activated() {
        if (activated == null) {
            setActivated(new BooleanFilter());
        }
        return activated;
    }

    public void setActivated(BooleanFilter activated) {
        this.activated = activated;
    }

    public StringFilter getLangKey() {
        return langKey;
    }

    public Optional<StringFilter> optionalLangKey() {
        return Optional.ofNullable(langKey);
    }

    public StringFilter langKey() {
        if (langKey == null) {
            setLangKey(new StringFilter());
        }
        return langKey;
    }

    public void setLangKey(StringFilter langKey) {
        this.langKey = langKey;
    }

    public StringFilter getImageUrl() {
        return imageUrl;
    }

    public Optional<StringFilter> optionalImageUrl() {
        return Optional.ofNullable(imageUrl);
    }

    public StringFilter imageUrl() {
        if (imageUrl == null) {
            setImageUrl(new StringFilter());
        }
        return imageUrl;
    }

    public void setImageUrl(StringFilter imageUrl) {
        this.imageUrl = imageUrl;
    }

    public StringFilter getActivationKey() {
        return activationKey;
    }

    public Optional<StringFilter> optionalActivationKey() {
        return Optional.ofNullable(activationKey);
    }

    public StringFilter activationKey() {
        if (activationKey == null) {
            setActivationKey(new StringFilter());
        }
        return activationKey;
    }

    public void setActivationKey(StringFilter activationKey) {
        this.activationKey = activationKey;
    }

    public StringFilter getResetKey() {
        return resetKey;
    }

    public Optional<StringFilter> optionalResetKey() {
        return Optional.ofNullable(resetKey);
    }

    public StringFilter resetKey() {
        if (resetKey == null) {
            setResetKey(new StringFilter());
        }
        return resetKey;
    }

    public void setResetKey(StringFilter resetKey) {
        this.resetKey = resetKey;
    }

    public InstantFilter getResetDate() {
        return resetDate;
    }

    public Optional<InstantFilter> optionalResetDate() {
        return Optional.ofNullable(resetDate);
    }

    public InstantFilter resetDate() {
        if (resetDate == null) {
            setResetDate(new InstantFilter());
        }
        return resetDate;
    }

    public void setResetDate(InstantFilter resetDate) {
        this.resetDate = resetDate;
    }

    public LongFilter getSystemUserId() {
        return systemUserId;
    }

    public Optional<LongFilter> optionalSystemUserId() {
        return Optional.ofNullable(systemUserId);
    }

    public LongFilter systemUserId() {
        if (systemUserId == null) {
            setSystemUserId(new LongFilter());
        }
        return systemUserId;
    }

    public void setSystemUserId(LongFilter systemUserId) {
        this.systemUserId = systemUserId;
    }

    public LongFilter getInstitutionId() {
        return institutionId;
    }

    public Optional<LongFilter> optionalInstitutionId() {
        return Optional.ofNullable(institutionId);
    }

    public LongFilter institutionId() {
        if (institutionId == null) {
            setInstitutionId(new LongFilter());
        }
        return institutionId;
    }

    public void setInstitutionId(LongFilter institutionId) {
        this.institutionId = institutionId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ApplicationUserCriteria that = (ApplicationUserCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(username, that.username) &&
            Objects.equals(firstName, that.firstName) &&
            Objects.equals(lastName, that.lastName) &&
            Objects.equals(email, that.email) &&
            Objects.equals(activated, that.activated) &&
            Objects.equals(langKey, that.langKey) &&
            Objects.equals(imageUrl, that.imageUrl) &&
            Objects.equals(activationKey, that.activationKey) &&
            Objects.equals(resetKey, that.resetKey) &&
            Objects.equals(resetDate, that.resetDate) &&
            Objects.equals(systemUserId, that.systemUserId) &&
            Objects.equals(institutionId, that.institutionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            username,
            firstName,
            lastName,
            email,
            activated,
            langKey,
            imageUrl,
            activationKey,
            resetKey,
            resetDate,
            systemUserId,
            institutionId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApplicationUserCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalUsername().map(f -> "username=" + f + ", ").orElse("") +
            optionalFirstName().map(f -> "firstName=" + f + ", ").orElse("") +
            optionalLastName().map(f -> "lastName=" + f + ", ").orElse("") +
            optionalEmail().map(f -> "email=" + f + ", ").orElse("") +
            optionalActivated().map(f -> "activated=" + f + ", ").orElse("") +
            optionalLangKey().map(f -> "langKey=" + f + ", ").orElse("") +
            optionalImageUrl().map(f -> "imageUrl=" + f + ", ").orElse("") +
            optionalActivationKey().map(f -> "activationKey=" + f + ", ").orElse("") +
            optionalResetKey().map(f -> "resetKey=" + f + ", ").orElse("") +
            optionalResetDate().map(f -> "resetDate=" + f + ", ").orElse("") +
            optionalSystemUserId().map(f -> "systemUserId=" + f + ", ").orElse("") +
            optionalInstitutionId().map(f -> "institutionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
