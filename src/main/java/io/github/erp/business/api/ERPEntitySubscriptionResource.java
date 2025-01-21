package io.github.erp.business.api;

import io.github.erp.repository.EntitySubscriptionRepository;
import io.github.erp.service.EntitySubscriptionQueryService;
import io.github.erp.service.EntitySubscriptionService;
import io.github.erp.service.criteria.EntitySubscriptionCriteria;
import io.github.erp.service.dto.EntitySubscriptionDTO;
import io.github.erp.web.rest.errors.BadRequestAlertException;
import io.github.erp.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link io.github.erp.domain.EntitySubscription}.
 */
@RestController
@RequestMapping("/api/erp/entity-subscriptions")
public class ERPEntitySubscriptionResource {

    private static final Logger LOG = LoggerFactory.getLogger(ERPEntitySubscriptionResource.class);

    private static final String ENTITY_NAME = "entitySubscription";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EntitySubscriptionService entitySubscriptionService;

    private final EntitySubscriptionRepository entitySubscriptionRepository;

    private final EntitySubscriptionQueryService entitySubscriptionQueryService;

    public ERPEntitySubscriptionResource(
        EntitySubscriptionService entitySubscriptionService,
        EntitySubscriptionRepository entitySubscriptionRepository,
        EntitySubscriptionQueryService entitySubscriptionQueryService
    ) {
        this.entitySubscriptionService = entitySubscriptionService;
        this.entitySubscriptionRepository = entitySubscriptionRepository;
        this.entitySubscriptionQueryService = entitySubscriptionQueryService;
    }

    /**
     * {@code POST  /entity-subscriptions} : Create a new entitySubscription.
     *
     * @param entitySubscriptionDTO the entitySubscriptionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new entitySubscriptionDTO, or with status {@code 400 (Bad Request)} if the entitySubscription has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<EntitySubscriptionDTO> createEntitySubscription(@Valid @RequestBody EntitySubscriptionDTO entitySubscriptionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save EntitySubscription : {}", entitySubscriptionDTO);
        if (entitySubscriptionDTO.getId() != null) {
            throw new BadRequestAlertException("A new entitySubscription cannot already have an ID", ENTITY_NAME, "idexists");
        }
        entitySubscriptionDTO = entitySubscriptionService.save(entitySubscriptionDTO);
        return ResponseEntity.created(new URI("/api/entity-subscriptions/" + entitySubscriptionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, entitySubscriptionDTO.getId().toString()))
            .body(entitySubscriptionDTO);
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
    public ResponseEntity<EntitySubscriptionDTO> updateEntitySubscription(
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

        if (!entitySubscriptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        entitySubscriptionDTO = entitySubscriptionService.update(entitySubscriptionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, entitySubscriptionDTO.getId().toString()))
            .body(entitySubscriptionDTO);
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
    public ResponseEntity<EntitySubscriptionDTO> partialUpdateEntitySubscription(
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

        if (!entitySubscriptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EntitySubscriptionDTO> result = entitySubscriptionService.partialUpdate(entitySubscriptionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, entitySubscriptionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /entity-subscriptions} : get all the entitySubscriptions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of entitySubscriptions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<EntitySubscriptionDTO>> getAllEntitySubscriptions(
        EntitySubscriptionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get EntitySubscriptions by criteria: {}", criteria);

        Page<EntitySubscriptionDTO> page = entitySubscriptionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /entity-subscriptions/count} : count all the entitySubscriptions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countEntitySubscriptions(EntitySubscriptionCriteria criteria) {
        LOG.debug("REST request to count EntitySubscriptions by criteria: {}", criteria);
        return ResponseEntity.ok().body(entitySubscriptionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /entity-subscriptions/:id} : get the "id" entitySubscription.
     *
     * @param id the id of the entitySubscriptionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the entitySubscriptionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntitySubscriptionDTO> getEntitySubscription(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EntitySubscription : {}", id);
        Optional<EntitySubscriptionDTO> entitySubscriptionDTO = entitySubscriptionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(entitySubscriptionDTO);
    }

    /**
     * {@code DELETE  /entity-subscriptions/:id} : delete the "id" entitySubscription.
     *
     * @param id the id of the entitySubscriptionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntitySubscription(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EntitySubscription : {}", id);
        entitySubscriptionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /entity-subscriptions/_search?query=:query} : search for the entitySubscription corresponding
     * to the query.
     *
     * @param query the query of the entitySubscription search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<EntitySubscriptionDTO>> searchEntitySubscriptions(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of EntitySubscriptions for query {}", query);
        try {
            Page<EntitySubscriptionDTO> page = entitySubscriptionService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}