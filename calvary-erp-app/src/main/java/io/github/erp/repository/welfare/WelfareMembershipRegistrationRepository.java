package io.github.erp.repository.welfare;

import io.github.erp.domain.welfare.WelfareMembershipRegistration;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link WelfareMembershipRegistration} entity.
 */
@Repository
public interface WelfareMembershipRegistrationRepository
    extends JpaRepository<WelfareMembershipRegistration, Long>, JpaSpecificationExecutor<WelfareMembershipRegistration> {
    @Query(
        "select r.membershipType as membershipType, count(r) as total from WelfareMembershipRegistration r group by r.membershipType"
    )
    List<MembershipTypeCount> countByMembershipType();

    interface MembershipTypeCount {
        String getMembershipType();

        long getTotal();
    }
}
