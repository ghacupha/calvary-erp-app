package io.github.erp.repository;

import io.github.erp.domain.Institution;
import io.github.erp.domain.criteria.InstitutionCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Institution entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InstitutionRepository extends ReactiveCrudRepository<Institution, Long>, InstitutionRepositoryInternal {
    Flux<Institution> findAllBy(Pageable pageable);

    @Override
    Mono<Institution> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Institution> findAllWithEagerRelationships();

    @Override
    Flux<Institution> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM institution entity WHERE entity.parent_institution_id = :id")
    Flux<Institution> findByParentInstitution(Long id);

    @Query("SELECT * FROM institution entity WHERE entity.parent_institution_id IS NULL")
    Flux<Institution> findAllWhereParentInstitutionIsNull();

    @Override
    <S extends Institution> Mono<S> save(S entity);

    @Override
    Flux<Institution> findAll();

    @Override
    Mono<Institution> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface InstitutionRepositoryInternal {
    <S extends Institution> Mono<S> save(S entity);

    Flux<Institution> findAllBy(Pageable pageable);

    Flux<Institution> findAll();

    Mono<Institution> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Institution> findAllBy(Pageable pageable, Criteria criteria);
    Flux<Institution> findByCriteria(InstitutionCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(InstitutionCriteria criteria);

    Mono<Institution> findOneWithEagerRelationships(Long id);

    Flux<Institution> findAllWithEagerRelationships();

    Flux<Institution> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
