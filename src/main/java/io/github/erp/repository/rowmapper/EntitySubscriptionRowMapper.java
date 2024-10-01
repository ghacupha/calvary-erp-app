package io.github.erp.repository.rowmapper;

import io.github.erp.domain.EntitySubscription;
import io.r2dbc.spi.Row;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link EntitySubscription}, with proper type conversions.
 */
@Service
public class EntitySubscriptionRowMapper implements BiFunction<Row, String, EntitySubscription> {

    private final ColumnConverter converter;

    public EntitySubscriptionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link EntitySubscription} stored in the database.
     */
    @Override
    public EntitySubscription apply(Row row, String prefix) {
        EntitySubscription entity = new EntitySubscription();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setSubscriptionToken(converter.fromRow(row, prefix + "_subscription_token", UUID.class));
        entity.setStartDate(converter.fromRow(row, prefix + "_start_date", ZonedDateTime.class));
        entity.setEndDate(converter.fromRow(row, prefix + "_end_date", ZonedDateTime.class));
        return entity;
    }
}
