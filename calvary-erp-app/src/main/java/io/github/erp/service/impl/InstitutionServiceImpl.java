package io.github.erp.service.impl;

import io.github.erp.domain.Institution;
import io.github.erp.repository.InstitutionRepository;
import io.github.erp.repository.search.InstitutionSearchRepository;
import io.github.erp.service.InstitutionService;
import io.github.erp.service.dto.InstitutionDTO;
import io.github.erp.service.mapper.InstitutionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public InstitutionDTO save(InstitutionDTO institutionDTO) {
        LOG.debug("Request to save Institution : {}", institutionDTO);
        Institution institution = institutionMapper.toEntity(institutionDTO);
        institution = institutionRepository.save(institution);
        institutionSearchRepository.index(institution);
        return institutionMapper.toDto(institution);
    }

    @Override
    public InstitutionDTO update(InstitutionDTO institutionDTO) {
        LOG.debug("Request to update Institution : {}", institutionDTO);
        Institution institution = institutionMapper.toEntity(institutionDTO);
        institution = institutionRepository.save(institution);
        institutionSearchRepository.index(institution);
        return institutionMapper.toDto(institution);
    }

    @Override
    public Optional<InstitutionDTO> partialUpdate(InstitutionDTO institutionDTO) {
        LOG.debug("Request to partially update Institution : {}", institutionDTO);

        return institutionRepository
            .findById(institutionDTO.getId())
            .map(existingInstitution -> {
                institutionMapper.partialUpdate(existingInstitution, institutionDTO);

                return existingInstitution;
            })
            .map(institutionRepository::save)
            .map(savedInstitution -> {
                institutionSearchRepository.index(savedInstitution);
                return savedInstitution;
            })
            .map(institutionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InstitutionDTO> findOne(Long id) {
        LOG.debug("Request to get Institution : {}", id);
        return institutionRepository.findById(id).map(institutionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Institution : {}", id);
        institutionRepository.deleteById(id);
        institutionSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InstitutionDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Institutions for query {}", query);
        return institutionSearchRepository.search(query, pageable).map(institutionMapper::toDto);
    }
}
