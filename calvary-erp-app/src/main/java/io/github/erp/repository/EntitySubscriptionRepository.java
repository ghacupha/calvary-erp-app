package io.github.erp.repository;

import io.github.erp.domain.EntitySubscription;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EntitySubscription entity.
 */
@Repository
public interface EntitySubscriptionRepository
    extends JpaRepository<EntitySubscription, Long>, JpaSpecificationExecutor<EntitySubscription> {
    default Optional<EntitySubscription> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<EntitySubscription> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<EntitySubscription> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select entitySubscription from EntitySubscription entitySubscription left join fetch entitySubscription.institution",
        countQuery = "select count(entitySubscription) from EntitySubscription entitySubscription"
    )
    Page<EntitySubscription> findAllWithToOneRelationships(Pageable pageable);

    @Query("select entitySubscription from EntitySubscription entitySubscription left join fetch entitySubscription.institution")
    List<EntitySubscription> findAllWithToOneRelationships();

    @Query(
        "select entitySubscription from EntitySubscription entitySubscription left join fetch entitySubscription.institution where entitySubscription.id =:id"
    )
    Optional<EntitySubscription> findOneWithToOneRelationships(@Param("id") Long id);
}
