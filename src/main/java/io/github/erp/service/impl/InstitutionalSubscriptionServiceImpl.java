package io.github.erp.service.impl;

import io.github.erp.domain.criteria.InstitutionalSubscriptionCriteria;
import io.github.erp.repository.InstitutionalSubscriptionRepository;
import io.github.erp.repository.search.InstitutionalSubscriptionSearchRepository;
import io.github.erp.service.InstitutionalSubscriptionService;
import io.github.erp.service.dto.InstitutionalSubscriptionDTO;
import io.github.erp.service.mapper.InstitutionalSubscriptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link io.github.erp.domain.InstitutionalSubscription}.
 */
@Service
@Transactional
public class InstitutionalSubscriptionServiceImpl implements InstitutionalSubscriptionService {

    private static final Logger LOG = LoggerFactory.getLogger(InstitutionalSubscriptionServiceImpl.class);

    private final InstitutionalSubscriptionRepository institutionalSubscriptionRepository;

    private final InstitutionalSubscriptionMapper institutionalSubscriptionMapper;

    private final InstitutionalSubscriptionSearchRepository institutionalSubscriptionSearchRepository;

    public InstitutionalSubscriptionServiceImpl(
        InstitutionalSubscriptionRepository institutionalSubscriptionRepository,
        InstitutionalSubscriptionMapper institutionalSubscriptionMapper,
        InstitutionalSubscriptionSearchRepository institutionalSubscriptionSearchRepository
    ) {
        this.institutionalSubscriptionRepository = institutionalSubscriptionRepository;
        this.institutionalSubscriptionMapper = institutionalSubscriptionMapper;
        this.institutionalSubscriptionSearchRepository = institutionalSubscriptionSearchRepository;
    }

    @Override
    public Mono<InstitutionalSubscriptionDTO> save(InstitutionalSubscriptionDTO institutionalSubscriptionDTO) {
        LOG.debug("Request to save InstitutionalSubscription : {}", institutionalSubscriptionDTO);
        return institutionalSubscriptionRepository
            .save(institutionalSubscriptionMapper.toEntity(institutionalSubscriptionDTO))
            .flatMap(institutionalSubscriptionSearchRepository::save)
            .map(institutionalSubscriptionMapper::toDto);
    }

    @Override
    public Mono<InstitutionalSubscriptionDTO> update(InstitutionalSubscriptionDTO institutionalSubscriptionDTO) {
        LOG.debug("Request to update InstitutionalSubscription : {}", institutionalSubscriptionDTO);
        return institutionalSubscriptionRepository
            .save(institutionalSubscriptionMapper.toEntity(institutionalSubscriptionDTO))
            .flatMap(institutionalSubscriptionSearchRepository::save)
            .map(institutionalSubscriptionMapper::toDto);
    }

    @Override
    public Mono<InstitutionalSubscriptionDTO> partialUpdate(InstitutionalSubscriptionDTO institutionalSubscriptionDTO) {
        LOG.debug("Request to partially update InstitutionalSubscription : {}", institutionalSubscriptionDTO);

        return institutionalSubscriptionRepository
            .findById(institutionalSubscriptionDTO.getId())
            .map(existingInstitutionalSubscription -> {
                institutionalSubscriptionMapper.partialUpdate(existingInstitutionalSubscription, institutionalSubscriptionDTO);

                return existingInstitutionalSubscription;
            })
            .flatMap(institutionalSubscriptionRepository::save)
            .flatMap(savedInstitutionalSubscription -> {
                institutionalSubscriptionSearchRepository.save(savedInstitutionalSubscription);
                return Mono.just(savedInstitutionalSubscription);
            })
            .map(institutionalSubscriptionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<InstitutionalSubscriptionDTO> findByCriteria(InstitutionalSubscriptionCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all InstitutionalSubscriptions by Criteria");
        return institutionalSubscriptionRepository.findByCriteria(criteria, pageable).map(institutionalSubscriptionMapper::toDto);
    }

    /**
     * Find the count of institutionalSubscriptions by criteria.
     * @param criteria filtering criteria
     * @return the count of institutionalSubscriptions
     */
    public Mono<Long> countByCriteria(InstitutionalSubscriptionCriteria criteria) {
        LOG.debug("Request to get the count of all InstitutionalSubscriptions by Criteria");
        return institutionalSubscriptionRepository.countByCriteria(criteria);
    }

    public Flux<InstitutionalSubscriptionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return institutionalSubscriptionRepository.findAllWithEagerRelationships(pageable).map(institutionalSubscriptionMapper::toDto);
    }

    public Mono<Long> countAll() {
        return institutionalSubscriptionRepository.count();
    }

    public Mono<Long> searchCount() {
        return institutionalSubscriptionSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<InstitutionalSubscriptionDTO> findOne(Long id) {
        LOG.debug("Request to get InstitutionalSubscription : {}", id);
        return institutionalSubscriptionRepository.findOneWithEagerRelationships(id).map(institutionalSubscriptionMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete InstitutionalSubscription : {}", id);
        return institutionalSubscriptionRepository.deleteById(id).then(institutionalSubscriptionSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<InstitutionalSubscriptionDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of InstitutionalSubscriptions for query {}", query);
        return institutionalSubscriptionSearchRepository.search(query, pageable).map(institutionalSubscriptionMapper::toDto);
    }
}
