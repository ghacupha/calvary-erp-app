package io.github.erp.repository.rowmapper;

import io.github.erp.domain.InstitutionalSubscription;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link InstitutionalSubscription}, with proper type conversions.
 */
@Service
public class InstitutionalSubscriptionRowMapper implements BiFunction<Row, String, InstitutionalSubscription> {

    private final ColumnConverter converter;

    public InstitutionalSubscriptionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link InstitutionalSubscription} stored in the database.
     */
    @Override
    public InstitutionalSubscription apply(Row row, String prefix) {
        InstitutionalSubscription entity = new InstitutionalSubscription();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setStartDate(converter.fromRow(row, prefix + "_start_date", LocalDate.class));
        entity.setExpiryDate(converter.fromRow(row, prefix + "_expiry_date", LocalDate.class));
        entity.setMemberLimit(converter.fromRow(row, prefix + "_member_limit", Integer.class));
        entity.setInstitutionId(converter.fromRow(row, prefix + "_institution_id", Long.class));
        return entity;
    }
}
