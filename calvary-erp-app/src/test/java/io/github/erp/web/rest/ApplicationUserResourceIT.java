package io.github.erp.web.rest;

import static io.github.erp.domain.ApplicationUserAsserts.*;
import static io.github.erp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.erp.IntegrationTest;
import io.github.erp.domain.ApplicationUser;
import io.github.erp.domain.Institution;
import io.github.erp.domain.User;
import io.github.erp.repository.ApplicationUserRepository;
import io.github.erp.repository.UserRepository;
import io.github.erp.repository.search.ApplicationUserSearchRepository;
import io.github.erp.service.ApplicationUserService;
import io.github.erp.service.dto.ApplicationUserDTO;
import io.github.erp.service.mapper.ApplicationUserMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ApplicationUserResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
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
    private MockMvc restApplicationUserMockMvc;

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
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        applicationUser.setSystemUser(user);
        // Add required entity
        Institution institution;
        if (TestUtil.findAll(em, Institution.class).isEmpty()) {
            institution = InstitutionResourceIT.createEntity();
            em.persist(institution);
            em.flush();
        } else {
            institution = TestUtil.findAll(em, Institution.class).get(0);
        }
        applicationUser.setInstitution(institution);
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
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedApplicationUser.setSystemUser(user);
        // Add required entity
        Institution institution;
        if (TestUtil.findAll(em, Institution.class).isEmpty()) {
            institution = InstitutionResourceIT.createUpdatedEntity();
            em.persist(institution);
            em.flush();
        } else {
            institution = TestUtil.findAll(em, Institution.class).get(0);
        }
        updatedApplicationUser.setInstitution(institution);
        return updatedApplicationUser;
    }

    @BeforeEach
    public void initTest() {
        applicationUser = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedApplicationUser != null) {
            applicationUserRepository.delete(insertedApplicationUser);
            applicationUserSearchRepository.delete(insertedApplicationUser);
            insertedApplicationUser = null;
        }
    }

    @Test
    @Transactional
    void createApplicationUser() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        // Create the ApplicationUser
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);
        var returnedApplicationUserDTO = om.readValue(
            restApplicationUserMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicationUserDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ApplicationUserDTO.class
        );

        // Validate the ApplicationUser in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedApplicationUser = applicationUserMapper.toEntity(returnedApplicationUserDTO);
        assertApplicationUserUpdatableFieldsEquals(returnedApplicationUser, getPersistedApplicationUser(returnedApplicationUser));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedApplicationUser = returnedApplicationUser;
    }

    @Test
    @Transactional
    void createApplicationUserWithExistingId() throws Exception {
        // Create the ApplicationUser with an existing ID
        applicationUser.setId(1L);
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restApplicationUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicationUserDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ApplicationUser in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkUsernameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        // set the field null
        applicationUser.setUsername(null);

        // Create the ApplicationUser, which fails.
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        restApplicationUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicationUserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllApplicationUsers() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList
        restApplicationUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(applicationUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].activated").value(hasItem(DEFAULT_ACTIVATED.booleanValue())))
            .andExpect(jsonPath("$.[*].langKey").value(hasItem(DEFAULT_LANG_KEY)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].activationKey").value(hasItem(DEFAULT_ACTIVATION_KEY)))
            .andExpect(jsonPath("$.[*].resetKey").value(hasItem(DEFAULT_RESET_KEY)))
            .andExpect(jsonPath("$.[*].resetDate").value(hasItem(DEFAULT_RESET_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllApplicationUsersWithEagerRelationshipsIsEnabled() throws Exception {
        when(applicationUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restApplicationUserMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(applicationUserServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllApplicationUsersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(applicationUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restApplicationUserMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(applicationUserRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getApplicationUser() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get the applicationUser
        restApplicationUserMockMvc
            .perform(get(ENTITY_API_URL_ID, applicationUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(applicationUser.getId().intValue()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.activated").value(DEFAULT_ACTIVATED.booleanValue()))
            .andExpect(jsonPath("$.langKey").value(DEFAULT_LANG_KEY))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL))
            .andExpect(jsonPath("$.activationKey").value(DEFAULT_ACTIVATION_KEY))
            .andExpect(jsonPath("$.resetKey").value(DEFAULT_RESET_KEY))
            .andExpect(jsonPath("$.resetDate").value(DEFAULT_RESET_DATE.toString()));
    }

    @Test
    @Transactional
    void getApplicationUsersByIdFiltering() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        Long id = applicationUser.getId();

        defaultApplicationUserFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultApplicationUserFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultApplicationUserFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByUsernameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where username equals to
        defaultApplicationUserFiltering("username.equals=" + DEFAULT_USERNAME, "username.equals=" + UPDATED_USERNAME);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByUsernameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where username in
        defaultApplicationUserFiltering("username.in=" + DEFAULT_USERNAME + "," + UPDATED_USERNAME, "username.in=" + UPDATED_USERNAME);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByUsernameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where username is not null
        defaultApplicationUserFiltering("username.specified=true", "username.specified=false");
    }

    @Test
    @Transactional
    void getAllApplicationUsersByUsernameContainsSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where username contains
        defaultApplicationUserFiltering("username.contains=" + DEFAULT_USERNAME, "username.contains=" + UPDATED_USERNAME);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByUsernameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where username does not contain
        defaultApplicationUserFiltering("username.doesNotContain=" + UPDATED_USERNAME, "username.doesNotContain=" + DEFAULT_USERNAME);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where firstName equals to
        defaultApplicationUserFiltering("firstName.equals=" + DEFAULT_FIRST_NAME, "firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where firstName in
        defaultApplicationUserFiltering(
            "firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME,
            "firstName.in=" + UPDATED_FIRST_NAME
        );
    }

    @Test
    @Transactional
    void getAllApplicationUsersByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where firstName is not null
        defaultApplicationUserFiltering("firstName.specified=true", "firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllApplicationUsersByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where firstName contains
        defaultApplicationUserFiltering("firstName.contains=" + DEFAULT_FIRST_NAME, "firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where firstName does not contain
        defaultApplicationUserFiltering("firstName.doesNotContain=" + UPDATED_FIRST_NAME, "firstName.doesNotContain=" + DEFAULT_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where lastName equals to
        defaultApplicationUserFiltering("lastName.equals=" + DEFAULT_LAST_NAME, "lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where lastName in
        defaultApplicationUserFiltering("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME, "lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where lastName is not null
        defaultApplicationUserFiltering("lastName.specified=true", "lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLastNameContainsSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where lastName contains
        defaultApplicationUserFiltering("lastName.contains=" + DEFAULT_LAST_NAME, "lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where lastName does not contain
        defaultApplicationUserFiltering("lastName.doesNotContain=" + UPDATED_LAST_NAME, "lastName.doesNotContain=" + DEFAULT_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where email equals to
        defaultApplicationUserFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where email in
        defaultApplicationUserFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where email is not null
        defaultApplicationUserFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    @Transactional
    void getAllApplicationUsersByEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where email contains
        defaultApplicationUserFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where email does not contain
        defaultApplicationUserFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByActivatedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where activated equals to
        defaultApplicationUserFiltering("activated.equals=" + DEFAULT_ACTIVATED, "activated.equals=" + UPDATED_ACTIVATED);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByActivatedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where activated in
        defaultApplicationUserFiltering("activated.in=" + DEFAULT_ACTIVATED + "," + UPDATED_ACTIVATED, "activated.in=" + UPDATED_ACTIVATED);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByActivatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where activated is not null
        defaultApplicationUserFiltering("activated.specified=true", "activated.specified=false");
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLangKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where langKey equals to
        defaultApplicationUserFiltering("langKey.equals=" + DEFAULT_LANG_KEY, "langKey.equals=" + UPDATED_LANG_KEY);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLangKeyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where langKey in
        defaultApplicationUserFiltering("langKey.in=" + DEFAULT_LANG_KEY + "," + UPDATED_LANG_KEY, "langKey.in=" + UPDATED_LANG_KEY);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLangKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where langKey is not null
        defaultApplicationUserFiltering("langKey.specified=true", "langKey.specified=false");
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLangKeyContainsSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where langKey contains
        defaultApplicationUserFiltering("langKey.contains=" + DEFAULT_LANG_KEY, "langKey.contains=" + UPDATED_LANG_KEY);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByLangKeyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where langKey does not contain
        defaultApplicationUserFiltering("langKey.doesNotContain=" + UPDATED_LANG_KEY, "langKey.doesNotContain=" + DEFAULT_LANG_KEY);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where imageUrl equals to
        defaultApplicationUserFiltering("imageUrl.equals=" + DEFAULT_IMAGE_URL, "imageUrl.equals=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where imageUrl in
        defaultApplicationUserFiltering("imageUrl.in=" + DEFAULT_IMAGE_URL + "," + UPDATED_IMAGE_URL, "imageUrl.in=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where imageUrl is not null
        defaultApplicationUserFiltering("imageUrl.specified=true", "imageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllApplicationUsersByImageUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where imageUrl contains
        defaultApplicationUserFiltering("imageUrl.contains=" + DEFAULT_IMAGE_URL, "imageUrl.contains=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where imageUrl does not contain
        defaultApplicationUserFiltering("imageUrl.doesNotContain=" + UPDATED_IMAGE_URL, "imageUrl.doesNotContain=" + DEFAULT_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByActivationKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where activationKey equals to
        defaultApplicationUserFiltering("activationKey.equals=" + DEFAULT_ACTIVATION_KEY, "activationKey.equals=" + UPDATED_ACTIVATION_KEY);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByActivationKeyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where activationKey in
        defaultApplicationUserFiltering(
            "activationKey.in=" + DEFAULT_ACTIVATION_KEY + "," + UPDATED_ACTIVATION_KEY,
            "activationKey.in=" + UPDATED_ACTIVATION_KEY
        );
    }

    @Test
    @Transactional
    void getAllApplicationUsersByActivationKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where activationKey is not null
        defaultApplicationUserFiltering("activationKey.specified=true", "activationKey.specified=false");
    }

    @Test
    @Transactional
    void getAllApplicationUsersByActivationKeyContainsSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where activationKey contains
        defaultApplicationUserFiltering(
            "activationKey.contains=" + DEFAULT_ACTIVATION_KEY,
            "activationKey.contains=" + UPDATED_ACTIVATION_KEY
        );
    }

    @Test
    @Transactional
    void getAllApplicationUsersByActivationKeyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where activationKey does not contain
        defaultApplicationUserFiltering(
            "activationKey.doesNotContain=" + UPDATED_ACTIVATION_KEY,
            "activationKey.doesNotContain=" + DEFAULT_ACTIVATION_KEY
        );
    }

    @Test
    @Transactional
    void getAllApplicationUsersByResetKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where resetKey equals to
        defaultApplicationUserFiltering("resetKey.equals=" + DEFAULT_RESET_KEY, "resetKey.equals=" + UPDATED_RESET_KEY);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByResetKeyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where resetKey in
        defaultApplicationUserFiltering("resetKey.in=" + DEFAULT_RESET_KEY + "," + UPDATED_RESET_KEY, "resetKey.in=" + UPDATED_RESET_KEY);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByResetKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where resetKey is not null
        defaultApplicationUserFiltering("resetKey.specified=true", "resetKey.specified=false");
    }

    @Test
    @Transactional
    void getAllApplicationUsersByResetKeyContainsSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where resetKey contains
        defaultApplicationUserFiltering("resetKey.contains=" + DEFAULT_RESET_KEY, "resetKey.contains=" + UPDATED_RESET_KEY);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByResetKeyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where resetKey does not contain
        defaultApplicationUserFiltering("resetKey.doesNotContain=" + UPDATED_RESET_KEY, "resetKey.doesNotContain=" + DEFAULT_RESET_KEY);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByResetDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where resetDate equals to
        defaultApplicationUserFiltering("resetDate.equals=" + DEFAULT_RESET_DATE, "resetDate.equals=" + UPDATED_RESET_DATE);
    }

    @Test
    @Transactional
    void getAllApplicationUsersByResetDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where resetDate in
        defaultApplicationUserFiltering(
            "resetDate.in=" + DEFAULT_RESET_DATE + "," + UPDATED_RESET_DATE,
            "resetDate.in=" + UPDATED_RESET_DATE
        );
    }

    @Test
    @Transactional
    void getAllApplicationUsersByResetDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        // Get all the applicationUserList where resetDate is not null
        defaultApplicationUserFiltering("resetDate.specified=true", "resetDate.specified=false");
    }

    @Test
    @Transactional
    void getAllApplicationUsersBySystemUserIsEqualToSomething() throws Exception {
        // Get already existing entity
        User systemUser = applicationUser.getSystemUser();
        applicationUserRepository.saveAndFlush(applicationUser);
        Long systemUserId = systemUser.getId();
        // Get all the applicationUserList where systemUser equals to systemUserId
        defaultApplicationUserShouldBeFound("systemUserId.equals=" + systemUserId);

        // Get all the applicationUserList where systemUser equals to (systemUserId + 1)
        defaultApplicationUserShouldNotBeFound("systemUserId.equals=" + (systemUserId + 1));
    }

    @Test
    @Transactional
    void getAllApplicationUsersByInstitutionIsEqualToSomething() throws Exception {
        Institution institution;
        if (TestUtil.findAll(em, Institution.class).isEmpty()) {
            applicationUserRepository.saveAndFlush(applicationUser);
            institution = InstitutionResourceIT.createEntity();
        } else {
            institution = TestUtil.findAll(em, Institution.class).get(0);
        }
        em.persist(institution);
        em.flush();
        applicationUser.setInstitution(institution);
        applicationUserRepository.saveAndFlush(applicationUser);
        Long institutionId = institution.getId();
        // Get all the applicationUserList where institution equals to institutionId
        defaultApplicationUserShouldBeFound("institutionId.equals=" + institutionId);

        // Get all the applicationUserList where institution equals to (institutionId + 1)
        defaultApplicationUserShouldNotBeFound("institutionId.equals=" + (institutionId + 1));
    }

    private void defaultApplicationUserFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultApplicationUserShouldBeFound(shouldBeFound);
        defaultApplicationUserShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultApplicationUserShouldBeFound(String filter) throws Exception {
        restApplicationUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(applicationUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].activated").value(hasItem(DEFAULT_ACTIVATED.booleanValue())))
            .andExpect(jsonPath("$.[*].langKey").value(hasItem(DEFAULT_LANG_KEY)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].activationKey").value(hasItem(DEFAULT_ACTIVATION_KEY)))
            .andExpect(jsonPath("$.[*].resetKey").value(hasItem(DEFAULT_RESET_KEY)))
            .andExpect(jsonPath("$.[*].resetDate").value(hasItem(DEFAULT_RESET_DATE.toString())));

        // Check, that the count call also returns 1
        restApplicationUserMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultApplicationUserShouldNotBeFound(String filter) throws Exception {
        restApplicationUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restApplicationUserMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingApplicationUser() throws Exception {
        // Get the applicationUser
        restApplicationUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingApplicationUser() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        applicationUserSearchRepository.save(applicationUser);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());

        // Update the applicationUser
        ApplicationUser updatedApplicationUser = applicationUserRepository.findById(applicationUser.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedApplicationUser are not directly saved in db
        em.detach(updatedApplicationUser);
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

        restApplicationUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, applicationUserDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(applicationUserDTO))
            )
            .andExpect(status().isOk());

        // Validate the ApplicationUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedApplicationUserToMatchAllProperties(updatedApplicationUser);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ApplicationUser> applicationUserSearchList = Streamable.of(applicationUserSearchRepository.findAll()).toList();
                ApplicationUser testApplicationUserSearch = applicationUserSearchList.get(searchDatabaseSizeAfter - 1);

                assertApplicationUserAllPropertiesEquals(testApplicationUserSearch, updatedApplicationUser);
            });
    }

    @Test
    @Transactional
    void putNonExistingApplicationUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        applicationUser.setId(longCount.incrementAndGet());

        // Create the ApplicationUser
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApplicationUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, applicationUserDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(applicationUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchApplicationUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        applicationUser.setId(longCount.incrementAndGet());

        // Create the ApplicationUser
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(applicationUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamApplicationUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        applicationUser.setId(longCount.incrementAndGet());

        // Create the ApplicationUser
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationUserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicationUserDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApplicationUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateApplicationUserWithPatch() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the applicationUser using partial update
        ApplicationUser partialUpdatedApplicationUser = new ApplicationUser();
        partialUpdatedApplicationUser.setId(applicationUser.getId());

        partialUpdatedApplicationUser
            .username(UPDATED_USERNAME)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .langKey(UPDATED_LANG_KEY)
            .imageUrl(UPDATED_IMAGE_URL)
            .resetDate(UPDATED_RESET_DATE);

        restApplicationUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApplicationUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedApplicationUser))
            )
            .andExpect(status().isOk());

        // Validate the ApplicationUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApplicationUserUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedApplicationUser, applicationUser),
            getPersistedApplicationUser(applicationUser)
        );
    }

    @Test
    @Transactional
    void fullUpdateApplicationUserWithPatch() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);

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

        restApplicationUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApplicationUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedApplicationUser))
            )
            .andExpect(status().isOk());

        // Validate the ApplicationUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApplicationUserUpdatableFieldsEquals(
            partialUpdatedApplicationUser,
            getPersistedApplicationUser(partialUpdatedApplicationUser)
        );
    }

    @Test
    @Transactional
    void patchNonExistingApplicationUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        applicationUser.setId(longCount.incrementAndGet());

        // Create the ApplicationUser
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApplicationUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, applicationUserDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(applicationUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchApplicationUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        applicationUser.setId(longCount.incrementAndGet());

        // Create the ApplicationUser
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(applicationUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamApplicationUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        applicationUser.setId(longCount.incrementAndGet());

        // Create the ApplicationUser
        ApplicationUserDTO applicationUserDTO = applicationUserMapper.toDto(applicationUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationUserMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(applicationUserDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApplicationUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteApplicationUser() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);
        applicationUserRepository.save(applicationUser);
        applicationUserSearchRepository.save(applicationUser);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the applicationUser
        restApplicationUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, applicationUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(applicationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchApplicationUser() throws Exception {
        // Initialize the database
        insertedApplicationUser = applicationUserRepository.saveAndFlush(applicationUser);
        applicationUserSearchRepository.save(applicationUser);

        // Search the applicationUser
        restApplicationUserMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + applicationUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(applicationUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].activated").value(hasItem(DEFAULT_ACTIVATED.booleanValue())))
            .andExpect(jsonPath("$.[*].langKey").value(hasItem(DEFAULT_LANG_KEY)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].activationKey").value(hasItem(DEFAULT_ACTIVATION_KEY)))
            .andExpect(jsonPath("$.[*].resetKey").value(hasItem(DEFAULT_RESET_KEY)))
            .andExpect(jsonPath("$.[*].resetDate").value(hasItem(DEFAULT_RESET_DATE.toString())));
    }

    protected long getRepositoryCount() {
        return applicationUserRepository.count();
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
        return applicationUserRepository.findById(applicationUser.getId()).orElseThrow();
    }

    protected void assertPersistedApplicationUserToMatchAllProperties(ApplicationUser expectedApplicationUser) {
        assertApplicationUserAllPropertiesEquals(expectedApplicationUser, getPersistedApplicationUser(expectedApplicationUser));
    }

    protected void assertPersistedApplicationUserToMatchUpdatableProperties(ApplicationUser expectedApplicationUser) {
        assertApplicationUserAllUpdatablePropertiesEquals(expectedApplicationUser, getPersistedApplicationUser(expectedApplicationUser));
    }
}
