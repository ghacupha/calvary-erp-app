package io.github.erp.service.impl;

import io.github.erp.domain.criteria.InstitutionCriteria;
import io.github.erp.repository.InstitutionRepository;
import io.github.erp.repository.search.InstitutionSearchRepository;
import io.github.erp.service.InstitutionService;
import io.github.erp.service.dto.InstitutionDTO;
import io.github.erp.service.mapper.InstitutionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link io.github.erp.domain.Institution}.
 */
@Service
@Transactional
public class InstitutionServiceImpl implements InstitutionService {

    private static final Logger LOG = LoggerFactory.getLogger(InstitutionServiceImpl.class);

    private final InstitutionRepository institutionRepository;

    private final InstitutionMapper institutionMapper;

    private final InstitutionSearchRepository institutionSearchRepository;

    public InstitutionServiceImpl(
        InstitutionRepository institutionRepository,
        InstitutionMapper institutionMapper,
        InstitutionSearchRepository institutionSearchRepository
    ) {
        this.institutionRepository = institutionRepository;
        this.institutionMapper = institutionMapper;
        this.institutionSearchRepository = institutionSearchRepository;
    }

    @Override
    public Mono<InstitutionDTO> save(InstitutionDTO institutionDTO) {
        LOG.debug("Request to save Institution : {}", institutionDTO);
        return institutionRepository
            .save(institutionMapper.toEntity(institutionDTO))
            .flatMap(institutionSearchRepository::save)
            .map(institutionMapper::toDto);
    }

    @Override
    public Mono<InstitutionDTO> update(InstitutionDTO institutionDTO) {
        LOG.debug("Request to update Institution : {}", institutionDTO);
        return institutionRepository
            .save(institutionMapper.toEntity(institutionDTO))
            .flatMap(institutionSearchRepository::save)
            .map(institutionMapper::toDto);
    }

    @Override
    public Mono<InstitutionDTO> partialUpdate(InstitutionDTO institutionDTO) {
        LOG.debug("Request to partially update Institution : {}", institutionDTO);

        return institutionRepository
            .findById(institutionDTO.getId())
            .map(existingInstitution -> {
                institutionMapper.partialUpdate(existingInstitution, institutionDTO);

                return existingInstitution;
            })
            .flatMap(institutionRepository::save)
            .flatMap(savedInstitution -> {
                institutionSearchRepository.save(savedInstitution);
                return Mono.just(savedInstitution);
            })
            .map(institutionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<InstitutionDTO> findByCriteria(InstitutionCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Institutions by Criteria");
        return institutionRepository.findByCriteria(criteria, pageable).map(institutionMapper::toDto);
    }

    /**
     * Find the count of institutions by criteria.
     * @param criteria filtering criteria
     * @return the count of institutions
     */
    public Mono<Long> countByCriteria(InstitutionCriteria criteria) {
        LOG.debug("Request to get the count of all Institutions by Criteria");
        return institutionRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return institutionRepository.count();
    }

    public Mono<Long> searchCount() {
        return institutionSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<InstitutionDTO> findOne(Long id) {
        LOG.debug("Request to get Institution : {}", id);
        return institutionRepository.findById(id).map(institutionMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Institution : {}", id);
        return institutionRepository.deleteById(id).then(institutionSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<InstitutionDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Institutions for query {}", query);
        return institutionSearchRepository.search(query, pageable).map(institutionMapper::toDto);
    }
}
