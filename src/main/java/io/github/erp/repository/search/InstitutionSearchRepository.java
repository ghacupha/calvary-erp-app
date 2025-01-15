package io.github.erp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import io.github.erp.domain.Institution;
import io.github.erp.repository.InstitutionRepository;
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
 * Spring Data Elasticsearch repository for the {@link Institution} entity.
 */
public interface InstitutionSearchRepository extends ElasticsearchRepository<Institution, Long>, InstitutionSearchRepositoryInternal {}

interface InstitutionSearchRepositoryInternal {
    Page<Institution> search(String query, Pageable pageable);

    Page<Institution> search(Query query);

    @Async
    void index(Institution entity);

    @Async
    void deleteFromIndexById(Long id);
}

class InstitutionSearchRepositoryInternalImpl implements InstitutionSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final InstitutionRepository repository;

    InstitutionSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, InstitutionRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Institution> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Institution> search(Query query) {
        SearchHits<Institution> searchHits = elasticsearchTemplate.search(query, Institution.class);
        List<Institution> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Institution entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Institution.class);
    }
}
