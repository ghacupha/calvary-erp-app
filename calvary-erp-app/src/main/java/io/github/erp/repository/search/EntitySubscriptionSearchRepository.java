package io.github.erp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import io.github.erp.domain.EntitySubscription;
import io.github.erp.repository.EntitySubscriptionRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link EntitySubscription} entity.
 */
public interface EntitySubscriptionSearchRepository
    extends ElasticsearchRepository<EntitySubscription, Long>, EntitySubscriptionSearchRepositoryInternal {}

interface EntitySubscriptionSearchRepositoryInternal {
    Page<EntitySubscription> search(String query, Pageable pageable);

    Page<EntitySubscription> search(Query query);

    @Async
    void index(EntitySubscription entity);

    @Async
    void deleteFromIndexById(Long id);
}

class EntitySubscriptionSearchRepositoryInternalImpl implements EntitySubscriptionSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EntitySubscriptionRepository repository;

    EntitySubscriptionSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, EntitySubscriptionRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<EntitySubscription> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<EntitySubscription> search(Query query) {
        SearchHits<EntitySubscription> searchHits = elasticsearchTemplate.search(query, EntitySubscription.class);
        List<EntitySubscription> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(EntitySubscription entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), EntitySubscription.class);
    }
}
