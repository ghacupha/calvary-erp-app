package io.github.erp.web.rest;

import io.github.erp.repository.ApplicationUserRepository;
import io.github.erp.service.ApplicationUserQueryService;
import io.github.erp.service.ApplicationUserService;
import io.github.erp.service.criteria.ApplicationUserCriteria;
import io.github.erp.service.dto.ApplicationUserDTO;
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
 * REST controller for managing {@link io.github.erp.domain.ApplicationUser}.
 */
@RestController
@RequestMapping("/api/application-users")
public class ApplicationUserResource {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationUserResource.class);

    private static final String ENTITY_NAME = "applicationUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ApplicationUserService applicationUserService;

    private final ApplicationUserRepository applicationUserRepository;

    private final ApplicationUserQueryService applicationUserQueryService;

    public ApplicationUserResource(
        ApplicationUserService applicationUserService,
        ApplicationUserRepository applicationUserRepository,
        ApplicationUserQueryService applicationUserQueryService
    ) {
        this.applicationUserService = applicationUserService;
        this.applicationUserRepository = applicationUserRepository;
        this.applicationUserQueryService = applicationUserQueryService;
    }

    /**
     * {@code POST  /application-users} : Create a new applicationUser.
     *
     * @param applicationUserDTO the applicationUserDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new applicationUserDTO, or with status {@code 400 (Bad Request)} if the applicationUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ApplicationUserDTO> createApplicationUser(@Valid @RequestBody ApplicationUserDTO applicationUserDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ApplicationUser : {}", applicationUserDTO);
        if (applicationUserDTO.getId() != null) {
            throw new BadRequestAlertException("A new applicationUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        applicationUserDTO = applicationUserService.save(applicationUserDTO);
        return ResponseEntity.created(new URI("/api/application-users/" + applicationUserDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, applicationUserDTO.getId().toString()))
            .body(applicationUserDTO);
    }

    /**
     * {@code PUT  /application-users/:id} : Updates an existing applicationUser.
     *
     * @param id the id of the applicationUserDTO to save.
     * @param applicationUserDTO the applicationUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated applicationUserDTO,
     * or with status {@code 400 (Bad Request)} if the applicationUserDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the applicationUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApplicationUserDTO> updateApplicationUser(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ApplicationUserDTO applicationUserDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ApplicationUser : {}, {}", id, applicationUserDTO);
        if (applicationUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, applicationUserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!applicationUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        applicationUserDTO = applicationUserService.update(applicationUserDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, applicationUserDTO.getId().toString()))
            .body(applicationUserDTO);
    }

    /**
     * {@code PATCH  /application-users/:id} : Partial updates given fields of an existing applicationUser, field will ignore if it is null
     *
     * @param id the id of the applicationUserDTO to save.
     * @param applicationUserDTO the applicationUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated applicationUserDTO,
     * or with status {@code 400 (Bad Request)} if the applicationUserDTO is not valid,
     * or with status {@code 404 (Not Found)} if the applicationUserDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the applicationUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ApplicationUserDTO> partialUpdateApplicationUser(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ApplicationUserDTO applicationUserDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ApplicationUser partially : {}, {}", id, applicationUserDTO);
        if (applicationUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, applicationUserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!applicationUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ApplicationUserDTO> result = applicationUserService.partialUpdate(applicationUserDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, applicationUserDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /application-users} : get all the applicationUsers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of applicationUsers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ApplicationUserDTO>> getAllApplicationUsers(
        ApplicationUserCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ApplicationUsers by criteria: {}", criteria);

        Page<ApplicationUserDTO> page = applicationUserQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /application-users/count} : count all the applicationUsers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countApplicationUsers(ApplicationUserCriteria criteria) {
        LOG.debug("REST request to count ApplicationUsers by criteria: {}", criteria);
        return ResponseEntity.ok().body(applicationUserQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /application-users/:id} : get the "id" applicationUser.
     *
     * @param id the id of the applicationUserDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the applicationUserDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationUserDTO> getApplicationUser(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ApplicationUser : {}", id);
        Optional<ApplicationUserDTO> applicationUserDTO = applicationUserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(applicationUserDTO);
    }

    /**
     * {@code DELETE  /application-users/:id} : delete the "id" applicationUser.
     *
     * @param id the id of the applicationUserDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplicationUser(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ApplicationUser : {}", id);
        applicationUserService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /application-users/_search?query=:query} : search for the applicationUser corresponding
     * to the query.
     *
     * @param query the query of the applicationUser search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<ApplicationUserDTO>> searchApplicationUsers(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of ApplicationUsers for query {}", query);
        try {
            Page<ApplicationUserDTO> page = applicationUserService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
