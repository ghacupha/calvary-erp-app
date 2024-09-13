package io.github.erp.service;

import io.github.erp.domain.criteria.InstitutionalSubscriptionCriteria;
import io.github.erp.service.dto.InstitutionalSubscriptionDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link io.github.erp.domain.InstitutionalSubscription}.
 */
public interface InstitutionalSubscriptionService {
    /**
     * Save a institutionalSubscription.
     *
     * @param institutionalSubscriptionDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<InstitutionalSubscriptionDTO> save(InstitutionalSubscriptionDTO institutionalSubscriptionDTO);

    /**
     * Updates a institutionalSubscription.
     *
     * @param institutionalSubscriptionDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<InstitutionalSubscriptionDTO> update(InstitutionalSubscriptionDTO institutionalSubscriptionDTO);

    /**
     * Partially updates a institutionalSubscription.
     *
     * @param institutionalSubscriptionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<InstitutionalSubscriptionDTO> partialUpdate(InstitutionalSubscriptionDTO institutionalSubscriptionDTO);
    /**
     * Find institutionalSubscriptions by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<InstitutionalSubscriptionDTO> findByCriteria(InstitutionalSubscriptionCriteria criteria, Pageable pageable);

    /**
     * Find the count of institutionalSubscriptions by criteria.
     * @param criteria filtering criteria
     * @return the count of institutionalSubscriptions
     */
    public Mono<Long> countByCriteria(InstitutionalSubscriptionCriteria criteria);

    /**
     * Get all the institutionalSubscriptions with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<InstitutionalSubscriptionDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of institutionalSubscriptions available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of institutionalSubscriptions available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" institutionalSubscription.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<InstitutionalSubscriptionDTO> findOne(Long id);

    /**
     * Delete the "id" institutionalSubscription.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the institutionalSubscription corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<InstitutionalSubscriptionDTO> search(String query, Pageable pageable);
}
