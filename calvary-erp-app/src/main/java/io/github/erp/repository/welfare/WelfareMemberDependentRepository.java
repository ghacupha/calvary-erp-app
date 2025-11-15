package io.github.erp.repository.welfare;

import io.github.erp.domain.welfare.WelfareMemberDependent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link WelfareMemberDependent} entity.
 */
@Repository
public interface WelfareMemberDependentRepository extends JpaRepository<WelfareMemberDependent, Long> {}
