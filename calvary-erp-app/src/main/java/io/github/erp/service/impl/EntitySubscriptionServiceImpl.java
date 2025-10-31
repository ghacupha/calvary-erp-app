package io.github.erp.service.impl;

import io.github.erp.domain.EntitySubscription;
import io.github.erp.repository.EntitySubscriptionRepository;
import io.github.erp.repository.search.EntitySubscriptionSearchRepository;
import io.github.erp.service.EntitySubscriptionService;
import io.github.erp.service.dto.EntitySubscriptionDTO;
import io.github.erp.service.mapper.EntitySubscriptionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public EntitySubscriptionDTO save(EntitySubscriptionDTO entitySubscriptionDTO) {
        LOG.debug("Request to save EntitySubscription : {}", entitySubscriptionDTO);
        EntitySubscription entitySubscription = entitySubscriptionMapper.toEntity(entitySubscriptionDTO);
        entitySubscription = entitySubscriptionRepository.save(entitySubscription);
        entitySubscriptionSearchRepository.index(entitySubscription);
        return entitySubscriptionMapper.toDto(entitySubscription);
    }

    @Override
    public EntitySubscriptionDTO update(EntitySubscriptionDTO entitySubscriptionDTO) {
        LOG.debug("Request to update EntitySubscription : {}", entitySubscriptionDTO);
        EntitySubscription entitySubscription = entitySubscriptionMapper.toEntity(entitySubscriptionDTO);
        entitySubscription = entitySubscriptionRepository.save(entitySubscription);
        entitySubscriptionSearchRepository.index(entitySubscription);
        return entitySubscriptionMapper.toDto(entitySubscription);
    }

    @Override
    public Optional<EntitySubscriptionDTO> partialUpdate(EntitySubscriptionDTO entitySubscriptionDTO) {
        LOG.debug("Request to partially update EntitySubscription : {}", entitySubscriptionDTO);

        return entitySubscriptionRepository
            .findById(entitySubscriptionDTO.getId())
            .map(existingEntitySubscription -> {
                entitySubscriptionMapper.partialUpdate(existingEntitySubscription, entitySubscriptionDTO);

                return existingEntitySubscription;
            })
            .map(entitySubscriptionRepository::save)
            .map(savedEntitySubscription -> {
                entitySubscriptionSearchRepository.index(savedEntitySubscription);
                return savedEntitySubscription;
            })
            .map(entitySubscriptionMapper::toDto);
    }

    public Page<EntitySubscriptionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return entitySubscriptionRepository.findAllWithEagerRelationships(pageable).map(entitySubscriptionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EntitySubscriptionDTO> findOne(Long id) {
        LOG.debug("Request to get EntitySubscription : {}", id);
        return entitySubscriptionRepository.findOneWithEagerRelationships(id).map(entitySubscriptionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete EntitySubscription : {}", id);
        entitySubscriptionRepository.deleteById(id);
        entitySubscriptionSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EntitySubscriptionDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of EntitySubscriptions for query {}", query);
        return entitySubscriptionSearchRepository.search(query, pageable).map(entitySubscriptionMapper::toDto);
    }
}
