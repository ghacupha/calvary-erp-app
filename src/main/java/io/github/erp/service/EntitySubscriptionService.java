package io.github.erp.service;

import io.github.erp.domain.criteria.EntitySubscriptionCriteria;
import io.github.erp.service.dto.EntitySubscriptionDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    Mono<EntitySubscriptionDTO> save(EntitySubscriptionDTO entitySubscriptionDTO);

    /**
     * Updates a entitySubscription.
     *
     * @param entitySubscriptionDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<EntitySubscriptionDTO> update(EntitySubscriptionDTO entitySubscriptionDTO);

    /**
     * Partially updates a entitySubscription.
     *
     * @param entitySubscriptionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<EntitySubscriptionDTO> partialUpdate(EntitySubscriptionDTO entitySubscriptionDTO);
    /**
     * Find entitySubscriptions by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<EntitySubscriptionDTO> findByCriteria(EntitySubscriptionCriteria criteria, Pageable pageable);

    /**
     * Find the count of entitySubscriptions by criteria.
     * @param criteria filtering criteria
     * @return the count of entitySubscriptions
     */
    public Mono<Long> countByCriteria(EntitySubscriptionCriteria criteria);

    /**
     * Returns the number of entitySubscriptions available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of entitySubscriptions available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" entitySubscription.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<EntitySubscriptionDTO> findOne(Long id);

    /**
     * Delete the "id" entitySubscription.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the entitySubscription corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<EntitySubscriptionDTO> search(String query, Pageable pageable);
}
