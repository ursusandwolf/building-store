package com.buildstore.security;

import com.buildstore.user.dto.AuthResponse;
import com.buildstore.user.dto.LoginRequest;
import com.buildstore.user.model.AppUser;
import com.buildstore.user.model.Role;
import com.buildstore.user.model.RoleName;
import com.buildstore.user.model.UserStatus;
import com.buildstore.user.repository.RoleRepository;
import com.buildstore.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MethodSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String PASSWORD = "password123";

    @BeforeEach
    void setUp() {
        createUserIfNotExist("customer@test.com", RoleName.ROLE_CUSTOMER);
        createUserIfNotExist("admin@test.com", RoleName.ROLE_ADMIN);
        createUserIfNotExist("accountant@test.com", RoleName.ROLE_ACCOUNTANT);
        createUserIfNotExist("sales@test.com", RoleName.ROLE_SALES_MANAGER);
        createUserIfNotExist("warehouse@test.com", RoleName.ROLE_WAREHOUSE_MANAGER);
    }

    private void createUserIfNotExist(String email, RoleName roleName) {
        if (userRepository.findByEmail(email).isEmpty()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            AppUser user = new AppUser();
            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode(PASSWORD));
            user.setStatus(UserStatus.ACTIVE);
            user.setRoles(Set.of(role));
            userRepository.save(user);
        }
    }

    private String obtainAccessToken(String email) throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .email(email)
                .password(PASSWORD)
                .build();

        String responseJson = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthResponse authResponse = objectMapper.readValue(responseJson, AuthResponse.class);
        return authResponse.accessToken();
    }

    // --- GET /api/admin/users ---

    @Test
    void getAdminUsers_anonymous_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAdminUsers_customer_shouldReturn403() throws Exception {
        String token = obtainAccessToken("customer@test.com");
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAdminUsers_accountant_shouldReturn403() throws Exception {
        String token = obtainAccessToken("accountant@test.com");
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAdminUsers_admin_shouldSucceed() throws Exception {
        String token = obtainAccessToken("admin@test.com");
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.email == 'admin@test.com')]").exists());
    }

    // --- GET /api/employees/me ---

    @Test
    void getEmployeesMe_anonymous_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/employees/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getEmployeesMe_customer_shouldReturn403() throws Exception {
        String token = obtainAccessToken("customer@test.com");
        mockMvc.perform(get("/api/employees/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void getEmployeesMe_salesManager_shouldSucceed() throws Exception {
        String token = obtainAccessToken("sales@test.com");
        mockMvc.perform(get("/api/employees/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("sales@test.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_SALES_MANAGER"));
    }

    @Test
    void getEmployeesMe_warehouseManager_shouldSucceed() throws Exception {
        String token = obtainAccessToken("warehouse@test.com");
        mockMvc.perform(get("/api/employees/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("warehouse@test.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_WAREHOUSE_MANAGER"));
    }

    @Test
    void getEmployeesMe_accountant_shouldSucceed() throws Exception {
        String token = obtainAccessToken("accountant@test.com");
        mockMvc.perform(get("/api/employees/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("accountant@test.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ACCOUNTANT"));
    }

    @Test
    void getEmployeesMe_admin_shouldSucceed() throws Exception {
        String token = obtainAccessToken("admin@test.com");
        mockMvc.perform(get("/api/employees/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@test.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));
    }
}
