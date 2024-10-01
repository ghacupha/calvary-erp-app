package io.github.erp.web.rest;

import static io.github.erp.domain.EntitySubscriptionAsserts.*;
import static io.github.erp.web.rest.TestUtil.createUpdateProxyForBean;
import static io.github.erp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.erp.IntegrationTest;
import io.github.erp.domain.EntitySubscription;
import io.github.erp.domain.Institution;
import io.github.erp.repository.EntityManager;
import io.github.erp.repository.EntitySubscriptionRepository;
import io.github.erp.repository.InstitutionRepository;
import io.github.erp.repository.search.EntitySubscriptionSearchRepository;
import io.github.erp.service.dto.EntitySubscriptionDTO;
import io.github.erp.service.mapper.EntitySubscriptionMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
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
 * Integration tests for the {@link EntitySubscriptionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EntitySubscriptionResourceIT {

    private static final UUID DEFAULT_SUBSCRIPTION_TOKEN = UUID.randomUUID();
    private static final UUID UPDATED_SUBSCRIPTION_TOKEN = UUID.randomUUID();

    private static final ZonedDateTime DEFAULT_START_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_START_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_END_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_END_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/entity-subscriptions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/entity-subscriptions/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EntitySubscriptionRepository entitySubscriptionRepository;

    @Autowired
    private EntitySubscriptionMapper entitySubscriptionMapper;

    @Autowired
    private EntitySubscriptionSearchRepository entitySubscriptionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private EntitySubscription entitySubscription;

    private EntitySubscription insertedEntitySubscription;

    @Autowired
    private InstitutionRepository institutionRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EntitySubscription createEntity() {
        return new EntitySubscription()
            .subscriptionToken(DEFAULT_SUBSCRIPTION_TOKEN)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EntitySubscription createUpdatedEntity() {
        return new EntitySubscription()
            .subscriptionToken(UPDATED_SUBSCRIPTION_TOKEN)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(EntitySubscription.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        entitySubscription = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedEntitySubscription != null) {
            entitySubscriptionRepository.delete(insertedEntitySubscription).block();
            entitySubscriptionSearchRepository.delete(insertedEntitySubscription).block();
            insertedEntitySubscription = null;
        }
        deleteEntities(em);
    }

    @Test
    void createEntitySubscription() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        // Create the EntitySubscription
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);
        var returnedEntitySubscriptionDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(entitySubscriptionDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(EntitySubscriptionDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the EntitySubscription in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEntitySubscription = entitySubscriptionMapper.toEntity(returnedEntitySubscriptionDTO);
        assertEntitySubscriptionUpdatableFieldsEquals(
            returnedEntitySubscription,
            getPersistedEntitySubscription(returnedEntitySubscription)
        );

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedEntitySubscription = returnedEntitySubscription;
    }

    @Test
    void createEntitySubscriptionWithExistingId() throws Exception {
        // Create the EntitySubscription with an existing ID
        entitySubscription.setId(1L);
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(entitySubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EntitySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkSubscriptionTokenIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        // set the field null
        entitySubscription.setSubscriptionToken(null);

        // Create the EntitySubscription, which fails.
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(entitySubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        // set the field null
        entitySubscription.setStartDate(null);

        // Create the EntitySubscription, which fails.
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(entitySubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        // set the field null
        entitySubscription.setEndDate(null);

        // Create the EntitySubscription, which fails.
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(entitySubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllEntitySubscriptions() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        // Get all the entitySubscriptionList
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
            .value(hasItem(entitySubscription.getId().intValue()))
            .jsonPath("$.[*].subscriptionToken")
            .value(hasItem(DEFAULT_SUBSCRIPTION_TOKEN.toString()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(sameInstant(DEFAULT_START_DATE)))
            .jsonPath("$.[*].endDate")
            .value(hasItem(sameInstant(DEFAULT_END_DATE)));
    }

    @Test
    void getEntitySubscription() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        // Get the entitySubscription
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, entitySubscription.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(entitySubscription.getId().intValue()))
            .jsonPath("$.subscriptionToken")
            .value(is(DEFAULT_SUBSCRIPTION_TOKEN.toString()))
            .jsonPath("$.startDate")
            .value(is(sameInstant(DEFAULT_START_DATE)))
            .jsonPath("$.endDate")
            .value(is(sameInstant(DEFAULT_END_DATE)));
    }

    @Test
    void getEntitySubscriptionsByIdFiltering() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        Long id = entitySubscription.getId();

        defaultEntitySubscriptionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultEntitySubscriptionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultEntitySubscriptionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllEntitySubscriptionsBySubscriptionTokenIsEqualToSomething() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        // Get all the entitySubscriptionList where subscriptionToken equals to
        defaultEntitySubscriptionFiltering(
            "subscriptionToken.equals=" + DEFAULT_SUBSCRIPTION_TOKEN,
            "subscriptionToken.equals=" + UPDATED_SUBSCRIPTION_TOKEN
        );
    }

    @Test
    void getAllEntitySubscriptionsBySubscriptionTokenIsInShouldWork() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        // Get all the entitySubscriptionList where subscriptionToken in
        defaultEntitySubscriptionFiltering(
            "subscriptionToken.in=" + DEFAULT_SUBSCRIPTION_TOKEN + "," + UPDATED_SUBSCRIPTION_TOKEN,
            "subscriptionToken.in=" + UPDATED_SUBSCRIPTION_TOKEN
        );
    }

    @Test
    void getAllEntitySubscriptionsBySubscriptionTokenIsNullOrNotNull() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        // Get all the entitySubscriptionList where subscriptionToken is not null
        defaultEntitySubscriptionFiltering("subscriptionToken.specified=true", "subscriptionToken.specified=false");
    }

    @Test
    void getAllEntitySubscriptionsByStartDateIsEqualToSomething() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        // Get all the entitySubscriptionList where startDate equals to
        defaultEntitySubscriptionFiltering("startDate.equals=" + DEFAULT_START_DATE, "startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    void getAllEntitySubscriptionsByStartDateIsInShouldWork() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        // Get all the entitySubscriptionList where startDate in
        defaultEntitySubscriptionFiltering(
            "startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE,
            "startDate.in=" + UPDATED_START_DATE
        );
    }

    @Test
    void getAllEntitySubscriptionsByStartDateIsNullOrNotNull() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        // Get all the entitySubscriptionList where startDate is not null
        defaultEntitySubscriptionFiltering("startDate.specified=true", "startDate.specified=false");
    }

    @Test
    void getAllEntitySubscriptionsByStartDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        // Get all the entitySubscriptionList where startDate is greater than or equal to
        defaultEntitySubscriptionFiltering(
            "startDate.greaterThanOrEqual=" + DEFAULT_START_DATE,
            "startDate.greaterThanOrEqual=" + UPDATED_START_DATE
        );
    }

    @Test
    void getAllEntitySubscriptionsByStartDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        // Get all the entitySubscriptionList where startDate is less than or equal to
        defaultEntitySubscriptionFiltering(
            "startDate.lessThanOrEqual=" + DEFAULT_START_DATE,
            "startDate.lessThanOrEqual=" + SMALLER_START_DATE
        );
    }

    @Test
    void getAllEntitySubscriptionsByStartDateIsLessThanSomething() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        // Get all the entitySubscriptionList where startDate is less than
        defaultEntitySubscriptionFiltering("startDate.lessThan=" + UPDATED_START_DATE, "startDate.lessThan=" + DEFAULT_START_DATE);
    }

    @Test
    void getAllEntitySubscriptionsByStartDateIsGreaterThanSomething() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        // Get all the entitySubscriptionList where startDate is greater than
        defaultEntitySubscriptionFiltering("startDate.greaterThan=" + SMALLER_START_DATE, "startDate.greaterThan=" + DEFAULT_START_DATE);
    }

    @Test
    void getAllEntitySubscriptionsByEndDateIsEqualToSomething() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        // Get all the entitySubscriptionList where endDate equals to
        defaultEntitySubscriptionFiltering("endDate.equals=" + DEFAULT_END_DATE, "endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    void getAllEntitySubscriptionsByEndDateIsInShouldWork() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        // Get all the entitySubscriptionList where endDate in
        defaultEntitySubscriptionFiltering("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE, "endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    void getAllEntitySubscriptionsByEndDateIsNullOrNotNull() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        // Get all the entitySubscriptionList where endDate is not null
        defaultEntitySubscriptionFiltering("endDate.specified=true", "endDate.specified=false");
    }

    @Test
    void getAllEntitySubscriptionsByEndDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        // Get all the entitySubscriptionList where endDate is greater than or equal to
        defaultEntitySubscriptionFiltering(
            "endDate.greaterThanOrEqual=" + DEFAULT_END_DATE,
            "endDate.greaterThanOrEqual=" + UPDATED_END_DATE
        );
    }

    @Test
    void getAllEntitySubscriptionsByEndDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        // Get all the entitySubscriptionList where endDate is less than or equal to
        defaultEntitySubscriptionFiltering("endDate.lessThanOrEqual=" + DEFAULT_END_DATE, "endDate.lessThanOrEqual=" + SMALLER_END_DATE);
    }

    @Test
    void getAllEntitySubscriptionsByEndDateIsLessThanSomething() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        // Get all the entitySubscriptionList where endDate is less than
        defaultEntitySubscriptionFiltering("endDate.lessThan=" + UPDATED_END_DATE, "endDate.lessThan=" + DEFAULT_END_DATE);
    }

    @Test
    void getAllEntitySubscriptionsByEndDateIsGreaterThanSomething() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        // Get all the entitySubscriptionList where endDate is greater than
        defaultEntitySubscriptionFiltering("endDate.greaterThan=" + SMALLER_END_DATE, "endDate.greaterThan=" + DEFAULT_END_DATE);
    }

    @Test
    void getAllEntitySubscriptionsByInstitutionIsEqualToSomething() {
        Institution institution = InstitutionResourceIT.createEntity();
        institutionRepository.save(institution).block();
        Long institutionId = institution.getId();
        entitySubscription.setInstitutionId(institutionId);
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();
        // Get all the entitySubscriptionList where institution equals to institutionId
        defaultEntitySubscriptionShouldBeFound("institutionId.equals=" + institutionId);

        // Get all the entitySubscriptionList where institution equals to (institutionId + 1)
        defaultEntitySubscriptionShouldNotBeFound("institutionId.equals=" + (institutionId + 1));
    }

    private void defaultEntitySubscriptionFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultEntitySubscriptionShouldBeFound(shouldBeFound);
        defaultEntitySubscriptionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEntitySubscriptionShouldBeFound(String filter) {
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
            .value(hasItem(entitySubscription.getId().intValue()))
            .jsonPath("$.[*].subscriptionToken")
            .value(hasItem(DEFAULT_SUBSCRIPTION_TOKEN.toString()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(sameInstant(DEFAULT_START_DATE)))
            .jsonPath("$.[*].endDate")
            .value(hasItem(sameInstant(DEFAULT_END_DATE)));

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
    private void defaultEntitySubscriptionShouldNotBeFound(String filter) {
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
    void getNonExistingEntitySubscription() {
        // Get the entitySubscription
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEntitySubscription() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        entitySubscriptionSearchRepository.save(entitySubscription).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());

        // Update the entitySubscription
        EntitySubscription updatedEntitySubscription = entitySubscriptionRepository.findById(entitySubscription.getId()).block();
        updatedEntitySubscription.subscriptionToken(UPDATED_SUBSCRIPTION_TOKEN).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(updatedEntitySubscription);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, entitySubscriptionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(entitySubscriptionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EntitySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEntitySubscriptionToMatchAllProperties(updatedEntitySubscription);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<EntitySubscription> entitySubscriptionSearchList = Streamable.of(
                    entitySubscriptionSearchRepository.findAll().collectList().block()
                ).toList();
                EntitySubscription testEntitySubscriptionSearch = entitySubscriptionSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertEntitySubscriptionAllPropertiesEquals(testEntitySubscriptionSearch, updatedEntitySubscription);
                assertEntitySubscriptionUpdatableFieldsEquals(testEntitySubscriptionSearch, updatedEntitySubscription);
            });
    }

    @Test
    void putNonExistingEntitySubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        entitySubscription.setId(longCount.incrementAndGet());

        // Create the EntitySubscription
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, entitySubscriptionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(entitySubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EntitySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchEntitySubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        entitySubscription.setId(longCount.incrementAndGet());

        // Create the EntitySubscription
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(entitySubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EntitySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamEntitySubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        entitySubscription.setId(longCount.incrementAndGet());

        // Create the EntitySubscription
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(entitySubscriptionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EntitySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateEntitySubscriptionWithPatch() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the entitySubscription using partial update
        EntitySubscription partialUpdatedEntitySubscription = new EntitySubscription();
        partialUpdatedEntitySubscription.setId(entitySubscription.getId());

        partialUpdatedEntitySubscription.startDate(UPDATED_START_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEntitySubscription.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEntitySubscription))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EntitySubscription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEntitySubscriptionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEntitySubscription, entitySubscription),
            getPersistedEntitySubscription(entitySubscription)
        );
    }

    @Test
    void fullUpdateEntitySubscriptionWithPatch() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the entitySubscription using partial update
        EntitySubscription partialUpdatedEntitySubscription = new EntitySubscription();
        partialUpdatedEntitySubscription.setId(entitySubscription.getId());

        partialUpdatedEntitySubscription
            .subscriptionToken(UPDATED_SUBSCRIPTION_TOKEN)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEntitySubscription.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEntitySubscription))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EntitySubscription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEntitySubscriptionUpdatableFieldsEquals(
            partialUpdatedEntitySubscription,
            getPersistedEntitySubscription(partialUpdatedEntitySubscription)
        );
    }

    @Test
    void patchNonExistingEntitySubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        entitySubscription.setId(longCount.incrementAndGet());

        // Create the EntitySubscription
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, entitySubscriptionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(entitySubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EntitySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchEntitySubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        entitySubscription.setId(longCount.incrementAndGet());

        // Create the EntitySubscription
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(entitySubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EntitySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamEntitySubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        entitySubscription.setId(longCount.incrementAndGet());

        // Create the EntitySubscription
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(entitySubscriptionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EntitySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteEntitySubscription() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();
        entitySubscriptionRepository.save(entitySubscription).block();
        entitySubscriptionSearchRepository.save(entitySubscription).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the entitySubscription
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, entitySubscription.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchEntitySubscription() {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.save(entitySubscription).block();
        entitySubscriptionSearchRepository.save(entitySubscription).block();

        // Search the entitySubscription
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + entitySubscription.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(entitySubscription.getId().intValue()))
            .jsonPath("$.[*].subscriptionToken")
            .value(hasItem(DEFAULT_SUBSCRIPTION_TOKEN.toString()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(sameInstant(DEFAULT_START_DATE)))
            .jsonPath("$.[*].endDate")
            .value(hasItem(sameInstant(DEFAULT_END_DATE)));
    }

    protected long getRepositoryCount() {
        return entitySubscriptionRepository.count().block();
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

    protected EntitySubscription getPersistedEntitySubscription(EntitySubscription entitySubscription) {
        return entitySubscriptionRepository.findById(entitySubscription.getId()).block();
    }

    protected void assertPersistedEntitySubscriptionToMatchAllProperties(EntitySubscription expectedEntitySubscription) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEntitySubscriptionAllPropertiesEquals(expectedEntitySubscription, getPersistedEntitySubscription(expectedEntitySubscription));
        assertEntitySubscriptionUpdatableFieldsEquals(
            expectedEntitySubscription,
            getPersistedEntitySubscription(expectedEntitySubscription)
        );
    }

    protected void assertPersistedEntitySubscriptionToMatchUpdatableProperties(EntitySubscription expectedEntitySubscription) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEntitySubscriptionAllUpdatablePropertiesEquals(expectedEntitySubscription, getPersistedEntitySubscription(expectedEntitySubscription));
        assertEntitySubscriptionUpdatableFieldsEquals(
            expectedEntitySubscription,
            getPersistedEntitySubscription(expectedEntitySubscription)
        );
    }
}
