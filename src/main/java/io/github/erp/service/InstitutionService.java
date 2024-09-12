package io.github.erp.service;

import io.github.erp.domain.criteria.InstitutionCriteria;
import io.github.erp.service.dto.InstitutionDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link io.github.erp.domain.Institution}.
 */
public interface InstitutionService {
    /**
     * Save a institution.
     *
     * @param institutionDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<InstitutionDTO> save(InstitutionDTO institutionDTO);

    /**
     * Updates a institution.
     *
     * @param institutionDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<InstitutionDTO> update(InstitutionDTO institutionDTO);

    /**
     * Partially updates a institution.
     *
     * @param institutionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<InstitutionDTO> partialUpdate(InstitutionDTO institutionDTO);
    /**
     * Find institutions by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<InstitutionDTO> findByCriteria(InstitutionCriteria criteria, Pageable pageable);

    /**
     * Find the count of institutions by criteria.
     * @param criteria filtering criteria
     * @return the count of institutions
     */
    public Mono<Long> countByCriteria(InstitutionCriteria criteria);

    /**
     * Get all the institutions with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<InstitutionDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of institutions available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of institutions available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" institution.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<InstitutionDTO> findOne(Long id);

    /**
     * Delete the "id" institution.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the institution corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<InstitutionDTO> search(String query, Pageable pageable);
}
