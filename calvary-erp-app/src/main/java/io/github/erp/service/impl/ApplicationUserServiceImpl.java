package io.github.erp.service.impl;

import io.github.erp.domain.ApplicationUser;
import io.github.erp.repository.ApplicationUserRepository;
import io.github.erp.repository.search.ApplicationUserSearchRepository;
import io.github.erp.service.ApplicationUserService;
import io.github.erp.service.dto.ApplicationUserDTO;
import io.github.erp.service.mapper.ApplicationUserMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ApplicationUserDTO save(ApplicationUserDTO applicationUserDTO) {
        LOG.debug("Request to save ApplicationUser : {}", applicationUserDTO);
        ApplicationUser applicationUser = applicationUserMapper.toEntity(applicationUserDTO);
        applicationUser = applicationUserRepository.save(applicationUser);
        applicationUserSearchRepository.index(applicationUser);
        return applicationUserMapper.toDto(applicationUser);
    }

    @Override
    public ApplicationUserDTO update(ApplicationUserDTO applicationUserDTO) {
        LOG.debug("Request to update ApplicationUser : {}", applicationUserDTO);
        ApplicationUser applicationUser = applicationUserMapper.toEntity(applicationUserDTO);
        applicationUser = applicationUserRepository.save(applicationUser);
        applicationUserSearchRepository.index(applicationUser);
        return applicationUserMapper.toDto(applicationUser);
    }

    @Override
    public Optional<ApplicationUserDTO> partialUpdate(ApplicationUserDTO applicationUserDTO) {
        LOG.debug("Request to partially update ApplicationUser : {}", applicationUserDTO);

        return applicationUserRepository
            .findById(applicationUserDTO.getId())
            .map(existingApplicationUser -> {
                applicationUserMapper.partialUpdate(existingApplicationUser, applicationUserDTO);

                return existingApplicationUser;
            })
            .map(applicationUserRepository::save)
            .map(savedApplicationUser -> {
                applicationUserSearchRepository.index(savedApplicationUser);
                return savedApplicationUser;
            })
            .map(applicationUserMapper::toDto);
    }

    public Page<ApplicationUserDTO> findAllWithEagerRelationships(Pageable pageable) {
        return applicationUserRepository.findAllWithEagerRelationships(pageable).map(applicationUserMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApplicationUserDTO> findOne(Long id) {
        LOG.debug("Request to get ApplicationUser : {}", id);
        return applicationUserRepository.findOneWithEagerRelationships(id).map(applicationUserMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ApplicationUser : {}", id);
        applicationUserRepository.deleteById(id);
        applicationUserSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationUserDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of ApplicationUsers for query {}", query);
        return applicationUserSearchRepository.search(query, pageable).map(applicationUserMapper::toDto);
    }
}
