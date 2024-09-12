package io.github.erp.repository;

import io.github.erp.domain.Institution;
import io.github.erp.domain.criteria.InstitutionCriteria;
import io.github.erp.repository.rowmapper.ColumnConverter;
import io.github.erp.repository.rowmapper.InstitutionRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Institution entity.
 */
@SuppressWarnings("unused")
class InstitutionRepositoryInternalImpl extends SimpleR2dbcRepository<Institution, Long> implements InstitutionRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final InstitutionRowMapper institutionMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("institution", EntityManager.ENTITY_ALIAS);
    private static final Table parentInstitutionTable = Table.aliased("institution", "parentInstitution");

    public InstitutionRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        InstitutionRowMapper institutionMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Institution.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.institutionMapper = institutionMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Institution> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Institution> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = InstitutionSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(InstitutionSqlHelper.getColumns(parentInstitutionTable, "parentInstitution"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(parentInstitutionTable)
            .on(Column.create("parent_institution_id", entityTable))
            .equals(Column.create("id", parentInstitutionTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Institution.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Institution> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Institution> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Institution> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Institution> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Institution> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Institution process(Row row, RowMetadata metadata) {
        Institution entity = institutionMapper.apply(row, "e");
        entity.setParentInstitution(institutionMapper.apply(row, "parentInstitution"));
        return entity;
    }

    @Override
    public <S extends Institution> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Institution> findByCriteria(InstitutionCriteria institutionCriteria, Pageable page) {
        return createQuery(page, buildConditions(institutionCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(InstitutionCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(InstitutionCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getInstitutionName() != null) {
                builder.buildFilterConditionForField(criteria.getInstitutionName(), entityTable.column("institution_name"));
            }
            if (criteria.getParentInstitutionId() != null) {
                builder.buildFilterConditionForField(criteria.getParentInstitutionId(), parentInstitutionTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
