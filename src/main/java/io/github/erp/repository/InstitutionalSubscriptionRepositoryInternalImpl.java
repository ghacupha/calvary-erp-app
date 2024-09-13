package io.github.erp.repository;

import io.github.erp.domain.InstitutionalSubscription;
import io.github.erp.domain.criteria.InstitutionalSubscriptionCriteria;
import io.github.erp.repository.rowmapper.ColumnConverter;
import io.github.erp.repository.rowmapper.InstitutionRowMapper;
import io.github.erp.repository.rowmapper.InstitutionalSubscriptionRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the InstitutionalSubscription entity.
 */
@SuppressWarnings("unused")
class InstitutionalSubscriptionRepositoryInternalImpl
    extends SimpleR2dbcRepository<InstitutionalSubscription, Long>
    implements InstitutionalSubscriptionRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final InstitutionRowMapper institutionMapper;
    private final InstitutionalSubscriptionRowMapper institutionalsubscriptionMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("institutional_subscription", EntityManager.ENTITY_ALIAS);
    private static final Table institutionTable = Table.aliased("institution", "institution");

    public InstitutionalSubscriptionRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        InstitutionRowMapper institutionMapper,
        InstitutionalSubscriptionRowMapper institutionalsubscriptionMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(
                converter.getMappingContext().getRequiredPersistentEntity(InstitutionalSubscription.class)
            ),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.institutionMapper = institutionMapper;
        this.institutionalsubscriptionMapper = institutionalsubscriptionMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<InstitutionalSubscription> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<InstitutionalSubscription> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = InstitutionalSubscriptionSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(InstitutionSqlHelper.getColumns(institutionTable, "institution"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(institutionTable)
            .on(Column.create("institution_id", entityTable))
            .equals(Column.create("id", institutionTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, InstitutionalSubscription.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<InstitutionalSubscription> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<InstitutionalSubscription> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<InstitutionalSubscription> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<InstitutionalSubscription> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<InstitutionalSubscription> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private InstitutionalSubscription process(Row row, RowMetadata metadata) {
        InstitutionalSubscription entity = institutionalsubscriptionMapper.apply(row, "e");
        entity.setInstitution(institutionMapper.apply(row, "institution"));
        return entity;
    }

    @Override
    public <S extends InstitutionalSubscription> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<InstitutionalSubscription> findByCriteria(
        InstitutionalSubscriptionCriteria institutionalSubscriptionCriteria,
        Pageable page
    ) {
        return createQuery(page, buildConditions(institutionalSubscriptionCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(InstitutionalSubscriptionCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(InstitutionalSubscriptionCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getStartDate() != null) {
                builder.buildFilterConditionForField(criteria.getStartDate(), entityTable.column("start_date"));
            }
            if (criteria.getExpiryDate() != null) {
                builder.buildFilterConditionForField(criteria.getExpiryDate(), entityTable.column("expiry_date"));
            }
            if (criteria.getMemberLimit() != null) {
                builder.buildFilterConditionForField(criteria.getMemberLimit(), entityTable.column("member_limit"));
            }
            if (criteria.getInstitutionId() != null) {
                builder.buildFilterConditionForField(criteria.getInstitutionId(), institutionTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
