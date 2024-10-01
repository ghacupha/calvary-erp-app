package io.github.erp.repository;

import io.github.erp.domain.EntitySubscription;
import io.github.erp.domain.criteria.EntitySubscriptionCriteria;
import io.github.erp.repository.rowmapper.ColumnConverter;
import io.github.erp.repository.rowmapper.EntitySubscriptionRowMapper;
import io.github.erp.repository.rowmapper.InstitutionRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the EntitySubscription entity.
 */
@SuppressWarnings("unused")
class EntitySubscriptionRepositoryInternalImpl
    extends SimpleR2dbcRepository<EntitySubscription, Long>
    implements EntitySubscriptionRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final InstitutionRowMapper institutionMapper;
    private final EntitySubscriptionRowMapper entitysubscriptionMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("entity_subscription", EntityManager.ENTITY_ALIAS);
    private static final Table institutionTable = Table.aliased("institution", "institution");

    public EntitySubscriptionRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        InstitutionRowMapper institutionMapper,
        EntitySubscriptionRowMapper entitysubscriptionMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(EntitySubscription.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.institutionMapper = institutionMapper;
        this.entitysubscriptionMapper = entitysubscriptionMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<EntitySubscription> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<EntitySubscription> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = EntitySubscriptionSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(InstitutionSqlHelper.getColumns(institutionTable, "institution"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(institutionTable)
            .on(Column.create("institution_id", entityTable))
            .equals(Column.create("id", institutionTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, EntitySubscription.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<EntitySubscription> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<EntitySubscription> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private EntitySubscription process(Row row, RowMetadata metadata) {
        EntitySubscription entity = entitysubscriptionMapper.apply(row, "e");
        entity.setInstitution(institutionMapper.apply(row, "institution"));
        return entity;
    }

    @Override
    public <S extends EntitySubscription> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<EntitySubscription> findByCriteria(EntitySubscriptionCriteria entitySubscriptionCriteria, Pageable page) {
        return createQuery(page, buildConditions(entitySubscriptionCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(EntitySubscriptionCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(EntitySubscriptionCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getSubscriptionToken() != null) {
                builder.buildFilterConditionForField(criteria.getSubscriptionToken(), entityTable.column("subscription_token"));
            }
            if (criteria.getStartDate() != null) {
                builder.buildFilterConditionForField(criteria.getStartDate(), entityTable.column("start_date"));
            }
            if (criteria.getEndDate() != null) {
                builder.buildFilterConditionForField(criteria.getEndDate(), entityTable.column("end_date"));
            }
            if (criteria.getInstitutionId() != null) {
                builder.buildFilterConditionForField(criteria.getInstitutionId(), institutionTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
