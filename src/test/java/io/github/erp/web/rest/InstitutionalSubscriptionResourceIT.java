package io.github.erp.web.rest;

import static io.github.erp.domain.InstitutionalSubscriptionAsserts.*;
import static io.github.erp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.erp.IntegrationTest;
import io.github.erp.domain.Institution;
import io.github.erp.domain.InstitutionalSubscription;
import io.github.erp.repository.EntityManager;
import io.github.erp.repository.InstitutionRepository;
import io.github.erp.repository.InstitutionalSubscriptionRepository;
import io.github.erp.repository.search.InstitutionalSubscriptionSearchRepository;
import io.github.erp.service.InstitutionalSubscriptionService;
import io.github.erp.service.dto.InstitutionalSubscriptionDTO;
import io.github.erp.service.mapper.InstitutionalSubscriptionMapper;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link InstitutionalSubscriptionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class InstitutionalSubscriptionResourceIT {

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_START_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_EXPIRY_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EXPIRY_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_EXPIRY_DATE = LocalDate.ofEpochDay(-1L);

    private static final Integer DEFAULT_MEMBER_LIMIT = 1;
    private static final Integer UPDATED_MEMBER_LIMIT = 2;
    private static final Integer SMALLER_MEMBER_LIMIT = 1 - 1;

    private static final String ENTITY_API_URL = "/api/institutional-subscriptions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/institutional-subscriptions/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InstitutionalSubscriptionRepository institutionalSubscriptionRepository;

    @Mock
    private InstitutionalSubscriptionRepository institutionalSubscriptionRepositoryMock;

    @Autowired
    private InstitutionalSubscriptionMapper institutionalSubscriptionMapper;

    @Mock
    private InstitutionalSubscriptionService institutionalSubscriptionServiceMock;

    @Autowired
    private InstitutionalSubscriptionSearchRepository institutionalSubscriptionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private InstitutionalSubscription institutionalSubscription;

    private InstitutionalSubscription insertedInstitutionalSubscription;

    @Autowired
    private InstitutionRepository institutionRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InstitutionalSubscription createEntity(EntityManager em) {
        InstitutionalSubscription institutionalSubscription = new InstitutionalSubscription()
            .startDate(DEFAULT_START_DATE)
            .expiryDate(DEFAULT_EXPIRY_DATE)
            .memberLimit(DEFAULT_MEMBER_LIMIT);
        // Add required entity
        Institution institution;
        institution = em.insert(InstitutionResourceIT.createEntity()).block();
        institutionalSubscription.setInstitution(institution);
        return institutionalSubscription;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InstitutionalSubscription createUpdatedEntity(EntityManager em) {
        InstitutionalSubscription updatedInstitutionalSubscription = new InstitutionalSubscription()
            .startDate(UPDATED_START_DATE)
            .expiryDate(UPDATED_EXPIRY_DATE)
            .memberLimit(UPDATED_MEMBER_LIMIT);
        // Add required entity
        Institution institution;
        institution = em.insert(InstitutionResourceIT.createUpdatedEntity()).block();
        updatedInstitutionalSubscription.setInstitution(institution);
        return updatedInstitutionalSubscription;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(InstitutionalSubscription.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        InstitutionResourceIT.deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        institutionalSubscription = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedInstitutionalSubscription != null) {
            institutionalSubscriptionRepository.delete(insertedInstitutionalSubscription).block();
            institutionalSubscriptionSearchRepository.delete(insertedInstitutionalSubscription).block();
            insertedInstitutionalSubscription = null;
        }
        deleteEntities(em);
    }

    @Test
    void createInstitutionalSubscription() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        // Create the InstitutionalSubscription
        InstitutionalSubscriptionDTO institutionalSubscriptionDTO = institutionalSubscriptionMapper.toDto(institutionalSubscription);
        var returnedInstitutionalSubscriptionDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(institutionalSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(InstitutionalSubscriptionDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the InstitutionalSubscription in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInstitutionalSubscription = institutionalSubscriptionMapper.toEntity(returnedInstitutionalSubscriptionDTO);
        assertInstitutionalSubscriptionUpdatableFieldsEquals(
            returnedInstitutionalSubscription,
            getPersistedInstitutionalSubscription(returnedInstitutionalSubscription)
        );

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(
                    institutionalSubscriptionSearchRepository.findAll().collectList().block()
                );
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedInstitutionalSubscription = returnedInstitutionalSubscription;
    }

    @Test
    void createInstitutionalSubscriptionWithExistingId() throws Exception {
        // Create the InstitutionalSubscription with an existing ID
        institutionalSubscription.setId(1L);
        InstitutionalSubscriptionDTO institutionalSubscriptionDTO = institutionalSubscriptionMapper.toDto(institutionalSubscription);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(institutionalSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InstitutionalSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        // set the field null
        institutionalSubscription.setStartDate(null);

        // Create the InstitutionalSubscription, which fails.
        InstitutionalSubscriptionDTO institutionalSubscriptionDTO = institutionalSubscriptionMapper.toDto(institutionalSubscription);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(institutionalSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkExpiryDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        // set the field null
        institutionalSubscription.setExpiryDate(null);

        // Create the InstitutionalSubscription, which fails.
        InstitutionalSubscriptionDTO institutionalSubscriptionDTO = institutionalSubscriptionMapper.toDto(institutionalSubscription);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(institutionalSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkMemberLimitIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        // set the field null
        institutionalSubscription.setMemberLimit(null);

        // Create the InstitutionalSubscription, which fails.
        InstitutionalSubscriptionDTO institutionalSubscriptionDTO = institutionalSubscriptionMapper.toDto(institutionalSubscription);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(institutionalSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllInstitutionalSubscriptions() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList
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
            .value(hasItem(institutionalSubscription.getId().intValue()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].expiryDate")
            .value(hasItem(DEFAULT_EXPIRY_DATE.toString()))
            .jsonPath("$.[*].memberLimit")
            .value(hasItem(DEFAULT_MEMBER_LIMIT));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInstitutionalSubscriptionsWithEagerRelationshipsIsEnabled() {
        when(institutionalSubscriptionServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(institutionalSubscriptionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInstitutionalSubscriptionsWithEagerRelationshipsIsNotEnabled() {
        when(institutionalSubscriptionServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(institutionalSubscriptionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getInstitutionalSubscription() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get the institutionalSubscription
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, institutionalSubscription.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(institutionalSubscription.getId().intValue()))
            .jsonPath("$.startDate")
            .value(is(DEFAULT_START_DATE.toString()))
            .jsonPath("$.expiryDate")
            .value(is(DEFAULT_EXPIRY_DATE.toString()))
            .jsonPath("$.memberLimit")
            .value(is(DEFAULT_MEMBER_LIMIT));
    }

    @Test
    void getInstitutionalSubscriptionsByIdFiltering() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        Long id = institutionalSubscription.getId();

        defaultInstitutionalSubscriptionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultInstitutionalSubscriptionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultInstitutionalSubscriptionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllInstitutionalSubscriptionsByStartDateIsEqualToSomething() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList where startDate equals to
        defaultInstitutionalSubscriptionFiltering("startDate.equals=" + DEFAULT_START_DATE, "startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    void getAllInstitutionalSubscriptionsByStartDateIsInShouldWork() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList where startDate in
        defaultInstitutionalSubscriptionFiltering(
            "startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE,
            "startDate.in=" + UPDATED_START_DATE
        );
    }

    @Test
    void getAllInstitutionalSubscriptionsByStartDateIsNullOrNotNull() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList where startDate is not null
        defaultInstitutionalSubscriptionFiltering("startDate.specified=true", "startDate.specified=false");
    }

    @Test
    void getAllInstitutionalSubscriptionsByStartDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList where startDate is greater than or equal to
        defaultInstitutionalSubscriptionFiltering(
            "startDate.greaterThanOrEqual=" + DEFAULT_START_DATE,
            "startDate.greaterThanOrEqual=" + UPDATED_START_DATE
        );
    }

    @Test
    void getAllInstitutionalSubscriptionsByStartDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList where startDate is less than or equal to
        defaultInstitutionalSubscriptionFiltering(
            "startDate.lessThanOrEqual=" + DEFAULT_START_DATE,
            "startDate.lessThanOrEqual=" + SMALLER_START_DATE
        );
    }

    @Test
    void getAllInstitutionalSubscriptionsByStartDateIsLessThanSomething() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList where startDate is less than
        defaultInstitutionalSubscriptionFiltering("startDate.lessThan=" + UPDATED_START_DATE, "startDate.lessThan=" + DEFAULT_START_DATE);
    }

    @Test
    void getAllInstitutionalSubscriptionsByStartDateIsGreaterThanSomething() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList where startDate is greater than
        defaultInstitutionalSubscriptionFiltering(
            "startDate.greaterThan=" + SMALLER_START_DATE,
            "startDate.greaterThan=" + DEFAULT_START_DATE
        );
    }

    @Test
    void getAllInstitutionalSubscriptionsByExpiryDateIsEqualToSomething() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList where expiryDate equals to
        defaultInstitutionalSubscriptionFiltering("expiryDate.equals=" + DEFAULT_EXPIRY_DATE, "expiryDate.equals=" + UPDATED_EXPIRY_DATE);
    }

    @Test
    void getAllInstitutionalSubscriptionsByExpiryDateIsInShouldWork() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList where expiryDate in
        defaultInstitutionalSubscriptionFiltering(
            "expiryDate.in=" + DEFAULT_EXPIRY_DATE + "," + UPDATED_EXPIRY_DATE,
            "expiryDate.in=" + UPDATED_EXPIRY_DATE
        );
    }

    @Test
    void getAllInstitutionalSubscriptionsByExpiryDateIsNullOrNotNull() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList where expiryDate is not null
        defaultInstitutionalSubscriptionFiltering("expiryDate.specified=true", "expiryDate.specified=false");
    }

    @Test
    void getAllInstitutionalSubscriptionsByExpiryDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList where expiryDate is greater than or equal to
        defaultInstitutionalSubscriptionFiltering(
            "expiryDate.greaterThanOrEqual=" + DEFAULT_EXPIRY_DATE,
            "expiryDate.greaterThanOrEqual=" + UPDATED_EXPIRY_DATE
        );
    }

    @Test
    void getAllInstitutionalSubscriptionsByExpiryDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList where expiryDate is less than or equal to
        defaultInstitutionalSubscriptionFiltering(
            "expiryDate.lessThanOrEqual=" + DEFAULT_EXPIRY_DATE,
            "expiryDate.lessThanOrEqual=" + SMALLER_EXPIRY_DATE
        );
    }

    @Test
    void getAllInstitutionalSubscriptionsByExpiryDateIsLessThanSomething() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList where expiryDate is less than
        defaultInstitutionalSubscriptionFiltering(
            "expiryDate.lessThan=" + UPDATED_EXPIRY_DATE,
            "expiryDate.lessThan=" + DEFAULT_EXPIRY_DATE
        );
    }

    @Test
    void getAllInstitutionalSubscriptionsByExpiryDateIsGreaterThanSomething() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList where expiryDate is greater than
        defaultInstitutionalSubscriptionFiltering(
            "expiryDate.greaterThan=" + SMALLER_EXPIRY_DATE,
            "expiryDate.greaterThan=" + DEFAULT_EXPIRY_DATE
        );
    }

    @Test
    void getAllInstitutionalSubscriptionsByMemberLimitIsEqualToSomething() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList where memberLimit equals to
        defaultInstitutionalSubscriptionFiltering(
            "memberLimit.equals=" + DEFAULT_MEMBER_LIMIT,
            "memberLimit.equals=" + UPDATED_MEMBER_LIMIT
        );
    }

    @Test
    void getAllInstitutionalSubscriptionsByMemberLimitIsInShouldWork() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList where memberLimit in
        defaultInstitutionalSubscriptionFiltering(
            "memberLimit.in=" + DEFAULT_MEMBER_LIMIT + "," + UPDATED_MEMBER_LIMIT,
            "memberLimit.in=" + UPDATED_MEMBER_LIMIT
        );
    }

    @Test
    void getAllInstitutionalSubscriptionsByMemberLimitIsNullOrNotNull() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList where memberLimit is not null
        defaultInstitutionalSubscriptionFiltering("memberLimit.specified=true", "memberLimit.specified=false");
    }

    @Test
    void getAllInstitutionalSubscriptionsByMemberLimitIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList where memberLimit is greater than or equal to
        defaultInstitutionalSubscriptionFiltering(
            "memberLimit.greaterThanOrEqual=" + DEFAULT_MEMBER_LIMIT,
            "memberLimit.greaterThanOrEqual=" + UPDATED_MEMBER_LIMIT
        );
    }

    @Test
    void getAllInstitutionalSubscriptionsByMemberLimitIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList where memberLimit is less than or equal to
        defaultInstitutionalSubscriptionFiltering(
            "memberLimit.lessThanOrEqual=" + DEFAULT_MEMBER_LIMIT,
            "memberLimit.lessThanOrEqual=" + SMALLER_MEMBER_LIMIT
        );
    }

    @Test
    void getAllInstitutionalSubscriptionsByMemberLimitIsLessThanSomething() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList where memberLimit is less than
        defaultInstitutionalSubscriptionFiltering(
            "memberLimit.lessThan=" + UPDATED_MEMBER_LIMIT,
            "memberLimit.lessThan=" + DEFAULT_MEMBER_LIMIT
        );
    }

    @Test
    void getAllInstitutionalSubscriptionsByMemberLimitIsGreaterThanSomething() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        // Get all the institutionalSubscriptionList where memberLimit is greater than
        defaultInstitutionalSubscriptionFiltering(
            "memberLimit.greaterThan=" + SMALLER_MEMBER_LIMIT,
            "memberLimit.greaterThan=" + DEFAULT_MEMBER_LIMIT
        );
    }

    @Test
    void getAllInstitutionalSubscriptionsByInstitutionIsEqualToSomething() {
        Institution institution = InstitutionResourceIT.createEntity();
        institutionRepository.save(institution).block();
        Long institutionId = institution.getId();
        institutionalSubscription.setInstitutionId(institutionId);
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();
        // Get all the institutionalSubscriptionList where institution equals to institutionId
        defaultInstitutionalSubscriptionShouldBeFound("institutionId.equals=" + institutionId);

        // Get all the institutionalSubscriptionList where institution equals to (institutionId + 1)
        defaultInstitutionalSubscriptionShouldNotBeFound("institutionId.equals=" + (institutionId + 1));
    }

    private void defaultInstitutionalSubscriptionFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultInstitutionalSubscriptionShouldBeFound(shouldBeFound);
        defaultInstitutionalSubscriptionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInstitutionalSubscriptionShouldBeFound(String filter) {
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
            .value(hasItem(institutionalSubscription.getId().intValue()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].expiryDate")
            .value(hasItem(DEFAULT_EXPIRY_DATE.toString()))
            .jsonPath("$.[*].memberLimit")
            .value(hasItem(DEFAULT_MEMBER_LIMIT));

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
    private void defaultInstitutionalSubscriptionShouldNotBeFound(String filter) {
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
    void getNonExistingInstitutionalSubscription() {
        // Get the institutionalSubscription
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingInstitutionalSubscription() throws Exception {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        institutionalSubscriptionSearchRepository.save(institutionalSubscription).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());

        // Update the institutionalSubscription
        InstitutionalSubscription updatedInstitutionalSubscription = institutionalSubscriptionRepository
            .findById(institutionalSubscription.getId())
            .block();
        updatedInstitutionalSubscription.startDate(UPDATED_START_DATE).expiryDate(UPDATED_EXPIRY_DATE).memberLimit(UPDATED_MEMBER_LIMIT);
        InstitutionalSubscriptionDTO institutionalSubscriptionDTO = institutionalSubscriptionMapper.toDto(updatedInstitutionalSubscription);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, institutionalSubscriptionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(institutionalSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the InstitutionalSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInstitutionalSubscriptionToMatchAllProperties(updatedInstitutionalSubscription);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(
                    institutionalSubscriptionSearchRepository.findAll().collectList().block()
                );
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<InstitutionalSubscription> institutionalSubscriptionSearchList = Streamable.of(
                    institutionalSubscriptionSearchRepository.findAll().collectList().block()
                ).toList();
                InstitutionalSubscription testInstitutionalSubscriptionSearch = institutionalSubscriptionSearchList.get(
                    searchDatabaseSizeAfter - 1
                );

                // Test fails because reactive api returns an empty object instead of null
                // assertInstitutionalSubscriptionAllPropertiesEquals(testInstitutionalSubscriptionSearch, updatedInstitutionalSubscription);
                assertInstitutionalSubscriptionUpdatableFieldsEquals(testInstitutionalSubscriptionSearch, updatedInstitutionalSubscription);
            });
    }

    @Test
    void putNonExistingInstitutionalSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        institutionalSubscription.setId(longCount.incrementAndGet());

        // Create the InstitutionalSubscription
        InstitutionalSubscriptionDTO institutionalSubscriptionDTO = institutionalSubscriptionMapper.toDto(institutionalSubscription);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, institutionalSubscriptionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(institutionalSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InstitutionalSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchInstitutionalSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        institutionalSubscription.setId(longCount.incrementAndGet());

        // Create the InstitutionalSubscription
        InstitutionalSubscriptionDTO institutionalSubscriptionDTO = institutionalSubscriptionMapper.toDto(institutionalSubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(institutionalSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InstitutionalSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamInstitutionalSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        institutionalSubscription.setId(longCount.incrementAndGet());

        // Create the InstitutionalSubscription
        InstitutionalSubscriptionDTO institutionalSubscriptionDTO = institutionalSubscriptionMapper.toDto(institutionalSubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(institutionalSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the InstitutionalSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateInstitutionalSubscriptionWithPatch() throws Exception {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the institutionalSubscription using partial update
        InstitutionalSubscription partialUpdatedInstitutionalSubscription = new InstitutionalSubscription();
        partialUpdatedInstitutionalSubscription.setId(institutionalSubscription.getId());

        partialUpdatedInstitutionalSubscription.expiryDate(UPDATED_EXPIRY_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInstitutionalSubscription.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInstitutionalSubscription))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the InstitutionalSubscription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstitutionalSubscriptionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInstitutionalSubscription, institutionalSubscription),
            getPersistedInstitutionalSubscription(institutionalSubscription)
        );
    }

    @Test
    void fullUpdateInstitutionalSubscriptionWithPatch() throws Exception {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the institutionalSubscription using partial update
        InstitutionalSubscription partialUpdatedInstitutionalSubscription = new InstitutionalSubscription();
        partialUpdatedInstitutionalSubscription.setId(institutionalSubscription.getId());

        partialUpdatedInstitutionalSubscription
            .startDate(UPDATED_START_DATE)
            .expiryDate(UPDATED_EXPIRY_DATE)
            .memberLimit(UPDATED_MEMBER_LIMIT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInstitutionalSubscription.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInstitutionalSubscription))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the InstitutionalSubscription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstitutionalSubscriptionUpdatableFieldsEquals(
            partialUpdatedInstitutionalSubscription,
            getPersistedInstitutionalSubscription(partialUpdatedInstitutionalSubscription)
        );
    }

    @Test
    void patchNonExistingInstitutionalSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        institutionalSubscription.setId(longCount.incrementAndGet());

        // Create the InstitutionalSubscription
        InstitutionalSubscriptionDTO institutionalSubscriptionDTO = institutionalSubscriptionMapper.toDto(institutionalSubscription);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, institutionalSubscriptionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(institutionalSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InstitutionalSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchInstitutionalSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        institutionalSubscription.setId(longCount.incrementAndGet());

        // Create the InstitutionalSubscription
        InstitutionalSubscriptionDTO institutionalSubscriptionDTO = institutionalSubscriptionMapper.toDto(institutionalSubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(institutionalSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InstitutionalSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamInstitutionalSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        institutionalSubscription.setId(longCount.incrementAndGet());

        // Create the InstitutionalSubscription
        InstitutionalSubscriptionDTO institutionalSubscriptionDTO = institutionalSubscriptionMapper.toDto(institutionalSubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(institutionalSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the InstitutionalSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteInstitutionalSubscription() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();
        institutionalSubscriptionRepository.save(institutionalSubscription).block();
        institutionalSubscriptionSearchRepository.save(institutionalSubscription).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the institutionalSubscription
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, institutionalSubscription.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionalSubscriptionSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchInstitutionalSubscription() {
        // Initialize the database
        insertedInstitutionalSubscription = institutionalSubscriptionRepository.save(institutionalSubscription).block();
        institutionalSubscriptionSearchRepository.save(institutionalSubscription).block();

        // Search the institutionalSubscription
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + institutionalSubscription.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(institutionalSubscription.getId().intValue()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].expiryDate")
            .value(hasItem(DEFAULT_EXPIRY_DATE.toString()))
            .jsonPath("$.[*].memberLimit")
            .value(hasItem(DEFAULT_MEMBER_LIMIT));
    }

    protected long getRepositoryCount() {
        return institutionalSubscriptionRepository.count().block();
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

    protected InstitutionalSubscription getPersistedInstitutionalSubscription(InstitutionalSubscription institutionalSubscription) {
        return institutionalSubscriptionRepository.findById(institutionalSubscription.getId()).block();
    }

    protected void assertPersistedInstitutionalSubscriptionToMatchAllProperties(
        InstitutionalSubscription expectedInstitutionalSubscription
    ) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInstitutionalSubscriptionAllPropertiesEquals(expectedInstitutionalSubscription, getPersistedInstitutionalSubscription(expectedInstitutionalSubscription));
        assertInstitutionalSubscriptionUpdatableFieldsEquals(
            expectedInstitutionalSubscription,
            getPersistedInstitutionalSubscription(expectedInstitutionalSubscription)
        );
    }

    protected void assertPersistedInstitutionalSubscriptionToMatchUpdatableProperties(
        InstitutionalSubscription expectedInstitutionalSubscription
    ) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInstitutionalSubscriptionAllUpdatablePropertiesEquals(expectedInstitutionalSubscription, getPersistedInstitutionalSubscription(expectedInstitutionalSubscription));
        assertInstitutionalSubscriptionUpdatableFieldsEquals(
            expectedInstitutionalSubscription,
            getPersistedInstitutionalSubscription(expectedInstitutionalSubscription)
        );
    }
}
