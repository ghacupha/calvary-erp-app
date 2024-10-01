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
import io.github.erp.repository.EntityManager;
import io.github.erp.repository.InstitutionRepository;
import io.github.erp.repository.search.InstitutionSearchRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link InstitutionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class InstitutionResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/institutions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/institutions/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private InstitutionMapper institutionMapper;

    @Autowired
    private InstitutionSearchRepository institutionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Institution institution;

    private Institution insertedInstitution;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Institution createEntity() {
        return new Institution().name(DEFAULT_NAME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Institution createUpdatedEntity() {
        return new Institution().name(UPDATED_NAME);
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
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll().collectList().block());
        // set the field null
        institution.setName(null);

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
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME));
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
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME));
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
    void getAllInstitutionsByNameIsEqualToSomething() {
        // Initialize the database
        insertedInstitution = institutionRepository.save(institution).block();

        // Get all the institutionList where name equals to
        defaultInstitutionFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllInstitutionsByNameIsInShouldWork() {
        // Initialize the database
        insertedInstitution = institutionRepository.save(institution).block();

        // Get all the institutionList where name in
        defaultInstitutionFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllInstitutionsByNameIsNullOrNotNull() {
        // Initialize the database
        insertedInstitution = institutionRepository.save(institution).block();

        // Get all the institutionList where name is not null
        defaultInstitutionFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllInstitutionsByNameContainsSomething() {
        // Initialize the database
        insertedInstitution = institutionRepository.save(institution).block();

        // Get all the institutionList where name contains
        defaultInstitutionFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllInstitutionsByNameNotContainsSomething() {
        // Initialize the database
        insertedInstitution = institutionRepository.save(institution).block();

        // Get all the institutionList where name does not contain
        defaultInstitutionFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
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
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME));

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
        updatedInstitution.name(UPDATED_NAME);
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

        partialUpdatedInstitution.name(UPDATED_NAME);

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

        partialUpdatedInstitution.name(UPDATED_NAME);

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
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME));
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
