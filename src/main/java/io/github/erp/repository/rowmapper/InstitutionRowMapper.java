package io.github.erp.repository.rowmapper;

import io.github.erp.domain.Institution;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Institution}, with proper type conversions.
 */
@Service
public class InstitutionRowMapper implements BiFunction<Row, String, Institution> {

    private final ColumnConverter converter;

    public InstitutionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Institution} stored in the database.
     */
    @Override
    public Institution apply(Row row, String prefix) {
        Institution entity = new Institution();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setInstitutionName(converter.fromRow(row, prefix + "_institution_name", String.class));
        entity.setParentInstitutionId(converter.fromRow(row, prefix + "_parent_institution_id", Long.class));
        return entity;
    }
}
