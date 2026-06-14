package com.example.buildstore.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicEndpoint_shouldBeAccessibleByAnonymous() throws Exception {
        mockMvc.perform(get("/api/public/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void privateEndpoint_shouldReturn401ForAnonymous() throws Exception {
        mockMvc.perform(get("/api/private/hello"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user")
    void privateEndpoint_shouldBeAccessibleByAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/private/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello from private endpoint!"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void privateEndpoint_shouldBeAccessibleByAdminUser() throws Exception {
        mockMvc.perform(get("/api/private/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello from private endpoint!"));
    }
}
