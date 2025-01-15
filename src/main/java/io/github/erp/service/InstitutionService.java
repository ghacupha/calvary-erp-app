package io.github.erp.service;

import io.github.erp.service.dto.InstitutionDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    InstitutionDTO save(InstitutionDTO institutionDTO);

    /**
     * Updates a institution.
     *
     * @param institutionDTO the entity to update.
     * @return the persisted entity.
     */
    InstitutionDTO update(InstitutionDTO institutionDTO);

    /**
     * Partially updates a institution.
     *
     * @param institutionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<InstitutionDTO> partialUpdate(InstitutionDTO institutionDTO);

    /**
     * Get the "id" institution.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<InstitutionDTO> findOne(Long id);

    /**
     * Delete the "id" institution.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the institution corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<InstitutionDTO> search(String query, Pageable pageable);
}
