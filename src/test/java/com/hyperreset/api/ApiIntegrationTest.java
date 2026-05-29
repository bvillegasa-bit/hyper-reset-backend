package com.hyperreset.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end integration tests for the Hyper Reset API.
 * <p>
 * Uses MockMvc with a random port and an embedded H2 database
 * to verify the full request → controller → service → response flow.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ------------------------------------------------------------------
    // Health Endpoint
    // ------------------------------------------------------------------

    @Test
    void healthEndpoint_Returns200() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    // ------------------------------------------------------------------
    // Auth: Register → Login → Profile E2E
    // ------------------------------------------------------------------

    @Test
    void registerCoach_Success() throws Exception {
        String body = "{"
                + "\"nombre\":\"Test Coach\","
                + "\"email\":\"coach.integration@test.com\","
                + "\"password\":\"password123\","
                + "\"rol\":\"COACH\""
                + "}";

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.rol").value("COACH"))
                .andReturn();

        // Verify we got a token
        String response = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(response);
        assertNotNull(root.get("data").get("token").asText());
    }

    @Test
    void registerLogin_E2E() throws Exception {
        // 1. Register
        String registerBody = "{"
                + "\"nombre\":\"E2E User\","
                + "\"email\":\"e2e.test@test.com\","
                + "\"password\":\"test1234\","
                + "\"rol\":\"COACH\""
                + "}";

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponse = registerResult.getResponse().getContentAsString();
        JsonNode registerRoot = objectMapper.readTree(registerResponse);
        String registerToken = registerRoot.get("data").get("token").asText();
        assertNotNull(registerToken, "Register should return a token");

        // 2. Login
        String loginBody = "{"
                + "\"email\":\"e2e.test@test.com\","
                + "\"password\":\"test1234\""
                + "}";

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        assertTrue(loginResponse.contains("token"), "Login response should contain a token");

        // 3. Get Profile using the login token
        String loginToken = objectMapper.readTree(loginResponse).get("data").get("token").asText();

        mockMvc.perform(get("/api/auth/profile")
                        .header("Authorization", "Bearer " + loginToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("e2e.test@test.com"))
                .andExpect(jsonPath("$.data.rol").value("COACH"));
    }

    // ------------------------------------------------------------------
    // Auth: Validation errors
    // ------------------------------------------------------------------

    @Test
    void register_WithDuplicateEmail_Returns400() throws Exception {
        // First registration
        String body = "{"
                + "\"nombre\":\"First User\","
                + "\"email\":\"duplicate@test.com\","
                + "\"password\":\"password123\","
                + "\"rol\":\"COACH\""
                + "}";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        // Second registration with same email
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
        // Error response uses ErrorResponse format {status, message, timestamp, errors}
        // so we do NOT assert $.success — it does not exist in ErrorResponse
    }

    @Test
    void login_WithInvalidCredentials_Returns401() throws Exception {
        // Register first
        String registerBody = "{"
                + "\"nombre\":\"Login Test\","
                + "\"email\":\"logintest@test.com\","
                + "\"password\":\"password123\","
                + "\"rol\":\"COACH\""
                + "}";
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        // Login with wrong password
        String body = "{"
                + "\"email\":\"logintest@test.com\","
                + "\"password\":\"wrongpassword123\""
                + "}";

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andReturn();

        // Error response uses ErrorResponse format: { status, message, timestamp, errors }
        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("status") || content.contains("message"),
                "Response should contain error details");
    }

    @Test
    void getProfile_WithoutToken_Returns401() throws Exception {
        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().isUnauthorized());
    }

    // ------------------------------------------------------------------
    // Auth: Request validation
    // ------------------------------------------------------------------

    @Test
    void register_WithShortPassword_Returns400() throws Exception {
        String body = "{"
                + "\"nombre\":\"Test User\","
                + "\"email\":\"test.short@test.com\","
                + "\"password\":\"123\","
                + "\"rol\":\"COACH\""
                + "}";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_WithInvalidEmail_Returns400() throws Exception {
        String body = "{"
                + "\"nombre\":\"Test User\","
                + "\"email\":\"not-an-email\","
                + "\"password\":\"password123\","
                + "\"rol\":\"COACH\""
                + "}";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------
    // Swagger / OpenAPI
    // ------------------------------------------------------------------

    @Test
    void swaggerUi_ReturnsOk() throws Exception {
        // Swagger UI redirects to the actual page
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void apiDocs_Returns200() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk());
    }

    // ------------------------------------------------------------------
    // CORS headers
    // ------------------------------------------------------------------

    @Test
    void corsPreflight_ReturnsAllowedHeaders() throws Exception {
        mockMvc.perform(options("/api/auth/login")
                        .header("Origin", "http://10.0.2.2:8080")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Authorization, Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().string("Access-Control-Allow-Methods",
                        org.hamcrest.Matchers.containsString("POST")));
    }

    @Test
    void cors_WithValidOrigin_ReturnsCorsHeaders() throws Exception {
        mockMvc.perform(get("/api/health")
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }
}
