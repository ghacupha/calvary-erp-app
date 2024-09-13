package io.github.erp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class InstitutionalSubscriptionSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("start_date", table, columnPrefix + "_start_date"));
        columns.add(Column.aliased("expiry_date", table, columnPrefix + "_expiry_date"));
        columns.add(Column.aliased("member_limit", table, columnPrefix + "_member_limit"));

        columns.add(Column.aliased("institution_id", table, columnPrefix + "_institution_id"));
        return columns;
    }
}
