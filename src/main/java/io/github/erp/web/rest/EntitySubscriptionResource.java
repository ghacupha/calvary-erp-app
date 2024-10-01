package io.github.erp.web.rest;

import io.github.erp.domain.criteria.EntitySubscriptionCriteria;
import io.github.erp.repository.EntitySubscriptionRepository;
import io.github.erp.service.EntitySubscriptionService;
import io.github.erp.service.dto.EntitySubscriptionDTO;
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
 * REST controller for managing {@link io.github.erp.domain.EntitySubscription}.
 */
@RestController
@RequestMapping("/api/entity-subscriptions")
public class EntitySubscriptionResource {

    private static final Logger LOG = LoggerFactory.getLogger(EntitySubscriptionResource.class);

    private static final String ENTITY_NAME = "entitySubscription";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EntitySubscriptionService entitySubscriptionService;

    private final EntitySubscriptionRepository entitySubscriptionRepository;

    public EntitySubscriptionResource(
        EntitySubscriptionService entitySubscriptionService,
        EntitySubscriptionRepository entitySubscriptionRepository
    ) {
        this.entitySubscriptionService = entitySubscriptionService;
        this.entitySubscriptionRepository = entitySubscriptionRepository;
    }

    /**
     * {@code POST  /entity-subscriptions} : Create a new entitySubscription.
     *
     * @param entitySubscriptionDTO the entitySubscriptionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new entitySubscriptionDTO, or with status {@code 400 (Bad Request)} if the entitySubscription has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<EntitySubscriptionDTO>> createEntitySubscription(
        @Valid @RequestBody EntitySubscriptionDTO entitySubscriptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save EntitySubscription : {}", entitySubscriptionDTO);
        if (entitySubscriptionDTO.getId() != null) {
            throw new BadRequestAlertException("A new entitySubscription cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return entitySubscriptionService
            .save(entitySubscriptionDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/entity-subscriptions/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /entity-subscriptions/:id} : Updates an existing entitySubscription.
     *
     * @param id the id of the entitySubscriptionDTO to save.
     * @param entitySubscriptionDTO the entitySubscriptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated entitySubscriptionDTO,
     * or with status {@code 400 (Bad Request)} if the entitySubscriptionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the entitySubscriptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<EntitySubscriptionDTO>> updateEntitySubscription(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EntitySubscriptionDTO entitySubscriptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update EntitySubscription : {}, {}", id, entitySubscriptionDTO);
        if (entitySubscriptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, entitySubscriptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return entitySubscriptionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return entitySubscriptionService
                    .update(entitySubscriptionDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /entity-subscriptions/:id} : Partial updates given fields of an existing entitySubscription, field will ignore if it is null
     *
     * @param id the id of the entitySubscriptionDTO to save.
     * @param entitySubscriptionDTO the entitySubscriptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated entitySubscriptionDTO,
     * or with status {@code 400 (Bad Request)} if the entitySubscriptionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the entitySubscriptionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the entitySubscriptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<EntitySubscriptionDTO>> partialUpdateEntitySubscription(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EntitySubscriptionDTO entitySubscriptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update EntitySubscription partially : {}, {}", id, entitySubscriptionDTO);
        if (entitySubscriptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, entitySubscriptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return entitySubscriptionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<EntitySubscriptionDTO> result = entitySubscriptionService.partialUpdate(entitySubscriptionDTO);

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
     * {@code GET  /entity-subscriptions} : get all the entitySubscriptions.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of entitySubscriptions in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<EntitySubscriptionDTO>>> getAllEntitySubscriptions(
        EntitySubscriptionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get EntitySubscriptions by criteria: {}", criteria);
        return entitySubscriptionService
            .countByCriteria(criteria)
            .zipWith(entitySubscriptionService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /entity-subscriptions/count} : count all the entitySubscriptions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countEntitySubscriptions(EntitySubscriptionCriteria criteria) {
        LOG.debug("REST request to count EntitySubscriptions by criteria: {}", criteria);
        return entitySubscriptionService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /entity-subscriptions/:id} : get the "id" entitySubscription.
     *
     * @param id the id of the entitySubscriptionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the entitySubscriptionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<EntitySubscriptionDTO>> getEntitySubscription(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EntitySubscription : {}", id);
        Mono<EntitySubscriptionDTO> entitySubscriptionDTO = entitySubscriptionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(entitySubscriptionDTO);
    }

    /**
     * {@code DELETE  /entity-subscriptions/:id} : delete the "id" entitySubscription.
     *
     * @param id the id of the entitySubscriptionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteEntitySubscription(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EntitySubscription : {}", id);
        return entitySubscriptionService
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
     * {@code SEARCH  /entity-subscriptions/_search?query=:query} : search for the entitySubscription corresponding
     * to the query.
     *
     * @param query the query of the entitySubscription search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<EntitySubscriptionDTO>>> searchEntitySubscriptions(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to search for a page of EntitySubscriptions for query {}", query);
        return entitySubscriptionService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page ->
                PaginationUtil.generatePaginationHttpHeaders(
                    ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                    page
                )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(entitySubscriptionService.search(query, pageable)));
    }
}
