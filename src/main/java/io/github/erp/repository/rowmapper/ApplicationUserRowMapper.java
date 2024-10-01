package io.github.erp.repository.rowmapper;

import io.github.erp.domain.ApplicationUser;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ApplicationUser}, with proper type conversions.
 */
@Service
public class ApplicationUserRowMapper implements BiFunction<Row, String, ApplicationUser> {

    private final ColumnConverter converter;

    public ApplicationUserRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ApplicationUser} stored in the database.
     */
    @Override
    public ApplicationUser apply(Row row, String prefix) {
        ApplicationUser entity = new ApplicationUser();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUsername(converter.fromRow(row, prefix + "_username", String.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setActivated(converter.fromRow(row, prefix + "_activated", Boolean.class));
        entity.setLangKey(converter.fromRow(row, prefix + "_lang_key", String.class));
        entity.setImageUrl(converter.fromRow(row, prefix + "_image_url", String.class));
        entity.setActivationKey(converter.fromRow(row, prefix + "_activation_key", String.class));
        entity.setResetKey(converter.fromRow(row, prefix + "_reset_key", String.class));
        entity.setResetDate(converter.fromRow(row, prefix + "_reset_date", Instant.class));
        entity.setSystemUserId(converter.fromRow(row, prefix + "_system_user_id", Long.class));
        return entity;
    }
}
