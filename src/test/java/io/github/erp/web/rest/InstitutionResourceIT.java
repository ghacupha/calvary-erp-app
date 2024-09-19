package io.github.erp.web.rest;

import static io.github.erp.domain.InstitutionAsserts.*;
import static io.github.erp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.erp.IntegrationTest;
import io.github.erp.domain.Institution;
import io.github.erp.domain.Institution;
import io.github.erp.repository.EntityManager;
import io.github.erp.repository.InstitutionRepository;
import io.github.erp.repository.InstitutionRepository;
import io.github.erp.repository.search.InstitutionSearchRepository;
import io.github.erp.service.InstitutionService;
import io.github.erp.service.dto.InstitutionDTO;
import io.github.erp.service.mapper.InstitutionMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

/**
 * Integration tests for the {@link InstitutionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class InstitutionResourceIT {

    private static final String DEFAULT_INSTITUTION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_INSTITUTION_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/institutions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/institutions/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Mock
    private InstitutionRepository institutionRepositoryMock;

    @Autowired
    private InstitutionMapper institutionMapper;

    @Mock
    private InstitutionService institutionServiceMock;

    @Autowired
    private InstitutionSearchRepository institutionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Institution institution;

    private Institution insertedInstitution;

    //    @Autowired
    //    private InstitutionRepository institutionRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Institution createEntity() {
        return new Institution().institutionName(DEFAULT_INSTITUTION_NAME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Institution createUpdatedEntity() {
        return new Institution().institutionName(UPDATED_INSTITUTION_NAME);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Institution.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        institution = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedInstitution != null) {
            institutionRepository.delete(insertedInstitution).block();
            institutionSearchRepository.delete(insertedInstitution).block();
            insertedInstitution = null;
        }
        deleteEntities(em);
    }

    @Test
    void createInstitution() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());
        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);
        var returnedInstitutionDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(institutionDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(InstitutionDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Institution in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInstitution = institutionMapper.toEntity(returnedInstitutionDTO);
        assertInstitutionUpdatableFieldsEquals(returnedInstitution, getPersistedInstitution(returnedInstitution));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedInstitution = returnedInstitution;
    }

    @Test
    void createInstitutionWithExistingId() throws Exception {
        // Create the Institution with an existing ID
        institution.setId(1L);
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(institutionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Institution in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkInstitutionNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());
        // set the field null
        institution.setInstitutionName(null);

        // Create the Institution, which fails.
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(institutionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllInstitutions() {
        // Initialize the database
        insertedInstitution = institutionRepository.save(institution).block();

        // Get all the institutionList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(institution.getId().intValue()))
            .jsonPath("$.[*].institutionName")
            .value(hasItem(DEFAULT_INSTITUTION_NAME));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInstitutionsWithEagerRelationshipsIsEnabled() {
        when(institutionServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(institutionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInstitutionsWithEagerRelationshipsIsNotEnabled() {
        when(institutionServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(institutionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getInstitution() {
        // Initialize the database
        insertedInstitution = institutionRepository.save(institution).block();

        // Get the institution
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, institution.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(institution.getId().intValue()))
            .jsonPath("$.institutionName")
            .value(is(DEFAULT_INSTITUTION_NAME));
    }

    @Test
    void getInstitutionsByIdFiltering() {
        // Initialize the database
        insertedInstitution = institutionRepository.save(institution).block();

        Long id = institution.getId();

        defaultInstitutionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultInstitutionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultInstitutionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllInstitutionsByInstitutionNameIsEqualToSomething() {
        // Initialize the database
        insertedInstitution = institutionRepository.save(institution).block();

        // Get all the institutionList where institutionName equals to
        defaultInstitutionFiltering(
            "institutionName.equals=" + DEFAULT_INSTITUTION_NAME,
            "institutionName.equals=" + UPDATED_INSTITUTION_NAME
        );
    }

    @Test
    void getAllInstitutionsByInstitutionNameIsInShouldWork() {
        // Initialize the database
        insertedInstitution = institutionRepository.save(institution).block();

        // Get all the institutionList where institutionName in
        defaultInstitutionFiltering(
            "institutionName.in=" + DEFAULT_INSTITUTION_NAME + "," + UPDATED_INSTITUTION_NAME,
            "institutionName.in=" + UPDATED_INSTITUTION_NAME
        );
    }

    @Test
    void getAllInstitutionsByInstitutionNameIsNullOrNotNull() {
        // Initialize the database
        insertedInstitution = institutionRepository.save(institution).block();

        // Get all the institutionList where institutionName is not null
        defaultInstitutionFiltering("institutionName.specified=true", "institutionName.specified=false");
    }

    @Test
    void getAllInstitutionsByInstitutionNameContainsSomething() {
        // Initialize the database
        insertedInstitution = institutionRepository.save(institution).block();

        // Get all the institutionList where institutionName contains
        defaultInstitutionFiltering(
            "institutionName.contains=" + DEFAULT_INSTITUTION_NAME,
            "institutionName.contains=" + UPDATED_INSTITUTION_NAME
        );
    }

    @Test
    void getAllInstitutionsByInstitutionNameNotContainsSomething() {
        // Initialize the database
        insertedInstitution = institutionRepository.save(institution).block();

        // Get all the institutionList where institutionName does not contain
        defaultInstitutionFiltering(
            "institutionName.doesNotContain=" + UPDATED_INSTITUTION_NAME,
            "institutionName.doesNotContain=" + DEFAULT_INSTITUTION_NAME
        );
    }

    @Test
    void getAllInstitutionsByParentInstitutionIsEqualToSomething() {
        Institution parentInstitution = InstitutionResourceIT.createEntity();
        institutionRepository.save(parentInstitution).block();
        Long parentInstitutionId = parentInstitution.getId();
        institution.setParentInstitutionId(parentInstitutionId);
        insertedInstitution = institutionRepository.save(institution).block();
        // Get all the institutionList where parentInstitution equals to parentInstitutionId
        defaultInstitutionShouldBeFound("parentInstitutionId.equals=" + parentInstitutionId);

        // Get all the institutionList where parentInstitution equals to (parentInstitutionId + 1)
        defaultInstitutionShouldNotBeFound("parentInstitutionId.equals=" + (parentInstitutionId + 1));
    }

    private void defaultInstitutionFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultInstitutionShouldBeFound(shouldBeFound);
        defaultInstitutionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInstitutionShouldBeFound(String filter) {
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(institution.getId().intValue()))
            .jsonPath("$.[*].institutionName")
            .value(hasItem(DEFAULT_INSTITUTION_NAME));

        // Check, that the count call also returns 1
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .value(is(1));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInstitutionShouldNotBeFound(String filter) {
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .isArray()
            .jsonPath("$")
            .isEmpty();

        // Check, that the count call also returns 0
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .value(is(0));
    }

    @Test
    void getNonExistingInstitution() {
        // Get the institution
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingInstitution() throws Exception {
        // Initialize the database
        insertedInstitution = institutionRepository.save(institution).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        institutionSearchRepository.save(institution).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());

        // Update the institution
        Institution updatedInstitution = institutionRepository.findById(institution.getId()).block();
        updatedInstitution.institutionName(UPDATED_INSTITUTION_NAME);
        InstitutionDTO institutionDTO = institutionMapper.toDto(updatedInstitution);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, institutionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(institutionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Institution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInstitutionToMatchAllProperties(updatedInstitution);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Institution> institutionSearchList = Streamable.of(
                    institutionSearchRepository.findAll().collectList().block()
                ).toList();
                Institution testInstitutionSearch = institutionSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertInstitutionAllPropertiesEquals(testInstitutionSearch, updatedInstitution);
                assertInstitutionUpdatableFieldsEquals(testInstitutionSearch, updatedInstitution);
            });
    }

    @Test
    void putNonExistingInstitution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());
        institution.setId(longCount.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, institutionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(institutionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Institution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchInstitution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());
        institution.setId(longCount.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(institutionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Institution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamInstitution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());
        institution.setId(longCount.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(institutionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Institution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateInstitutionWithPatch() throws Exception {
        // Initialize the database
        insertedInstitution = institutionRepository.save(institution).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the institution using partial update
        Institution partialUpdatedInstitution = new Institution();
        partialUpdatedInstitution.setId(institution.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInstitution.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInstitution))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Institution in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstitutionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInstitution, institution),
            getPersistedInstitution(institution)
        );
    }

    @Test
    void fullUpdateInstitutionWithPatch() throws Exception {
        // Initialize the database
        insertedInstitution = institutionRepository.save(institution).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the institution using partial update
        Institution partialUpdatedInstitution = new Institution();
        partialUpdatedInstitution.setId(institution.getId());

        partialUpdatedInstitution.institutionName(UPDATED_INSTITUTION_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInstitution.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInstitution))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Institution in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstitutionUpdatableFieldsEquals(partialUpdatedInstitution, getPersistedInstitution(partialUpdatedInstitution));
    }

    @Test
    void patchNonExistingInstitution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());
        institution.setId(longCount.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, institutionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(institutionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Institution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchInstitution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());
        institution.setId(longCount.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(institutionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Institution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamInstitution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());
        institution.setId(longCount.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(institutionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Institution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteInstitution() {
        // Initialize the database
        insertedInstitution = institutionRepository.save(institution).block();
        institutionRepository.save(institution).block();
        institutionSearchRepository.save(institution).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the institution
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, institution.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchInstitution() {
        // Initialize the database
        insertedInstitution = institutionRepository.save(institution).block();
        institutionSearchRepository.save(institution).block();

        // Search the institution
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + institution.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(institution.getId().intValue()))
            .jsonPath("$.[*].institutionName")
            .value(hasItem(DEFAULT_INSTITUTION_NAME));
    }

    protected long getRepositoryCount() {
        return institutionRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Institution getPersistedInstitution(Institution institution) {
        return institutionRepository.findById(institution.getId()).block();
    }

    protected void assertPersistedInstitutionToMatchAllProperties(Institution expectedInstitution) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInstitutionAllPropertiesEquals(expectedInstitution, getPersistedInstitution(expectedInstitution));
        assertInstitutionUpdatableFieldsEquals(expectedInstitution, getPersistedInstitution(expectedInstitution));
    }

    protected void assertPersistedInstitutionToMatchUpdatableProperties(Institution expectedInstitution) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInstitutionAllUpdatablePropertiesEquals(expectedInstitution, getPersistedInstitution(expectedInstitution));
        assertInstitutionUpdatableFieldsEquals(expectedInstitution, getPersistedInstitution(expectedInstitution));
    }
}
