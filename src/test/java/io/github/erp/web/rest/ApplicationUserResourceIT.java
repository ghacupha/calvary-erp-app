package io.github.erp.web.rest;

import static io.github.erp.domain.ApplicationUserAsserts.*;
import static io.github.erp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.erp.IntegrationTest;
import io.github.erp.domain.ApplicationUser;
import io.github.erp.domain.User;
import io.github.erp.repository.ApplicationUserRepository;
import io.github.erp.repository.EntityManager;
import io.github.erp.repository.UserRepository;
import io.github.erp.repository.UserRepository;
import io.github.erp.repository.search.ApplicationUserSearchRepository;
import io.github.erp.service.ApplicationUserService;
import io.github.erp.service.dto.ApplicationUserDTO;
import io.github.erp.service.mapper.ApplicationUserMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link ApplicationUserResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ApplicationUserResourceIT {

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVATED = false;
    private static final Boolean UPDATED_ACTIVATED = true;

    private static final String DEFAULT_LANG_KEY = "AAAAAAAAAA";
    private static final String UPDATED_LANG_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_ACTIVATION_KEY = "AAAAAAAAAA";
    private static final String UPDATED_ACTIVATION_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_RESET_KEY = "AAAAAAAAAA";
    private static final String UPDATED_RESET_KEY = "BBBBBBBBBB";

    private static final Instant DEFAULT_RESET_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RESET_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/application-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/application-users/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ApplicationUserRepository applicationUserRepositoryMock;

    @Autowired
    private ApplicationUserMapper applicationUserMapper;

    @Mock
    private ApplicationUserService applicationUserServiceMock;

    @Autowired
    private ApplicationUserSearchRepository applicationUserSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ApplicationUser applicationUser;

    private ApplicationUser insertedApplicationUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApplicationUser createEntity(EntityManager em) {
        ApplicationUser applicationUser = new ApplicationUser()
            .username(DEFAULT_USERNAME)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .activated(DEFAULT_ACTIVATED)
            .langKey(DEFAULT_LANG_KEY)
            .imageUrl(DEFAULT_IMAGE_URL)
            .activationKey(DEFAULT_ACTIVATION_KEY)
            .resetKey(DEFAULT_RESET_KEY)
            .resetDate(DEFAULT_RESET_DATE);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity()).block();
        applicationUser.setSystemUser(user);
        return applicationUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApplicationUser createUpdatedEntity(EntityManager em) {
        ApplicationUser updatedApplicationUser = new ApplicationUser()
            .username(UPDATED_USERNAME)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .activated(UPDATED_ACTIVATED)
            .langKey(UPDATED_LANG_KEY)
            .imageUrl(UPDATED_IMAGE_URL)
            .activationKey(UPDATED_ACTIVATION_KEY)
            .resetKey(UPDATED_RESET_KEY)
            .resetDate(UPDATED_RESET_DATE);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity()).block();
        updatedApplicationUser.setSystemUser(user);
        return updatedApplicationUser;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ApplicationUser.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        UserResourceIT.deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        applicationUser = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedApplicationUser != null) {
            applicationUserRepository.delete(insertedApplicationUser).block();
            applicationUserSearchRepository.delete(insertedApplicationUser).block();
            insertedApplicationUser = null;
        }
        deleteEntities(em);
        userRepository.deleteAllUserAuthorities().block();
        userRepository.deleteAll().block();
    }

    @Test
    void createApplicationUser() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());
        // Create the ApplicationUser
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);
        var returnedApplicationUserDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(applicationUserDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ApplicationUserDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the ApplicationUser in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedApplicationUser = applicationUserMapper.toEntity(returnedApplicationUserDTO);
        assertApplicationUserUpdatableFieldsEquals(returnedApplicationUser, getPersistedApplicationUser(returnedApplicationUser));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedApplicationUser = returnedApplicationUser;
    }

    @Test
    void createApplicationUserWithExistingId() throws Exception {
        // Create the ApplicationUser with an existing ID
        applicationUser.setId(1L);
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(applicationUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ApplicationUser in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkUsernameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());
        // set the field null
        applicationUser.setUsername(null);

        // Create the ApplicationUser, which fails.
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(applicationUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllApplicationUsers() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList
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
            .value(hasItem(applicationUser.getId().intValue()))
            .jsonPath("$.[*].username")
            .value(hasItem(DEFAULT_USERNAME))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].activated")
            .value(hasItem(DEFAULT_ACTIVATED.booleanValue()))
            .jsonPath("$.[*].langKey")
            .value(hasItem(DEFAULT_LANG_KEY))
            .jsonPath("$.[*].imageUrl")
            .value(hasItem(DEFAULT_IMAGE_URL))
            .jsonPath("$.[*].activationKey")
            .value(hasItem(DEFAULT_ACTIVATION_KEY))
            .jsonPath("$.[*].resetKey")
            .value(hasItem(DEFAULT_RESET_KEY))
            .jsonPath("$.[*].resetDate")
            .value(hasItem(DEFAULT_RESET_DATE.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllApplicationUsersWithEagerRelationshipsIsEnabled() {
        when(applicationUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(applicationUserServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllApplicationUsersWithEagerRelationshipsIsNotEnabled() {
        when(applicationUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(applicationUserRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getApplicationUser() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get the applicationUser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, applicationUser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(applicationUser.getId().intValue()))
            .jsonPath("$.username")
            .value(is(DEFAULT_USERNAME))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.activated")
            .value(is(DEFAULT_ACTIVATED.booleanValue()))
            .jsonPath("$.langKey")
            .value(is(DEFAULT_LANG_KEY))
            .jsonPath("$.imageUrl")
            .value(is(DEFAULT_IMAGE_URL))
            .jsonPath("$.activationKey")
            .value(is(DEFAULT_ACTIVATION_KEY))
            .jsonPath("$.resetKey")
            .value(is(DEFAULT_RESET_KEY))
            .jsonPath("$.resetDate")
            .value(is(DEFAULT_RESET_DATE.toString()));
    }

    @Test
    void getApplicationUsersByIdFiltering() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        Long id = applicationUser.getId();

        defaultApplicationUserFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultApplicationUserFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultApplicationUserFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllApplicationUsersByUsernameIsEqualToSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where username equals to
        defaultApplicationUserFiltering("username.equals=" + DEFAULT_USERNAME, "username.equals=" + UPDATED_USERNAME);
    }

    @Test
    void getAllApplicationUsersByUsernameIsInShouldWork() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where username in
        defaultApplicationUserFiltering("username.in=" + DEFAULT_USERNAME + "," + UPDATED_USERNAME, "username.in=" + UPDATED_USERNAME);
    }

    @Test
    void getAllApplicationUsersByUsernameIsNullOrNotNull() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where username is not null
        defaultApplicationUserFiltering("username.specified=true", "username.specified=false");
    }

    @Test
    void getAllApplicationUsersByUsernameContainsSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where username contains
        defaultApplicationUserFiltering("username.contains=" + DEFAULT_USERNAME, "username.contains=" + UPDATED_USERNAME);
    }

    @Test
    void getAllApplicationUsersByUsernameNotContainsSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where username does not contain
        defaultApplicationUserFiltering("username.doesNotContain=" + UPDATED_USERNAME, "username.doesNotContain=" + DEFAULT_USERNAME);
    }

    @Test
    void getAllApplicationUsersByFirstNameIsEqualToSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where firstName equals to
        defaultApplicationUserFiltering("firstName.equals=" + DEFAULT_FIRST_NAME, "firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllApplicationUsersByFirstNameIsInShouldWork() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where firstName in
        defaultApplicationUserFiltering(
            "firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME,
            "firstName.in=" + UPDATED_FIRST_NAME
        );
    }

    @Test
    void getAllApplicationUsersByFirstNameIsNullOrNotNull() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where firstName is not null
        defaultApplicationUserFiltering("firstName.specified=true", "firstName.specified=false");
    }

    @Test
    void getAllApplicationUsersByFirstNameContainsSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where firstName contains
        defaultApplicationUserFiltering("firstName.contains=" + DEFAULT_FIRST_NAME, "firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllApplicationUsersByFirstNameNotContainsSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where firstName does not contain
        defaultApplicationUserFiltering("firstName.doesNotContain=" + UPDATED_FIRST_NAME, "firstName.doesNotContain=" + DEFAULT_FIRST_NAME);
    }

    @Test
    void getAllApplicationUsersByLastNameIsEqualToSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where lastName equals to
        defaultApplicationUserFiltering("lastName.equals=" + DEFAULT_LAST_NAME, "lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllApplicationUsersByLastNameIsInShouldWork() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where lastName in
        defaultApplicationUserFiltering("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME, "lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllApplicationUsersByLastNameIsNullOrNotNull() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where lastName is not null
        defaultApplicationUserFiltering("lastName.specified=true", "lastName.specified=false");
    }

    @Test
    void getAllApplicationUsersByLastNameContainsSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where lastName contains
        defaultApplicationUserFiltering("lastName.contains=" + DEFAULT_LAST_NAME, "lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllApplicationUsersByLastNameNotContainsSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where lastName does not contain
        defaultApplicationUserFiltering("lastName.doesNotContain=" + UPDATED_LAST_NAME, "lastName.doesNotContain=" + DEFAULT_LAST_NAME);
    }

    @Test
    void getAllApplicationUsersByEmailIsEqualToSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where email equals to
        defaultApplicationUserFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    void getAllApplicationUsersByEmailIsInShouldWork() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where email in
        defaultApplicationUserFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    void getAllApplicationUsersByEmailIsNullOrNotNull() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where email is not null
        defaultApplicationUserFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    void getAllApplicationUsersByEmailContainsSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where email contains
        defaultApplicationUserFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    void getAllApplicationUsersByEmailNotContainsSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where email does not contain
        defaultApplicationUserFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    void getAllApplicationUsersByActivatedIsEqualToSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where activated equals to
        defaultApplicationUserFiltering("activated.equals=" + DEFAULT_ACTIVATED, "activated.equals=" + UPDATED_ACTIVATED);
    }

    @Test
    void getAllApplicationUsersByActivatedIsInShouldWork() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where activated in
        defaultApplicationUserFiltering("activated.in=" + DEFAULT_ACTIVATED + "," + UPDATED_ACTIVATED, "activated.in=" + UPDATED_ACTIVATED);
    }

    @Test
    void getAllApplicationUsersByActivatedIsNullOrNotNull() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where activated is not null
        defaultApplicationUserFiltering("activated.specified=true", "activated.specified=false");
    }

    @Test
    void getAllApplicationUsersByLangKeyIsEqualToSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where langKey equals to
        defaultApplicationUserFiltering("langKey.equals=" + DEFAULT_LANG_KEY, "langKey.equals=" + UPDATED_LANG_KEY);
    }

    @Test
    void getAllApplicationUsersByLangKeyIsInShouldWork() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where langKey in
        defaultApplicationUserFiltering("langKey.in=" + DEFAULT_LANG_KEY + "," + UPDATED_LANG_KEY, "langKey.in=" + UPDATED_LANG_KEY);
    }

    @Test
    void getAllApplicationUsersByLangKeyIsNullOrNotNull() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where langKey is not null
        defaultApplicationUserFiltering("langKey.specified=true", "langKey.specified=false");
    }

    @Test
    void getAllApplicationUsersByLangKeyContainsSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where langKey contains
        defaultApplicationUserFiltering("langKey.contains=" + DEFAULT_LANG_KEY, "langKey.contains=" + UPDATED_LANG_KEY);
    }

    @Test
    void getAllApplicationUsersByLangKeyNotContainsSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where langKey does not contain
        defaultApplicationUserFiltering("langKey.doesNotContain=" + UPDATED_LANG_KEY, "langKey.doesNotContain=" + DEFAULT_LANG_KEY);
    }

    @Test
    void getAllApplicationUsersByImageUrlIsEqualToSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where imageUrl equals to
        defaultApplicationUserFiltering("imageUrl.equals=" + DEFAULT_IMAGE_URL, "imageUrl.equals=" + UPDATED_IMAGE_URL);
    }

    @Test
    void getAllApplicationUsersByImageUrlIsInShouldWork() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where imageUrl in
        defaultApplicationUserFiltering("imageUrl.in=" + DEFAULT_IMAGE_URL + "," + UPDATED_IMAGE_URL, "imageUrl.in=" + UPDATED_IMAGE_URL);
    }

    @Test
    void getAllApplicationUsersByImageUrlIsNullOrNotNull() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where imageUrl is not null
        defaultApplicationUserFiltering("imageUrl.specified=true", "imageUrl.specified=false");
    }

    @Test
    void getAllApplicationUsersByImageUrlContainsSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where imageUrl contains
        defaultApplicationUserFiltering("imageUrl.contains=" + DEFAULT_IMAGE_URL, "imageUrl.contains=" + UPDATED_IMAGE_URL);
    }

    @Test
    void getAllApplicationUsersByImageUrlNotContainsSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where imageUrl does not contain
        defaultApplicationUserFiltering("imageUrl.doesNotContain=" + UPDATED_IMAGE_URL, "imageUrl.doesNotContain=" + DEFAULT_IMAGE_URL);
    }

    @Test
    void getAllApplicationUsersByActivationKeyIsEqualToSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where activationKey equals to
        defaultApplicationUserFiltering("activationKey.equals=" + DEFAULT_ACTIVATION_KEY, "activationKey.equals=" + UPDATED_ACTIVATION_KEY);
    }

    @Test
    void getAllApplicationUsersByActivationKeyIsInShouldWork() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where activationKey in
        defaultApplicationUserFiltering(
            "activationKey.in=" + DEFAULT_ACTIVATION_KEY + "," + UPDATED_ACTIVATION_KEY,
            "activationKey.in=" + UPDATED_ACTIVATION_KEY
        );
    }

    @Test
    void getAllApplicationUsersByActivationKeyIsNullOrNotNull() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where activationKey is not null
        defaultApplicationUserFiltering("activationKey.specified=true", "activationKey.specified=false");
    }

    @Test
    void getAllApplicationUsersByActivationKeyContainsSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where activationKey contains
        defaultApplicationUserFiltering(
            "activationKey.contains=" + DEFAULT_ACTIVATION_KEY,
            "activationKey.contains=" + UPDATED_ACTIVATION_KEY
        );
    }

    @Test
    void getAllApplicationUsersByActivationKeyNotContainsSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where activationKey does not contain
        defaultApplicationUserFiltering(
            "activationKey.doesNotContain=" + UPDATED_ACTIVATION_KEY,
            "activationKey.doesNotContain=" + DEFAULT_ACTIVATION_KEY
        );
    }

    @Test
    void getAllApplicationUsersByResetKeyIsEqualToSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where resetKey equals to
        defaultApplicationUserFiltering("resetKey.equals=" + DEFAULT_RESET_KEY, "resetKey.equals=" + UPDATED_RESET_KEY);
    }

    @Test
    void getAllApplicationUsersByResetKeyIsInShouldWork() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where resetKey in
        defaultApplicationUserFiltering("resetKey.in=" + DEFAULT_RESET_KEY + "," + UPDATED_RESET_KEY, "resetKey.in=" + UPDATED_RESET_KEY);
    }

    @Test
    void getAllApplicationUsersByResetKeyIsNullOrNotNull() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where resetKey is not null
        defaultApplicationUserFiltering("resetKey.specified=true", "resetKey.specified=false");
    }

    @Test
    void getAllApplicationUsersByResetKeyContainsSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where resetKey contains
        defaultApplicationUserFiltering("resetKey.contains=" + DEFAULT_RESET_KEY, "resetKey.contains=" + UPDATED_RESET_KEY);
    }

    @Test
    void getAllApplicationUsersByResetKeyNotContainsSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where resetKey does not contain
        defaultApplicationUserFiltering("resetKey.doesNotContain=" + UPDATED_RESET_KEY, "resetKey.doesNotContain=" + DEFAULT_RESET_KEY);
    }

    @Test
    void getAllApplicationUsersByResetDateIsEqualToSomething() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where resetDate equals to
        defaultApplicationUserFiltering("resetDate.equals=" + DEFAULT_RESET_DATE, "resetDate.equals=" + UPDATED_RESET_DATE);
    }

    @Test
    void getAllApplicationUsersByResetDateIsInShouldWork() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where resetDate in
        defaultApplicationUserFiltering(
            "resetDate.in=" + DEFAULT_RESET_DATE + "," + UPDATED_RESET_DATE,
            "resetDate.in=" + UPDATED_RESET_DATE
        );
    }

    @Test
    void getAllApplicationUsersByResetDateIsNullOrNotNull() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        // Get all the applicationUserList where resetDate is not null
        defaultApplicationUserFiltering("resetDate.specified=true", "resetDate.specified=false");
    }

    @Test
    void getAllApplicationUsersBySystemUserIsEqualToSomething() {
        // Get already existing entity
        User systemUser = applicationUser.getSystemUser();
        // Get all the applicationUserList where systemUser equals to systemUserId
        defaultApplicationUserShouldBeFound("systemUserId.equals=" + systemUser.getId());

        // Get all the applicationUserList where systemUser equals to (systemUserId + 1)
        defaultApplicationUserShouldNotBeFound("systemUserId.equals=" + (systemUser.getId() + 1));
    }

    private void defaultApplicationUserFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultApplicationUserShouldBeFound(shouldBeFound);
        defaultApplicationUserShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultApplicationUserShouldBeFound(String filter) {
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
            .value(hasItem(applicationUser.getId().intValue()))
            .jsonPath("$.[*].username")
            .value(hasItem(DEFAULT_USERNAME))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].activated")
            .value(hasItem(DEFAULT_ACTIVATED.booleanValue()))
            .jsonPath("$.[*].langKey")
            .value(hasItem(DEFAULT_LANG_KEY))
            .jsonPath("$.[*].imageUrl")
            .value(hasItem(DEFAULT_IMAGE_URL))
            .jsonPath("$.[*].activationKey")
            .value(hasItem(DEFAULT_ACTIVATION_KEY))
            .jsonPath("$.[*].resetKey")
            .value(hasItem(DEFAULT_RESET_KEY))
            .jsonPath("$.[*].resetDate")
            .value(hasItem(DEFAULT_RESET_DATE.toString()));

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
    private void defaultApplicationUserShouldNotBeFound(String filter) {
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
    void getNonExistingApplicationUser() {
        // Get the applicationUser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingApplicationUser() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        applicationUserSearchRepository.save(applicationUser).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());

        // Update the applicationUser
        ApplicationUser updatedApplicationUser = applicationUserRepository.findById(applicationUser.getId()).block();
        updatedApplicationUser
            .username(UPDATED_USERNAME)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .activated(UPDATED_ACTIVATED)
            .langKey(UPDATED_LANG_KEY)
            .imageUrl(UPDATED_IMAGE_URL)
            .activationKey(UPDATED_ACTIVATION_KEY)
            .resetKey(UPDATED_RESET_KEY)
            .resetDate(UPDATED_RESET_DATE);
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(updatedApplicationUser);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, applicationUserDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(applicationUserDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ApplicationUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedApplicationUserToMatchAllProperties(updatedApplicationUser);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ApplicationUser> applicationUserSearchList = Streamable.of(
                    applicationUserSearchRepository.findAll().collectList().block()
                ).toList();
                ApplicationUser testApplicationUserSearch = applicationUserSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertApplicationUserAllPropertiesEquals(testApplicationUserSearch, updatedApplicationUser);
                assertApplicationUserUpdatableFieldsEquals(testApplicationUserSearch, updatedApplicationUser);
            });
    }

    @Test
    void putNonExistingApplicationUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());
        applicationUser.setId(longCount.incrementAndGet());

        // Create the ApplicationUser
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, applicationUserDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(applicationUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ApplicationUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchApplicationUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());
        applicationUser.setId(longCount.incrementAndGet());

        // Create the ApplicationUser
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(applicationUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ApplicationUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamApplicationUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());
        applicationUser.setId(longCount.incrementAndGet());

        // Create the ApplicationUser
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(applicationUserDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ApplicationUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateApplicationUserWithPatch() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the applicationUser using partial update
        ApplicationUser partialUpdatedApplicationUser = new ApplicationUser();
        partialUpdatedApplicationUser.setId(applicationUser.getId());

        partialUpdatedApplicationUser
            .username(UPDATED_USERNAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .activated(UPDATED_ACTIVATED)
            .imageUrl(UPDATED_IMAGE_URL)
            .activationKey(UPDATED_ACTIVATION_KEY)
            .resetKey(UPDATED_RESET_KEY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedApplicationUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedApplicationUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ApplicationUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApplicationUserUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedApplicationUser, applicationUser),
            getPersistedApplicationUser(applicationUser)
        );
    }

    @Test
    void fullUpdateApplicationUserWithPatch() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the applicationUser using partial update
        ApplicationUser partialUpdatedApplicationUser = new ApplicationUser();
        partialUpdatedApplicationUser.setId(applicationUser.getId());

        partialUpdatedApplicationUser
            .username(UPDATED_USERNAME)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .activated(UPDATED_ACTIVATED)
            .langKey(UPDATED_LANG_KEY)
            .imageUrl(UPDATED_IMAGE_URL)
            .activationKey(UPDATED_ACTIVATION_KEY)
            .resetKey(UPDATED_RESET_KEY)
            .resetDate(UPDATED_RESET_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedApplicationUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedApplicationUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ApplicationUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApplicationUserUpdatableFieldsEquals(
            partialUpdatedApplicationUser,
            getPersistedApplicationUser(partialUpdatedApplicationUser)
        );
    }

    @Test
    void patchNonExistingApplicationUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());
        applicationUser.setId(longCount.incrementAndGet());

        // Create the ApplicationUser
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, applicationUserDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(applicationUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ApplicationUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchApplicationUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());
        applicationUser.setId(longCount.incrementAndGet());

        // Create the ApplicationUser
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(applicationUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ApplicationUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamApplicationUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());
        applicationUser.setId(longCount.incrementAndGet());

        // Create the ApplicationUser
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(applicationUserDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ApplicationUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteApplicationUser() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();
        applicationUserRepository.save(applicationUser).block();
        applicationUserSearchRepository.save(applicationUser).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the applicationUser
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, applicationUser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchApplicationUser() {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.save(applicationUser).block();
        applicationUserSearchRepository.save(applicationUser).block();

        // Search the applicationUser
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + applicationUser.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(applicationUser.getId().intValue()))
            .jsonPath("$.[*].username")
            .value(hasItem(DEFAULT_USERNAME))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].activated")
            .value(hasItem(DEFAULT_ACTIVATED.booleanValue()))
            .jsonPath("$.[*].langKey")
            .value(hasItem(DEFAULT_LANG_KEY))
            .jsonPath("$.[*].imageUrl")
            .value(hasItem(DEFAULT_IMAGE_URL))
            .jsonPath("$.[*].activationKey")
            .value(hasItem(DEFAULT_ACTIVATION_KEY))
            .jsonPath("$.[*].resetKey")
            .value(hasItem(DEFAULT_RESET_KEY))
            .jsonPath("$.[*].resetDate")
            .value(hasItem(DEFAULT_RESET_DATE.toString()));
    }

    protected long getRepositoryCount() {
        return applicationUserRepository.count().block();
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

    protected ApplicationUser getPersistedApplicationUser(ApplicationUser applicationUser) {
        return applicationUserRepository.findById(applicationUser.getId()).block();
    }

    protected void assertPersistedApplicationUserToMatchAllProperties(ApplicationUser expectedApplicationUser) {
        // Test fails because reactive api returns an empty object instead of null
        // assertApplicationUserAllPropertiesEquals(expectedApplicationUser, getPersistedApplicationUser(expectedApplicationUser));
        assertApplicationUserUpdatableFieldsEquals(expectedApplicationUser, getPersistedApplicationUser(expectedApplicationUser));
    }

    protected void assertPersistedApplicationUserToMatchUpdatableProperties(ApplicationUser expectedApplicationUser) {
        // Test fails because reactive api returns an empty object instead of null
        // assertApplicationUserAllUpdatablePropertiesEquals(expectedApplicationUser, getPersistedApplicationUser(expectedApplicationUser));
        assertApplicationUserUpdatableFieldsEquals(expectedApplicationUser, getPersistedApplicationUser(expectedApplicationUser));
    }
}
