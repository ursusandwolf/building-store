package com.buildstore.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DatabaseAuthenticationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginWithCorrectCredentials_shouldSucceed() throws Exception {
        mockMvc.perform(get("/api/private/hello")
                .with(httpBasic("user@example.com", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello from private endpoint!"));
    }

    @Test
    void loginWithIncorrectPassword_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/private/hello")
                .with(httpBasic("user@example.com", "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithNonExistentUser_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/private/hello")
                .with(httpBasic("nonexistent@example.com", "password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithAdminCredentials_shouldSucceed() throws Exception {
        mockMvc.perform(get("/api/private/hello")
                .with(httpBasic("admin@example.com", "admin-pass")))
                .andExpect(status().isOk());
    }
}
