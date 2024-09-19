package io.github.erp.web.rest;

import io.github.erp.domain.criteria.InstitutionalSubscriptionCriteria;
import io.github.erp.repository.InstitutionalSubscriptionRepository;
import io.github.erp.service.InstitutionalSubscriptionService;
import io.github.erp.service.dto.InstitutionalSubscriptionDTO;
import io.github.erp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link io.github.erp.domain.InstitutionalSubscription}.
 */
@RestController
@RequestMapping("/api/institutional-subscriptions")
public class InstitutionalSubscriptionResource {

    private static final Logger LOG = LoggerFactory.getLogger(InstitutionalSubscriptionResource.class);

    private static final String ENTITY_NAME = "institutionalSubscription";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InstitutionalSubscriptionService institutionalSubscriptionService;

    private final InstitutionalSubscriptionRepository institutionalSubscriptionRepository;

    public InstitutionalSubscriptionResource(
        InstitutionalSubscriptionService institutionalSubscriptionService,
        InstitutionalSubscriptionRepository institutionalSubscriptionRepository
    ) {
        this.institutionalSubscriptionService = institutionalSubscriptionService;
        this.institutionalSubscriptionRepository = institutionalSubscriptionRepository;
    }

    /**
     * {@code POST  /institutional-subscriptions} : Create a new institutionalSubscription.
     *
     * @param institutionalSubscriptionDTO the institutionalSubscriptionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new institutionalSubscriptionDTO, or with status {@code 400 (Bad Request)} if the institutionalSubscription has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<InstitutionalSubscriptionDTO>> createInstitutionalSubscription(
        @Valid @RequestBody InstitutionalSubscriptionDTO institutionalSubscriptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save InstitutionalSubscription : {}", institutionalSubscriptionDTO);
        if (institutionalSubscriptionDTO.getId() != null) {
            throw new BadRequestAlertException("A new institutionalSubscription cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return institutionalSubscriptionService
            .save(institutionalSubscriptionDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/institutional-subscriptions/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /institutional-subscriptions/:id} : Updates an existing institutionalSubscription.
     *
     * @param id the id of the institutionalSubscriptionDTO to save.
     * @param institutionalSubscriptionDTO the institutionalSubscriptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated institutionalSubscriptionDTO,
     * or with status {@code 400 (Bad Request)} if the institutionalSubscriptionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the institutionalSubscriptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<InstitutionalSubscriptionDTO>> updateInstitutionalSubscription(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InstitutionalSubscriptionDTO institutionalSubscriptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update InstitutionalSubscription : {}, {}", id, institutionalSubscriptionDTO);
        if (institutionalSubscriptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, institutionalSubscriptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return institutionalSubscriptionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return institutionalSubscriptionService
                    .update(institutionalSubscriptionDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /institutional-subscriptions/:id} : Partial updates given fields of an existing institutionalSubscription, field will ignore if it is null
     *
     * @param id the id of the institutionalSubscriptionDTO to save.
     * @param institutionalSubscriptionDTO the institutionalSubscriptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated institutionalSubscriptionDTO,
     * or with status {@code 400 (Bad Request)} if the institutionalSubscriptionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the institutionalSubscriptionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the institutionalSubscriptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<InstitutionalSubscriptionDTO>> partialUpdateInstitutionalSubscription(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InstitutionalSubscriptionDTO institutionalSubscriptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update InstitutionalSubscription partially : {}, {}", id, institutionalSubscriptionDTO);
        if (institutionalSubscriptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, institutionalSubscriptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return institutionalSubscriptionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<InstitutionalSubscriptionDTO> result = institutionalSubscriptionService.partialUpdate(institutionalSubscriptionDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /institutional-subscriptions} : get all the institutionalSubscriptions.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of institutionalSubscriptions in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<InstitutionalSubscriptionDTO>>> getAllInstitutionalSubscriptions(
        InstitutionalSubscriptionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get InstitutionalSubscriptions by criteria: {}", criteria);
        return institutionalSubscriptionService
            .countByCriteria(criteria)
            .zipWith(institutionalSubscriptionService.findByCriteria(criteria, pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /institutional-subscriptions/count} : count all the institutionalSubscriptions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countInstitutionalSubscriptions(InstitutionalSubscriptionCriteria criteria) {
        LOG.debug("REST request to count InstitutionalSubscriptions by criteria: {}", criteria);
        return institutionalSubscriptionService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /institutional-subscriptions/:id} : get the "id" institutionalSubscription.
     *
     * @param id the id of the institutionalSubscriptionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the institutionalSubscriptionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<InstitutionalSubscriptionDTO>> getInstitutionalSubscription(@PathVariable("id") Long id) {
        LOG.debug("REST request to get InstitutionalSubscription : {}", id);
        Mono<InstitutionalSubscriptionDTO> institutionalSubscriptionDTO = institutionalSubscriptionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(institutionalSubscriptionDTO);
    }

    /**
     * {@code DELETE  /institutional-subscriptions/:id} : delete the "id" institutionalSubscription.
     *
     * @param id the id of the institutionalSubscriptionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteInstitutionalSubscription(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete InstitutionalSubscription : {}", id);
        return institutionalSubscriptionService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    /**
     * {@code SEARCH  /institutional-subscriptions/_search?query=:query} : search for the institutionalSubscription corresponding
     * to the query.
     *
     * @param query the query of the institutionalSubscription search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<InstitutionalSubscriptionDTO>>> searchInstitutionalSubscriptions(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to search for a page of InstitutionalSubscriptions for query {}", query);
        return institutionalSubscriptionService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page ->
                PaginationUtil.generatePaginationHttpHeaders(
                    ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                    page
                )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(institutionalSubscriptionService.search(query, pageable)));
    }
}
