package io.github.erp.repository;

import io.github.erp.domain.ApplicationUser;
import io.github.erp.domain.criteria.ApplicationUserCriteria;
import io.github.erp.repository.rowmapper.ApplicationUserRowMapper;
import io.github.erp.repository.rowmapper.ColumnConverter;
import io.github.erp.repository.rowmapper.UserRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the ApplicationUser entity.
 */
@SuppressWarnings("unused")
class ApplicationUserRepositoryInternalImpl
    extends SimpleR2dbcRepository<ApplicationUser, Long>
    implements ApplicationUserRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper userMapper;
    private final ApplicationUserRowMapper applicationuserMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("application_user", EntityManager.ENTITY_ALIAS);
    private static final Table systemUserTable = Table.aliased("jhi_user", "systemUser");

    public ApplicationUserRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        ApplicationUserRowMapper applicationuserMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(ApplicationUser.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.applicationuserMapper = applicationuserMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<ApplicationUser> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<ApplicationUser> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ApplicationUserSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(systemUserTable, "systemUser"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(systemUserTable)
            .on(Column.create("system_user_id", entityTable))
            .equals(Column.create("id", systemUserTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, ApplicationUser.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<ApplicationUser> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<ApplicationUser> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<ApplicationUser> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<ApplicationUser> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<ApplicationUser> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private ApplicationUser process(Row row, RowMetadata metadata) {
        ApplicationUser entity = applicationuserMapper.apply(row, "e");
        entity.setSystemUser(userMapper.apply(row, "systemUser"));
        return entity;
    }

    @Override
    public <S extends ApplicationUser> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<ApplicationUser> findByCriteria(ApplicationUserCriteria applicationUserCriteria, Pageable page) {
        return createQuery(page, buildConditions(applicationUserCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(ApplicationUserCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(ApplicationUserCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getUsername() != null) {
                builder.buildFilterConditionForField(criteria.getUsername(), entityTable.column("username"));
            }
            if (criteria.getFirstName() != null) {
                builder.buildFilterConditionForField(criteria.getFirstName(), entityTable.column("first_name"));
            }
            if (criteria.getLastName() != null) {
                builder.buildFilterConditionForField(criteria.getLastName(), entityTable.column("last_name"));
            }
            if (criteria.getEmail() != null) {
                builder.buildFilterConditionForField(criteria.getEmail(), entityTable.column("email"));
            }
            if (criteria.getActivated() != null) {
                builder.buildFilterConditionForField(criteria.getActivated(), entityTable.column("activated"));
            }
            if (criteria.getLangKey() != null) {
                builder.buildFilterConditionForField(criteria.getLangKey(), entityTable.column("lang_key"));
            }
            if (criteria.getImageUrl() != null) {
                builder.buildFilterConditionForField(criteria.getImageUrl(), entityTable.column("image_url"));
            }
            if (criteria.getActivationKey() != null) {
                builder.buildFilterConditionForField(criteria.getActivationKey(), entityTable.column("activation_key"));
            }
            if (criteria.getResetKey() != null) {
                builder.buildFilterConditionForField(criteria.getResetKey(), entityTable.column("reset_key"));
            }
            if (criteria.getResetDate() != null) {
                builder.buildFilterConditionForField(criteria.getResetDate(), entityTable.column("reset_date"));
            }
            if (criteria.getSystemUserId() != null) {
                builder.buildFilterConditionForField(criteria.getSystemUserId(), systemUserTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
