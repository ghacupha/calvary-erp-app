package io.github.erp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import io.github.erp.domain.ApplicationUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link ApplicationUser} entity.
 */
public interface ApplicationUserSearchRepository
    extends ReactiveElasticsearchRepository<ApplicationUser, Long>, ApplicationUserSearchRepositoryInternal {}

interface ApplicationUserSearchRepositoryInternal {
    Flux<ApplicationUser> search(String query, Pageable pageable);

    Flux<ApplicationUser> search(Query query);
}

class ApplicationUserSearchRepositoryInternalImpl implements ApplicationUserSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    ApplicationUserSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<ApplicationUser> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<ApplicationUser> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, ApplicationUser.class).map(SearchHit::getContent);
    }
}
