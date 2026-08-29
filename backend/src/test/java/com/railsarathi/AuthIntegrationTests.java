package com.railsarathi;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.railsarathi.dto.LoginRequest;
import com.railsarathi.dto.RegisterRequest;
import com.railsarathi.repository.UserRepository;

@SpringBootTest
public class AuthIntegrationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .fullName("Sourav Ganguly")
                .username("sourav")
                .email("sourav@kolkata.com")
                .password("Password@123")
                .phone("9830098300")
                .dateOfBirth(LocalDate.of(1995, 7, 8))
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.sessionId").isNotEmpty())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.user.email").value("sourav@kolkata.com"))
                .andExpect(jsonPath("$.data.user.role").value("ROLE_PASSENGER"));
    }

    @Test
    void shouldRejectRegistrationWhenPasswordIsShorterThan8Characters() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .fullName("Test User")
                .username("testshort")
                .email("short@test.com")
                .password("pass1") // Less than 8 characters
                .phone("9830098301")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    void shouldRejectDuplicateEmailRegistration() throws Exception {
        RegisterRequest request1 = RegisterRequest.builder()
                .fullName("User One")
                .username("userone")
                .email("duplicate@test.com")
                .password("Password@123")
                .phone("9830098302")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        RegisterRequest request2 = RegisterRequest.builder()
                .fullName("User Two")
                .username("usertwo")
                .email("duplicate@test.com")
                .password("Password@456")
                .phone("9830098303")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void shouldLoginSuccessfullyAndAccessProtectedProfile() throws Exception {
        // 1. Register
        RegisterRequest registerRequest = RegisterRequest.builder()
                .fullName("Utsab Ghoshal")
                .username("utsab")
                .email("utsab@railsarathi.com")
                .password("SecurePass@123")
                .phone("9876543210")
                .dateOfBirth(LocalDate.of(2004, 1, 1))
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // 2. Login with email
        LoginRequest loginRequest = LoginRequest.builder()
                .emailOrUsername("utsab@railsarathi.com")
                .password("SecurePass@123")
                .build();

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.sessionId").isNotEmpty())
                .andReturn();

        JsonNode root = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String token = root.path("data").path("accessToken").asText();

        // 3. Access protected profile with Bearer token
        mockMvc.perform(get("/api/v1/users/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("utsab"))
                .andExpect(jsonPath("$.data.email").value("utsab@railsarathi.com"));
    }

    @Test
    void shouldRejectLoginWithInvalidPassword() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .fullName("Valid User")
                .username("validuser")
                .email("valid@test.com")
                .password("CorrectPass@123")
                .phone("9830098304")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = LoginRequest.builder()
                .emailOrUsername("valid@test.com")
                .password("WrongPassword")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void shouldRejectAccessToProtectedProfileWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/users/profile"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
}
