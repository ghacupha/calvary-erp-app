package io.github.erp.web.rest;

import static io.github.erp.domain.InstitutionAsserts.*;
import static io.github.erp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.erp.IntegrationTest;
import io.github.erp.domain.Institution;
import io.github.erp.repository.InstitutionRepository;
import io.github.erp.repository.search.InstitutionSearchRepository;
import io.github.erp.service.dto.InstitutionDTO;
import io.github.erp.service.mapper.InstitutionMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link InstitutionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private MockMvc restInstitutionMockMvc;

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

    @BeforeEach
    public void initTest() {
        institution = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedInstitution != null) {
            institutionRepository.delete(insertedInstitution);
            institutionSearchRepository.delete(insertedInstitution);
            insertedInstitution = null;
        }
    }

    @Test
    @Transactional
    void createInstitution() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);
        var returnedInstitutionDTO = om.readValue(
            restInstitutionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(institutionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InstitutionDTO.class
        );

        // Validate the Institution in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInstitution = institutionMapper.toEntity(returnedInstitutionDTO);
        assertInstitutionUpdatableFieldsEquals(returnedInstitution, getPersistedInstitution(returnedInstitution));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedInstitution = returnedInstitution;
    }

    @Test
    @Transactional
    void createInstitutionWithExistingId() throws Exception {
        // Create the Institution with an existing ID
        institution.setId(1L);
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restInstitutionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(institutionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Institution in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        // set the field null
        institution.setName(null);

        // Create the Institution, which fails.
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        restInstitutionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(institutionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllInstitutions() throws Exception {
        // Initialize the database
        insertedInstitution = institutionRepository.saveAndFlush(institution);

        // Get all the institutionList
        restInstitutionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(institution.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getInstitution() throws Exception {
        // Initialize the database
        insertedInstitution = institutionRepository.saveAndFlush(institution);

        // Get the institution
        restInstitutionMockMvc
            .perform(get(ENTITY_API_URL_ID, institution.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(institution.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getInstitutionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedInstitution = institutionRepository.saveAndFlush(institution);

        Long id = institution.getId();

        defaultInstitutionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultInstitutionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultInstitutionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllInstitutionsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstitution = institutionRepository.saveAndFlush(institution);

        // Get all the institutionList where name equals to
        defaultInstitutionFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllInstitutionsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstitution = institutionRepository.saveAndFlush(institution);

        // Get all the institutionList where name in
        defaultInstitutionFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllInstitutionsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstitution = institutionRepository.saveAndFlush(institution);

        // Get all the institutionList where name is not null
        defaultInstitutionFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllInstitutionsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedInstitution = institutionRepository.saveAndFlush(institution);

        // Get all the institutionList where name contains
        defaultInstitutionFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllInstitutionsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInstitution = institutionRepository.saveAndFlush(institution);

        // Get all the institutionList where name does not contain
        defaultInstitutionFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    private void defaultInstitutionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultInstitutionShouldBeFound(shouldBeFound);
        defaultInstitutionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInstitutionShouldBeFound(String filter) throws Exception {
        restInstitutionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(institution.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restInstitutionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInstitutionShouldNotBeFound(String filter) throws Exception {
        restInstitutionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInstitutionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInstitution() throws Exception {
        // Get the institution
        restInstitutionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInstitution() throws Exception {
        // Initialize the database
        insertedInstitution = institutionRepository.saveAndFlush(institution);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        institutionSearchRepository.save(institution);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());

        // Update the institution
        Institution updatedInstitution = institutionRepository.findById(institution.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInstitution are not directly saved in db
        em.detach(updatedInstitution);
        updatedInstitution.name(UPDATED_NAME);
        InstitutionDTO institutionDTO = institutionMapper.toDto(updatedInstitution);

        restInstitutionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, institutionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(institutionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Institution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInstitutionToMatchAllProperties(updatedInstitution);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Institution> institutionSearchList = Streamable.of(institutionSearchRepository.findAll()).toList();
                Institution testInstitutionSearch = institutionSearchList.get(searchDatabaseSizeAfter - 1);

                assertInstitutionAllPropertiesEquals(testInstitutionSearch, updatedInstitution);
            });
    }

    @Test
    @Transactional
    void putNonExistingInstitution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        institution.setId(longCount.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInstitutionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, institutionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(institutionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Institution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchInstitution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        institution.setId(longCount.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstitutionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(institutionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Institution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInstitution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        institution.setId(longCount.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstitutionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(institutionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Institution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateInstitutionWithPatch() throws Exception {
        // Initialize the database
        insertedInstitution = institutionRepository.saveAndFlush(institution);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the institution using partial update
        Institution partialUpdatedInstitution = new Institution();
        partialUpdatedInstitution.setId(institution.getId());

        restInstitutionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstitution.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInstitution))
            )
            .andExpect(status().isOk());

        // Validate the Institution in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstitutionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInstitution, institution),
            getPersistedInstitution(institution)
        );
    }

    @Test
    @Transactional
    void fullUpdateInstitutionWithPatch() throws Exception {
        // Initialize the database
        insertedInstitution = institutionRepository.saveAndFlush(institution);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the institution using partial update
        Institution partialUpdatedInstitution = new Institution();
        partialUpdatedInstitution.setId(institution.getId());

        partialUpdatedInstitution.name(UPDATED_NAME);

        restInstitutionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstitution.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInstitution))
            )
            .andExpect(status().isOk());

        // Validate the Institution in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstitutionUpdatableFieldsEquals(partialUpdatedInstitution, getPersistedInstitution(partialUpdatedInstitution));
    }

    @Test
    @Transactional
    void patchNonExistingInstitution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        institution.setId(longCount.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInstitutionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, institutionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(institutionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Institution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInstitution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        institution.setId(longCount.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstitutionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(institutionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Institution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInstitution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        institution.setId(longCount.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstitutionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(institutionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Institution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteInstitution() throws Exception {
        // Initialize the database
        insertedInstitution = institutionRepository.saveAndFlush(institution);
        institutionRepository.save(institution);
        institutionSearchRepository.save(institution);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the institution
        restInstitutionMockMvc
            .perform(delete(ENTITY_API_URL_ID, institution.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchInstitution() throws Exception {
        // Initialize the database
        insertedInstitution = institutionRepository.saveAndFlush(institution);
        institutionSearchRepository.save(institution);

        // Search the institution
        restInstitutionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + institution.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(institution.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    protected long getRepositoryCount() {
        return institutionRepository.count();
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
        return institutionRepository.findById(institution.getId()).orElseThrow();
    }

    protected void assertPersistedInstitutionToMatchAllProperties(Institution expectedInstitution) {
        assertInstitutionAllPropertiesEquals(expectedInstitution, getPersistedInstitution(expectedInstitution));
    }

    protected void assertPersistedInstitutionToMatchUpdatableProperties(Institution expectedInstitution) {
        assertInstitutionAllUpdatablePropertiesEquals(expectedInstitution, getPersistedInstitution(expectedInstitution));
    }
}
