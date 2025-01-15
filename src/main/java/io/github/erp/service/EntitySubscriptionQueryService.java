package io.github.erp.service;

import io.github.erp.domain.*; // for static metamodels
import io.github.erp.domain.EntitySubscription;
import io.github.erp.repository.EntitySubscriptionRepository;
import io.github.erp.repository.search.EntitySubscriptionSearchRepository;
import io.github.erp.service.criteria.EntitySubscriptionCriteria;
import io.github.erp.service.dto.EntitySubscriptionDTO;
import io.github.erp.service.mapper.EntitySubscriptionMapper;
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
 * Service for executing complex queries for {@link EntitySubscription} entities in the database.
 * The main input is a {@link EntitySubscriptionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link EntitySubscriptionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EntitySubscriptionQueryService extends QueryService<EntitySubscription> {

    private static final Logger LOG = LoggerFactory.getLogger(EntitySubscriptionQueryService.class);

    private final EntitySubscriptionRepository entitySubscriptionRepository;

    private final EntitySubscriptionMapper entitySubscriptionMapper;

    private final EntitySubscriptionSearchRepository entitySubscriptionSearchRepository;

    public EntitySubscriptionQueryService(
        EntitySubscriptionRepository entitySubscriptionRepository,
        EntitySubscriptionMapper entitySubscriptionMapper,
        EntitySubscriptionSearchRepository entitySubscriptionSearchRepository
    ) {
        this.entitySubscriptionRepository = entitySubscriptionRepository;
        this.entitySubscriptionMapper = entitySubscriptionMapper;
        this.entitySubscriptionSearchRepository = entitySubscriptionSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link EntitySubscriptionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EntitySubscriptionDTO> findByCriteria(EntitySubscriptionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<EntitySubscription> specification = createSpecification(criteria);
        return entitySubscriptionRepository.findAll(specification, page).map(entitySubscriptionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EntitySubscriptionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<EntitySubscription> specification = createSpecification(criteria);
        return entitySubscriptionRepository.count(specification);
    }

    /**
     * Function to convert {@link EntitySubscriptionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<EntitySubscription> createSpecification(EntitySubscriptionCriteria criteria) {
        Specification<EntitySubscription> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), EntitySubscription_.id));
            }
            if (criteria.getSubscriptionToken() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getSubscriptionToken(), EntitySubscription_.subscriptionToken)
                );
            }
            if (criteria.getStartDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartDate(), EntitySubscription_.startDate));
            }
            if (criteria.getEndDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndDate(), EntitySubscription_.endDate));
            }
            if (criteria.getInstitutionId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getInstitutionId(), root ->
                        root.join(EntitySubscription_.institution, JoinType.LEFT).get(Institution_.id)
                    )
                );
            }
        }
        return specification;
    }
}
