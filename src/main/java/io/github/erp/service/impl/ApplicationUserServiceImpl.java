package io.github.erp.service.impl;

import io.github.erp.domain.criteria.ApplicationUserCriteria;
import io.github.erp.repository.ApplicationUserRepository;
import io.github.erp.repository.search.ApplicationUserSearchRepository;
import io.github.erp.service.ApplicationUserService;
import io.github.erp.service.dto.ApplicationUserDTO;
import io.github.erp.service.mapper.ApplicationUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link io.github.erp.domain.ApplicationUser}.
 */
@Service
@Transactional
public class ApplicationUserServiceImpl implements ApplicationUserService {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationUserServiceImpl.class);

    private final ApplicationUserRepository applicationUserRepository;

    private final ApplicationUserMapper applicationUserMapper;

    private final ApplicationUserSearchRepository applicationUserSearchRepository;

    public ApplicationUserServiceImpl(
        ApplicationUserRepository applicationUserRepository,
        ApplicationUserMapper applicationUserMapper,
        ApplicationUserSearchRepository applicationUserSearchRepository
    ) {
        this.applicationUserRepository = applicationUserRepository;
        this.applicationUserMapper = applicationUserMapper;
        this.applicationUserSearchRepository = applicationUserSearchRepository;
    }

    @Override
    public Mono<ApplicationUserDTO> save(ApplicationUserDTO applicationUserDTO) {
        LOG.debug("Request to save ApplicationUser : {}", applicationUserDTO);
        return applicationUserRepository
            .save(applicationUserMapper.toEntity(applicationUserDTO))
            .flatMap(applicationUserSearchRepository::save)
            .map(applicationUserMapper::toDto);
    }

    @Override
    public Mono<ApplicationUserDTO> update(ApplicationUserDTO applicationUserDTO) {
        LOG.debug("Request to update ApplicationUser : {}", applicationUserDTO);
        return applicationUserRepository
            .save(applicationUserMapper.toEntity(applicationUserDTO))
            .flatMap(applicationUserSearchRepository::save)
            .map(applicationUserMapper::toDto);
    }

    @Override
    public Mono<ApplicationUserDTO> partialUpdate(ApplicationUserDTO applicationUserDTO) {
        LOG.debug("Request to partially update ApplicationUser : {}", applicationUserDTO);

        return applicationUserRepository
            .findById(applicationUserDTO.getId())
            .map(existingApplicationUser -> {
                applicationUserMapper.partialUpdate(existingApplicationUser, applicationUserDTO);

                return existingApplicationUser;
            })
            .flatMap(applicationUserRepository::save)
            .flatMap(savedApplicationUser -> {
                applicationUserSearchRepository.save(savedApplicationUser);
                return Mono.just(savedApplicationUser);
            })
            .map(applicationUserMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ApplicationUserDTO> findByCriteria(ApplicationUserCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all ApplicationUsers by Criteria");
        return applicationUserRepository.findByCriteria(criteria, pageable).map(applicationUserMapper::toDto);
    }

    /**
     * Find the count of applicationUsers by criteria.
     * @param criteria filtering criteria
     * @return the count of applicationUsers
     */
    public Mono<Long> countByCriteria(ApplicationUserCriteria criteria) {
        LOG.debug("Request to get the count of all ApplicationUsers by Criteria");
        return applicationUserRepository.countByCriteria(criteria);
    }

    public Flux<ApplicationUserDTO> findAllWithEagerRelationships(Pageable pageable) {
        return applicationUserRepository.findAllWithEagerRelationships(pageable).map(applicationUserMapper::toDto);
    }

    public Mono<Long> countAll() {
        return applicationUserRepository.count();
    }

    public Mono<Long> searchCount() {
        return applicationUserSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ApplicationUserDTO> findOne(Long id) {
        LOG.debug("Request to get ApplicationUser : {}", id);
        return applicationUserRepository.findOneWithEagerRelationships(id).map(applicationUserMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete ApplicationUser : {}", id);
        return applicationUserRepository.deleteById(id).then(applicationUserSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ApplicationUserDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of ApplicationUsers for query {}", query);
        return applicationUserSearchRepository.search(query, pageable).map(applicationUserMapper::toDto);
    }
}
