package io.github.erp.repository;

import io.github.erp.domain.InstitutionalSubscription;
import io.github.erp.domain.criteria.InstitutionalSubscriptionCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the InstitutionalSubscription entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InstitutionalSubscriptionRepository
    extends ReactiveCrudRepository<InstitutionalSubscription, Long>, InstitutionalSubscriptionRepositoryInternal {
    Flux<InstitutionalSubscription> findAllBy(Pageable pageable);

    @Override
    Mono<InstitutionalSubscription> findOneWithEagerRelationships(Long id);

    @Override
    Flux<InstitutionalSubscription> findAllWithEagerRelationships();

    @Override
    Flux<InstitutionalSubscription> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM institutional_subscription entity WHERE entity.institution_id = :id")
    Flux<InstitutionalSubscription> findByInstitution(Long id);

    @Query("SELECT * FROM institutional_subscription entity WHERE entity.institution_id IS NULL")
    Flux<InstitutionalSubscription> findAllWhereInstitutionIsNull();

    @Override
    <S extends InstitutionalSubscription> Mono<S> save(S entity);

    @Override
    Flux<InstitutionalSubscription> findAll();

    @Override
    Mono<InstitutionalSubscription> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface InstitutionalSubscriptionRepositoryInternal {
    <S extends InstitutionalSubscription> Mono<S> save(S entity);

    Flux<InstitutionalSubscription> findAllBy(Pageable pageable);

    Flux<InstitutionalSubscription> findAll();

    Mono<InstitutionalSubscription> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<InstitutionalSubscription> findAllBy(Pageable pageable, Criteria criteria);
    Flux<InstitutionalSubscription> findByCriteria(InstitutionalSubscriptionCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(InstitutionalSubscriptionCriteria criteria);

    Mono<InstitutionalSubscription> findOneWithEagerRelationships(Long id);

    Flux<InstitutionalSubscription> findAllWithEagerRelationships();

    Flux<InstitutionalSubscription> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
