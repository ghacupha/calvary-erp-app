package io.github.erp.service.welfare;

import io.github.erp.domain.welfare.*; // for static metamodels
import io.github.erp.domain.welfare.WelfareMembershipRegistration;
import io.github.erp.repository.welfare.WelfareMembershipRegistrationRepository;
import io.github.erp.service.criteria.welfare.WelfareMembershipRegistrationCriteria;
import io.github.erp.service.dto.welfare.WelfareMembershipRegistrationDTO;
import io.github.erp.service.mapper.welfare.WelfareMembershipRegistrationMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link WelfareMembershipRegistration} entities in the database.
 */
@Service
@Transactional(readOnly = true)
public class WelfareMembershipRegistrationQueryService extends QueryService<WelfareMembershipRegistration> {

    private static final Logger LOG = LoggerFactory.getLogger(WelfareMembershipRegistrationQueryService.class);

    private final WelfareMembershipRegistrationRepository welfareMembershipRegistrationRepository;

    private final WelfareMembershipRegistrationMapper welfareMembershipRegistrationMapper;

    public WelfareMembershipRegistrationQueryService(
        WelfareMembershipRegistrationRepository welfareMembershipRegistrationRepository,
        WelfareMembershipRegistrationMapper welfareMembershipRegistrationMapper
    ) {
        this.welfareMembershipRegistrationRepository = welfareMembershipRegistrationRepository;
        this.welfareMembershipRegistrationMapper = welfareMembershipRegistrationMapper;
    }

    /**
     * Return a {@link Page} of {@link WelfareMembershipRegistrationDTO} matching the criteria.
     */
    public Page<WelfareMembershipRegistrationDTO> findByCriteria(
        WelfareMembershipRegistrationCriteria criteria,
        Pageable page
    ) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<WelfareMembershipRegistration> specification = createSpecification(criteria);
        return welfareMembershipRegistrationRepository.findAll(specification, page).map(welfareMembershipRegistrationMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     */
    public long countByCriteria(WelfareMembershipRegistrationCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<WelfareMembershipRegistration> specification = createSpecification(criteria);
        return welfareMembershipRegistrationRepository.count(specification);
    }

    /**
     * Function to convert {@link WelfareMembershipRegistrationCriteria} to a {@link Specification}.
     */
    protected Specification<WelfareMembershipRegistration> createSpecification(WelfareMembershipRegistrationCriteria criteria) {
        Specification<WelfareMembershipRegistration> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), WelfareMembershipRegistration_.id));
            }
            if (criteria.getApplicantFirstName() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getApplicantFirstName(), WelfareMembershipRegistration_.applicantFirstName)
                );
            }
            if (criteria.getApplicantLastName() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getApplicantLastName(), WelfareMembershipRegistration_.applicantLastName)
                );
            }
            if (criteria.getApplicantEmail() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getApplicantEmail(), WelfareMembershipRegistration_.applicantEmail)
                );
            }
            if (criteria.getPhoneNumber() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getPhoneNumber(), WelfareMembershipRegistration_.phoneNumber)
                );
            }
            if (criteria.getCity() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCity(), WelfareMembershipRegistration_.city));
            }
            if (criteria.getStateProvince() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getStateProvince(), WelfareMembershipRegistration_.stateProvince)
                );
            }
            if (criteria.getMembershipType() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getMembershipType(), WelfareMembershipRegistration_.membershipType)
                );
            }
            if (criteria.getSubmittedAt() != null) {
                specification = specification.and(
                    buildRangeSpecification(criteria.getSubmittedAt(), WelfareMembershipRegistration_.submittedAt)
                );
            }
            if (criteria.getDependentsId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getDependentsId(), root ->
                        root.join(WelfareMembershipRegistration_.dependents, JoinType.LEFT).get(WelfareMemberDependent_.id)
                    )
                );
            }
        }
        return specification;
    }
}
