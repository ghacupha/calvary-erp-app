package io.github.erp.service.welfare;

import io.github.erp.service.dto.welfare.WelfareMembershipRegistrationDTO;
import io.github.erp.service.dto.welfare.WelfareQuestionnaireReportDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.erp.domain.welfare.WelfareMembershipRegistration}.
 */
public interface WelfareMembershipRegistrationService {
    /**
     * Save a welfare membership registration.
     *
     * @param registrationDTO the entity to save.
     * @return the persisted entity.
     */
    WelfareMembershipRegistrationDTO save(WelfareMembershipRegistrationDTO registrationDTO);

    /**
     * Get all the registrations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<WelfareMembershipRegistrationDTO> findAll(Pageable pageable);

    /**
     * Get the "id" registration.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<WelfareMembershipRegistrationDTO> findOne(Long id);

    /**
     * Delete the "id" registration.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Build an aggregate report for staff consumption.
     *
     * @return summary metrics across all registrations.
     */
    WelfareQuestionnaireReportDTO buildSummaryReport();
}
