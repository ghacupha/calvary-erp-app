package io.github.erp.service.impl.welfare;

import io.github.erp.domain.welfare.WelfareMembershipRegistration;
import io.github.erp.repository.welfare.WelfareMemberDependentRepository;
import io.github.erp.repository.welfare.WelfareMembershipRegistrationRepository;
import io.github.erp.service.dto.welfare.WelfareMembershipRegistrationDTO;
import io.github.erp.service.dto.welfare.WelfareQuestionnaireReportDTO;
import io.github.erp.service.mapper.welfare.WelfareMembershipRegistrationMapper;
import io.github.erp.service.welfare.WelfareMembershipRegistrationService;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.github.erp.domain.welfare.WelfareMembershipRegistration}.
 */
@Service
@Transactional
public class WelfareMembershipRegistrationServiceImpl implements WelfareMembershipRegistrationService {

    private static final Logger LOG = LoggerFactory.getLogger(WelfareMembershipRegistrationServiceImpl.class);

    private final WelfareMembershipRegistrationRepository welfareMembershipRegistrationRepository;

    private final WelfareMemberDependentRepository welfareMemberDependentRepository;

    private final WelfareMembershipRegistrationMapper welfareMembershipRegistrationMapper;

    public WelfareMembershipRegistrationServiceImpl(
        WelfareMembershipRegistrationRepository welfareMembershipRegistrationRepository,
        WelfareMemberDependentRepository welfareMemberDependentRepository,
        WelfareMembershipRegistrationMapper welfareMembershipRegistrationMapper
    ) {
        this.welfareMembershipRegistrationRepository = welfareMembershipRegistrationRepository;
        this.welfareMemberDependentRepository = welfareMemberDependentRepository;
        this.welfareMembershipRegistrationMapper = welfareMembershipRegistrationMapper;
    }

    @Override
    public WelfareMembershipRegistrationDTO save(WelfareMembershipRegistrationDTO registrationDTO) {
        LOG.debug("Request to save WelfareMembershipRegistration : {}", registrationDTO);
        WelfareMembershipRegistration registration = welfareMembershipRegistrationMapper.toEntity(registrationDTO);
        if (registration.getSubmittedAt() == null) {
            registration.setSubmittedAt(Instant.now());
        }
        registration = welfareMembershipRegistrationRepository.save(registration);
        return welfareMembershipRegistrationMapper.toDto(registration);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WelfareMembershipRegistrationDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all WelfareMembershipRegistrations");
        return welfareMembershipRegistrationRepository.findAll(pageable).map(welfareMembershipRegistrationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WelfareMembershipRegistrationDTO> findOne(Long id) {
        LOG.debug("Request to get WelfareMembershipRegistration : {}", id);
        return welfareMembershipRegistrationRepository.findById(id).map(welfareMembershipRegistrationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete WelfareMembershipRegistration : {}", id);
        welfareMembershipRegistrationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public WelfareQuestionnaireReportDTO buildSummaryReport() {
        long totalRegistrations = welfareMembershipRegistrationRepository.count();
        long totalDependents = welfareMemberDependentRepository.count();
        Map<String, Long> membershipBreakdown = welfareMembershipRegistrationRepository
            .countByMembershipType()
            .stream()
            .collect(
                Collectors.toMap(
                    entry -> entry.getMembershipType() == null ? "UNSPECIFIED" : entry.getMembershipType(),
                    WelfareMembershipRegistrationRepository.MembershipTypeCount::getTotal,
                    Long::sum,
                    LinkedHashMap::new
                )
            );
        return new WelfareQuestionnaireReportDTO(totalRegistrations, totalDependents, membershipBreakdown);
    }
}
