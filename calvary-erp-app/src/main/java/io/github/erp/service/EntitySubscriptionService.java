package io.github.erp.service;

import io.github.erp.service.dto.EntitySubscriptionDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.erp.domain.EntitySubscription}.
 */
public interface EntitySubscriptionService {
    /**
     * Save a entitySubscription.
     *
     * @param entitySubscriptionDTO the entity to save.
     * @return the persisted entity.
     */
    EntitySubscriptionDTO save(EntitySubscriptionDTO entitySubscriptionDTO);

    /**
     * Updates a entitySubscription.
     *
     * @param entitySubscriptionDTO the entity to update.
     * @return the persisted entity.
     */
    EntitySubscriptionDTO update(EntitySubscriptionDTO entitySubscriptionDTO);

    /**
     * Partially updates a entitySubscription.
     *
     * @param entitySubscriptionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<EntitySubscriptionDTO> partialUpdate(EntitySubscriptionDTO entitySubscriptionDTO);

    /**
     * Get all the entitySubscriptions with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EntitySubscriptionDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" entitySubscription.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EntitySubscriptionDTO> findOne(Long id);

    /**
     * Delete the "id" entitySubscription.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the entitySubscription corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EntitySubscriptionDTO> search(String query, Pageable pageable);
}
