package io.github.erp.service;

import io.github.erp.domain.criteria.ApplicationUserCriteria;
import io.github.erp.service.dto.ApplicationUserDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link io.github.erp.domain.ApplicationUser}.
 */
public interface ApplicationUserService {
    /**
     * Save a applicationUser.
     *
     * @param applicationUserDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ApplicationUserDTO> save(ApplicationUserDTO applicationUserDTO);

    /**
     * Updates a applicationUser.
     *
     * @param applicationUserDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ApplicationUserDTO> update(ApplicationUserDTO applicationUserDTO);

    /**
     * Partially updates a applicationUser.
     *
     * @param applicationUserDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ApplicationUserDTO> partialUpdate(ApplicationUserDTO applicationUserDTO);
    /**
     * Find applicationUsers by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ApplicationUserDTO> findByCriteria(ApplicationUserCriteria criteria, Pageable pageable);

    /**
     * Find the count of applicationUsers by criteria.
     * @param criteria filtering criteria
     * @return the count of applicationUsers
     */
    public Mono<Long> countByCriteria(ApplicationUserCriteria criteria);

    /**
     * Get all the applicationUsers with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ApplicationUserDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of applicationUsers available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of applicationUsers available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" applicationUser.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ApplicationUserDTO> findOne(Long id);

    /**
     * Delete the "id" applicationUser.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the applicationUser corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ApplicationUserDTO> search(String query, Pageable pageable);
}
