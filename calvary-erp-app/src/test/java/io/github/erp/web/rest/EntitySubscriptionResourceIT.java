package io.github.erp.web.rest;

import static io.github.erp.domain.EntitySubscriptionAsserts.*;
import static io.github.erp.web.rest.TestUtil.createUpdateProxyForBean;
import static io.github.erp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.erp.IntegrationTest;
import io.github.erp.domain.EntitySubscription;
import io.github.erp.domain.Institution;
import io.github.erp.repository.EntitySubscriptionRepository;
import io.github.erp.repository.search.EntitySubscriptionSearchRepository;
import io.github.erp.service.EntitySubscriptionService;
import io.github.erp.service.dto.EntitySubscriptionDTO;
import io.github.erp.service.mapper.EntitySubscriptionMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EntitySubscriptionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
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

    @Mock
    private EntitySubscriptionRepository entitySubscriptionRepositoryMock;

    @Autowired
    private EntitySubscriptionMapper entitySubscriptionMapper;

    @Mock
    private EntitySubscriptionService entitySubscriptionServiceMock;

    @Autowired
    private EntitySubscriptionSearchRepository entitySubscriptionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEntitySubscriptionMockMvc;

    private EntitySubscription entitySubscription;

    private EntitySubscription insertedEntitySubscription;

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

    @BeforeEach
    public void initTest() {
        entitySubscription = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedEntitySubscription != null) {
            entitySubscriptionRepository.delete(insertedEntitySubscription);
            entitySubscriptionSearchRepository.delete(insertedEntitySubscription);
            insertedEntitySubscription = null;
        }
    }

    @Test
    @Transactional
    void createEntitySubscription() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        // Create the EntitySubscription
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);
        var returnedEntitySubscriptionDTO = om.readValue(
            restEntitySubscriptionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entitySubscriptionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EntitySubscriptionDTO.class
        );

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
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedEntitySubscription = returnedEntitySubscription;
    }

    @Test
    @Transactional
    void createEntitySubscriptionWithExistingId() throws Exception {
        // Create the EntitySubscription with an existing ID
        entitySubscription.setId(1L);
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restEntitySubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entitySubscriptionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EntitySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSubscriptionTokenIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        // set the field null
        entitySubscription.setSubscriptionToken(null);

        // Create the EntitySubscription, which fails.
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);

        restEntitySubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entitySubscriptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        // set the field null
        entitySubscription.setStartDate(null);

        // Create the EntitySubscription, which fails.
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);

        restEntitySubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entitySubscriptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        // set the field null
        entitySubscription.setEndDate(null);

        // Create the EntitySubscription, which fails.
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);

        restEntitySubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entitySubscriptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllEntitySubscriptions() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        // Get all the entitySubscriptionList
        restEntitySubscriptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(entitySubscription.getId().intValue())))
            .andExpect(jsonPath("$.[*].subscriptionToken").value(hasItem(DEFAULT_SUBSCRIPTION_TOKEN.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(sameInstant(DEFAULT_START_DATE))))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(sameInstant(DEFAULT_END_DATE))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEntitySubscriptionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(entitySubscriptionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEntitySubscriptionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(entitySubscriptionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEntitySubscriptionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(entitySubscriptionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEntitySubscriptionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(entitySubscriptionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEntitySubscription() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        // Get the entitySubscription
        restEntitySubscriptionMockMvc
            .perform(get(ENTITY_API_URL_ID, entitySubscription.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(entitySubscription.getId().intValue()))
            .andExpect(jsonPath("$.subscriptionToken").value(DEFAULT_SUBSCRIPTION_TOKEN.toString()))
            .andExpect(jsonPath("$.startDate").value(sameInstant(DEFAULT_START_DATE)))
            .andExpect(jsonPath("$.endDate").value(sameInstant(DEFAULT_END_DATE)));
    }

    @Test
    @Transactional
    void getEntitySubscriptionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        Long id = entitySubscription.getId();

        defaultEntitySubscriptionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultEntitySubscriptionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultEntitySubscriptionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEntitySubscriptionsBySubscriptionTokenIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        // Get all the entitySubscriptionList where subscriptionToken equals to
        defaultEntitySubscriptionFiltering(
            "subscriptionToken.equals=" + DEFAULT_SUBSCRIPTION_TOKEN,
            "subscriptionToken.equals=" + UPDATED_SUBSCRIPTION_TOKEN
        );
    }

    @Test
    @Transactional
    void getAllEntitySubscriptionsBySubscriptionTokenIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        // Get all the entitySubscriptionList where subscriptionToken in
        defaultEntitySubscriptionFiltering(
            "subscriptionToken.in=" + DEFAULT_SUBSCRIPTION_TOKEN + "," + UPDATED_SUBSCRIPTION_TOKEN,
            "subscriptionToken.in=" + UPDATED_SUBSCRIPTION_TOKEN
        );
    }

    @Test
    @Transactional
    void getAllEntitySubscriptionsBySubscriptionTokenIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        // Get all the entitySubscriptionList where subscriptionToken is not null
        defaultEntitySubscriptionFiltering("subscriptionToken.specified=true", "subscriptionToken.specified=false");
    }

    @Test
    @Transactional
    void getAllEntitySubscriptionsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        // Get all the entitySubscriptionList where startDate equals to
        defaultEntitySubscriptionFiltering("startDate.equals=" + DEFAULT_START_DATE, "startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllEntitySubscriptionsByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        // Get all the entitySubscriptionList where startDate in
        defaultEntitySubscriptionFiltering(
            "startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE,
            "startDate.in=" + UPDATED_START_DATE
        );
    }

    @Test
    @Transactional
    void getAllEntitySubscriptionsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        // Get all the entitySubscriptionList where startDate is not null
        defaultEntitySubscriptionFiltering("startDate.specified=true", "startDate.specified=false");
    }

    @Test
    @Transactional
    void getAllEntitySubscriptionsByStartDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        // Get all the entitySubscriptionList where startDate is greater than or equal to
        defaultEntitySubscriptionFiltering(
            "startDate.greaterThanOrEqual=" + DEFAULT_START_DATE,
            "startDate.greaterThanOrEqual=" + UPDATED_START_DATE
        );
    }

    @Test
    @Transactional
    void getAllEntitySubscriptionsByStartDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        // Get all the entitySubscriptionList where startDate is less than or equal to
        defaultEntitySubscriptionFiltering(
            "startDate.lessThanOrEqual=" + DEFAULT_START_DATE,
            "startDate.lessThanOrEqual=" + SMALLER_START_DATE
        );
    }

    @Test
    @Transactional
    void getAllEntitySubscriptionsByStartDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        // Get all the entitySubscriptionList where startDate is less than
        defaultEntitySubscriptionFiltering("startDate.lessThan=" + UPDATED_START_DATE, "startDate.lessThan=" + DEFAULT_START_DATE);
    }

    @Test
    @Transactional
    void getAllEntitySubscriptionsByStartDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        // Get all the entitySubscriptionList where startDate is greater than
        defaultEntitySubscriptionFiltering("startDate.greaterThan=" + SMALLER_START_DATE, "startDate.greaterThan=" + DEFAULT_START_DATE);
    }

    @Test
    @Transactional
    void getAllEntitySubscriptionsByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        // Get all the entitySubscriptionList where endDate equals to
        defaultEntitySubscriptionFiltering("endDate.equals=" + DEFAULT_END_DATE, "endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllEntitySubscriptionsByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        // Get all the entitySubscriptionList where endDate in
        defaultEntitySubscriptionFiltering("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE, "endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllEntitySubscriptionsByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        // Get all the entitySubscriptionList where endDate is not null
        defaultEntitySubscriptionFiltering("endDate.specified=true", "endDate.specified=false");
    }

    @Test
    @Transactional
    void getAllEntitySubscriptionsByEndDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        // Get all the entitySubscriptionList where endDate is greater than or equal to
        defaultEntitySubscriptionFiltering(
            "endDate.greaterThanOrEqual=" + DEFAULT_END_DATE,
            "endDate.greaterThanOrEqual=" + UPDATED_END_DATE
        );
    }

    @Test
    @Transactional
    void getAllEntitySubscriptionsByEndDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        // Get all the entitySubscriptionList where endDate is less than or equal to
        defaultEntitySubscriptionFiltering("endDate.lessThanOrEqual=" + DEFAULT_END_DATE, "endDate.lessThanOrEqual=" + SMALLER_END_DATE);
    }

    @Test
    @Transactional
    void getAllEntitySubscriptionsByEndDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        // Get all the entitySubscriptionList where endDate is less than
        defaultEntitySubscriptionFiltering("endDate.lessThan=" + UPDATED_END_DATE, "endDate.lessThan=" + DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    void getAllEntitySubscriptionsByEndDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        // Get all the entitySubscriptionList where endDate is greater than
        defaultEntitySubscriptionFiltering("endDate.greaterThan=" + SMALLER_END_DATE, "endDate.greaterThan=" + DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    void getAllEntitySubscriptionsByInstitutionIsEqualToSomething() throws Exception {
        Institution institution;
        if (TestUtil.findAll(em, Institution.class).isEmpty()) {
            entitySubscriptionRepository.saveAndFlush(entitySubscription);
            institution = InstitutionResourceIT.createEntity();
        } else {
            institution = TestUtil.findAll(em, Institution.class).get(0);
        }
        em.persist(institution);
        em.flush();
        entitySubscription.setInstitution(institution);
        entitySubscriptionRepository.saveAndFlush(entitySubscription);
        Long institutionId = institution.getId();
        // Get all the entitySubscriptionList where institution equals to institutionId
        defaultEntitySubscriptionShouldBeFound("institutionId.equals=" + institutionId);

        // Get all the entitySubscriptionList where institution equals to (institutionId + 1)
        defaultEntitySubscriptionShouldNotBeFound("institutionId.equals=" + (institutionId + 1));
    }

    private void defaultEntitySubscriptionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultEntitySubscriptionShouldBeFound(shouldBeFound);
        defaultEntitySubscriptionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEntitySubscriptionShouldBeFound(String filter) throws Exception {
        restEntitySubscriptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(entitySubscription.getId().intValue())))
            .andExpect(jsonPath("$.[*].subscriptionToken").value(hasItem(DEFAULT_SUBSCRIPTION_TOKEN.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(sameInstant(DEFAULT_START_DATE))))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(sameInstant(DEFAULT_END_DATE))));

        // Check, that the count call also returns 1
        restEntitySubscriptionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEntitySubscriptionShouldNotBeFound(String filter) throws Exception {
        restEntitySubscriptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEntitySubscriptionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEntitySubscription() throws Exception {
        // Get the entitySubscription
        restEntitySubscriptionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEntitySubscription() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        entitySubscriptionSearchRepository.save(entitySubscription);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());

        // Update the entitySubscription
        EntitySubscription updatedEntitySubscription = entitySubscriptionRepository.findById(entitySubscription.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEntitySubscription are not directly saved in db
        em.detach(updatedEntitySubscription);
        updatedEntitySubscription.subscriptionToken(UPDATED_SUBSCRIPTION_TOKEN).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(updatedEntitySubscription);

        restEntitySubscriptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, entitySubscriptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(entitySubscriptionDTO))
            )
            .andExpect(status().isOk());

        // Validate the EntitySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEntitySubscriptionToMatchAllProperties(updatedEntitySubscription);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<EntitySubscription> entitySubscriptionSearchList = Streamable.of(
                    entitySubscriptionSearchRepository.findAll()
                ).toList();
                EntitySubscription testEntitySubscriptionSearch = entitySubscriptionSearchList.get(searchDatabaseSizeAfter - 1);

                assertEntitySubscriptionAllPropertiesEquals(testEntitySubscriptionSearch, updatedEntitySubscription);
            });
    }

    @Test
    @Transactional
    void putNonExistingEntitySubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        entitySubscription.setId(longCount.incrementAndGet());

        // Create the EntitySubscription
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEntitySubscriptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, entitySubscriptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(entitySubscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EntitySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchEntitySubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        entitySubscription.setId(longCount.incrementAndGet());

        // Create the EntitySubscription
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntitySubscriptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(entitySubscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EntitySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEntitySubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        entitySubscription.setId(longCount.incrementAndGet());

        // Create the EntitySubscription
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntitySubscriptionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entitySubscriptionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EntitySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateEntitySubscriptionWithPatch() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the entitySubscription using partial update
        EntitySubscription partialUpdatedEntitySubscription = new EntitySubscription();
        partialUpdatedEntitySubscription.setId(entitySubscription.getId());

        partialUpdatedEntitySubscription.endDate(UPDATED_END_DATE);

        restEntitySubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEntitySubscription.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEntitySubscription))
            )
            .andExpect(status().isOk());

        // Validate the EntitySubscription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEntitySubscriptionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEntitySubscription, entitySubscription),
            getPersistedEntitySubscription(entitySubscription)
        );
    }

    @Test
    @Transactional
    void fullUpdateEntitySubscriptionWithPatch() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the entitySubscription using partial update
        EntitySubscription partialUpdatedEntitySubscription = new EntitySubscription();
        partialUpdatedEntitySubscription.setId(entitySubscription.getId());

        partialUpdatedEntitySubscription
            .subscriptionToken(UPDATED_SUBSCRIPTION_TOKEN)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);

        restEntitySubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEntitySubscription.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEntitySubscription))
            )
            .andExpect(status().isOk());

        // Validate the EntitySubscription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEntitySubscriptionUpdatableFieldsEquals(
            partialUpdatedEntitySubscription,
            getPersistedEntitySubscription(partialUpdatedEntitySubscription)
        );
    }

    @Test
    @Transactional
    void patchNonExistingEntitySubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        entitySubscription.setId(longCount.incrementAndGet());

        // Create the EntitySubscription
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEntitySubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, entitySubscriptionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(entitySubscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EntitySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEntitySubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        entitySubscription.setId(longCount.incrementAndGet());

        // Create the EntitySubscription
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntitySubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(entitySubscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EntitySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEntitySubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        entitySubscription.setId(longCount.incrementAndGet());

        // Create the EntitySubscription
        EntitySubscriptionDTO entitySubscriptionDTO = entitySubscriptionMapper.toDto(entitySubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntitySubscriptionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(entitySubscriptionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EntitySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteEntitySubscription() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);
        entitySubscriptionRepository.save(entitySubscription);
        entitySubscriptionSearchRepository.save(entitySubscription);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the entitySubscription
        restEntitySubscriptionMockMvc
            .perform(delete(ENTITY_API_URL_ID, entitySubscription.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entitySubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchEntitySubscription() throws Exception {
        // Initialize the database
        insertedEntitySubscription = entitySubscriptionRepository.saveAndFlush(entitySubscription);
        entitySubscriptionSearchRepository.save(entitySubscription);

        // Search the entitySubscription
        restEntitySubscriptionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + entitySubscription.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(entitySubscription.getId().intValue())))
            .andExpect(jsonPath("$.[*].subscriptionToken").value(hasItem(DEFAULT_SUBSCRIPTION_TOKEN.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(sameInstant(DEFAULT_START_DATE))))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(sameInstant(DEFAULT_END_DATE))));
    }

    protected long getRepositoryCount() {
        return entitySubscriptionRepository.count();
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
        return entitySubscriptionRepository.findById(entitySubscription.getId()).orElseThrow();
    }

    protected void assertPersistedEntitySubscriptionToMatchAllProperties(EntitySubscription expectedEntitySubscription) {
        assertEntitySubscriptionAllPropertiesEquals(expectedEntitySubscription, getPersistedEntitySubscription(expectedEntitySubscription));
    }

    protected void assertPersistedEntitySubscriptionToMatchUpdatableProperties(EntitySubscription expectedEntitySubscription) {
        assertEntitySubscriptionAllUpdatablePropertiesEquals(
            expectedEntitySubscription,
            getPersistedEntitySubscription(expectedEntitySubscription)
        );
    }
}
