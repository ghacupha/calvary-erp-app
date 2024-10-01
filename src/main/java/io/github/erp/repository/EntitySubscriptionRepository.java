package io.github.erp.repository;

import io.github.erp.domain.EntitySubscription;
import io.github.erp.domain.criteria.EntitySubscriptionCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the EntitySubscription entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EntitySubscriptionRepository
    extends ReactiveCrudRepository<EntitySubscription, Long>, EntitySubscriptionRepositoryInternal {
    Flux<EntitySubscription> findAllBy(Pageable pageable);

    @Query("SELECT * FROM entity_subscription entity WHERE entity.institution_id = :id")
    Flux<EntitySubscription> findByInstitution(Long id);

    @Query("SELECT * FROM entity_subscription entity WHERE entity.institution_id IS NULL")
    Flux<EntitySubscription> findAllWhereInstitutionIsNull();

    @Override
    <S extends EntitySubscription> Mono<S> save(S entity);

    @Override
    Flux<EntitySubscription> findAll();

    @Override
    Mono<EntitySubscription> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EntitySubscriptionRepositoryInternal {
    <S extends EntitySubscription> Mono<S> save(S entity);

    Flux<EntitySubscription> findAllBy(Pageable pageable);

    Flux<EntitySubscription> findAll();

    Mono<EntitySubscription> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<EntitySubscription> findAllBy(Pageable pageable, Criteria criteria);
    Flux<EntitySubscription> findByCriteria(EntitySubscriptionCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(EntitySubscriptionCriteria criteria);
}
