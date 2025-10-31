package io.github.erp.business;

import java.util.List;

public interface Mapping<V1, V2> {
    default List<V2> toValue2(List<V1> v1) {
        return v1.stream().map(this::toValue2).toList();
    }

    default List<V1> toValue1(List<V2> v2) {
        return v2.stream().map(this::toValue1).toList();
    }

    V2 toValue2(V1 value2);

    V1 toValue1(V2 value1);
}
