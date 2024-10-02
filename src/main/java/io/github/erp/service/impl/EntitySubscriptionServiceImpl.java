package io.github.erp.service.impl;

import io.github.erp.domain.criteria.EntitySubscriptionCriteria;
import io.github.erp.repository.EntitySubscriptionRepository;
import io.github.erp.repository.search.EntitySubscriptionSearchRepository;
import io.github.erp.service.EntitySubscriptionService;
import io.github.erp.service.dto.EntitySubscriptionDTO;
import io.github.erp.service.mapper.EntitySubscriptionMapper;
import java.time.ZonedDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link io.github.erp.domain.EntitySubscription}.
 */
@Service
@Transactional
public class EntitySubscriptionServiceImpl implements EntitySubscriptionService {

    private static final Logger LOG = LoggerFactory.getLogger(EntitySubscriptionServiceImpl.class);

    private final EntitySubscriptionRepository entitySubscriptionRepository;

    private final EntitySubscriptionMapper entitySubscriptionMapper;

    private final EntitySubscriptionSearchRepository entitySubscriptionSearchRepository;

    public EntitySubscriptionServiceImpl(
        EntitySubscriptionRepository entitySubscriptionRepository,
        EntitySubscriptionMapper entitySubscriptionMapper,
        EntitySubscriptionSearchRepository entitySubscriptionSearchRepository
    ) {
        this.entitySubscriptionRepository = entitySubscriptionRepository;
        this.entitySubscriptionMapper = entitySubscriptionMapper;
        this.entitySubscriptionSearchRepository = entitySubscriptionSearchRepository;
    }

    @Override
    public Mono<Boolean> hasValidSubscription(Long institutionId) {
        return entitySubscriptionRepository.findByInstitutionIdAndEndDateAfter(institutionId, ZonedDateTime.now()).hasElements();
    }

    @Override
    public Mono<EntitySubscriptionDTO> save(EntitySubscriptionDTO entitySubscriptionDTO) {
        LOG.debug("Request to save EntitySubscription : {}", entitySubscriptionDTO);
        return entitySubscriptionRepository
            .save(entitySubscriptionMapper.toEntity(entitySubscriptionDTO))
            .flatMap(entitySubscriptionSearchRepository::save)
            .map(entitySubscriptionMapper::toDto);
    }

    @Override
    public Mono<EntitySubscriptionDTO> update(EntitySubscriptionDTO entitySubscriptionDTO) {
        LOG.debug("Request to update EntitySubscription : {}", entitySubscriptionDTO);
        return entitySubscriptionRepository
            .save(entitySubscriptionMapper.toEntity(entitySubscriptionDTO))
            .flatMap(entitySubscriptionSearchRepository::save)
            .map(entitySubscriptionMapper::toDto);
    }

    @Override
    public Mono<EntitySubscriptionDTO> partialUpdate(EntitySubscriptionDTO entitySubscriptionDTO) {
        LOG.debug("Request to partially update EntitySubscription : {}", entitySubscriptionDTO);

        return entitySubscriptionRepository
            .findById(entitySubscriptionDTO.getId())
            .map(existingEntitySubscription -> {
                entitySubscriptionMapper.partialUpdate(existingEntitySubscription, entitySubscriptionDTO);

                return existingEntitySubscription;
            })
            .flatMap(entitySubscriptionRepository::save)
            .flatMap(savedEntitySubscription -> {
                entitySubscriptionSearchRepository.save(savedEntitySubscription);
                return Mono.just(savedEntitySubscription);
            })
            .map(entitySubscriptionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EntitySubscriptionDTO> findByCriteria(EntitySubscriptionCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all EntitySubscriptions by Criteria");
        return entitySubscriptionRepository.findByCriteria(criteria, pageable).map(entitySubscriptionMapper::toDto);
    }

    /**
     * Find the count of entitySubscriptions by criteria.
     * @param criteria filtering criteria
     * @return the count of entitySubscriptions
     */
    public Mono<Long> countByCriteria(EntitySubscriptionCriteria criteria) {
        LOG.debug("Request to get the count of all EntitySubscriptions by Criteria");
        return entitySubscriptionRepository.countByCriteria(criteria);
    }

    public Flux<EntitySubscriptionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return entitySubscriptionRepository.findAllWithEagerRelationships(pageable).map(entitySubscriptionMapper::toDto);
    }

    public Mono<Long> countAll() {
        return entitySubscriptionRepository.count();
    }

    public Mono<Long> searchCount() {
        return entitySubscriptionSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EntitySubscriptionDTO> findOne(Long id) {
        LOG.debug("Request to get EntitySubscription : {}", id);
        return entitySubscriptionRepository.findOneWithEagerRelationships(id).map(entitySubscriptionMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete EntitySubscription : {}", id);
        return entitySubscriptionRepository.deleteById(id).then(entitySubscriptionSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EntitySubscriptionDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of EntitySubscriptions for query {}", query);
        return entitySubscriptionSearchRepository.search(query, pageable).map(entitySubscriptionMapper::toDto);
    }
}
