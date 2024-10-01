package io.github.erp.repository;

import io.github.erp.domain.ApplicationUser;
import io.github.erp.domain.criteria.ApplicationUserCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ApplicationUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ApplicationUserRepository extends ReactiveCrudRepository<ApplicationUser, Long>, ApplicationUserRepositoryInternal {
    Flux<ApplicationUser> findAllBy(Pageable pageable);

    @Override
    Mono<ApplicationUser> findOneWithEagerRelationships(Long id);

    @Override
    Flux<ApplicationUser> findAllWithEagerRelationships();

    @Override
    Flux<ApplicationUser> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM application_user entity WHERE entity.system_user_id = :id")
    Flux<ApplicationUser> findBySystemUser(Long id);

    @Query("SELECT * FROM application_user entity WHERE entity.system_user_id IS NULL")
    Flux<ApplicationUser> findAllWhereSystemUserIsNull();

    @Query("SELECT * FROM application_user entity WHERE entity.institution_id = :id")
    Flux<ApplicationUser> findByInstitution(Long id);

    @Query("SELECT * FROM application_user entity WHERE entity.institution_id IS NULL")
    Flux<ApplicationUser> findAllWhereInstitutionIsNull();

    @Override
    <S extends ApplicationUser> Mono<S> save(S entity);

    @Override
    Flux<ApplicationUser> findAll();

    @Override
    Mono<ApplicationUser> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ApplicationUserRepositoryInternal {
    <S extends ApplicationUser> Mono<S> save(S entity);

    Flux<ApplicationUser> findAllBy(Pageable pageable);

    Flux<ApplicationUser> findAll();

    Mono<ApplicationUser> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ApplicationUser> findAllBy(Pageable pageable, Criteria criteria);
    Flux<ApplicationUser> findByCriteria(ApplicationUserCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(ApplicationUserCriteria criteria);

    Mono<ApplicationUser> findOneWithEagerRelationships(Long id);

    Flux<ApplicationUser> findAllWithEagerRelationships();

    Flux<ApplicationUser> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
