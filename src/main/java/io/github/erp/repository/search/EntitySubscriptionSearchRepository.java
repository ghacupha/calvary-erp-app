package io.github.erp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import io.github.erp.domain.EntitySubscription;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link EntitySubscription} entity.
 */
public interface EntitySubscriptionSearchRepository
    extends ReactiveElasticsearchRepository<EntitySubscription, Long>, EntitySubscriptionSearchRepositoryInternal {}

interface EntitySubscriptionSearchRepositoryInternal {
    Flux<EntitySubscription> search(String query, Pageable pageable);

    Flux<EntitySubscription> search(Query query);
}

class EntitySubscriptionSearchRepositoryInternalImpl implements EntitySubscriptionSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    EntitySubscriptionSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<EntitySubscription> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<EntitySubscription> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, EntitySubscription.class).map(SearchHit::getContent);
    }
}
