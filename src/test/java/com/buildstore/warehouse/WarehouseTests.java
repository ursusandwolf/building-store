package com.buildstore.warehouse;

import com.buildstore.user.dto.AuthResponse;
import com.buildstore.user.dto.LoginRequest;
import com.buildstore.user.model.AppUser;
import com.buildstore.user.model.Role;
import com.buildstore.user.model.RoleName;
import com.buildstore.user.model.UserStatus;
import com.buildstore.user.repository.RoleRepository;
import com.buildstore.user.repository.UserRepository;
import com.buildstore.warehouse.dto.WarehouseRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class WarehouseTests {

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

    private String adminToken;
    private String warehouseManagerToken;
    private String customerToken;

    @BeforeEach
    void setUp() throws Exception {
        adminToken = createToken("admin_wh@test.com", RoleName.ROLE_ADMIN);
        warehouseManagerToken = createToken("wh_manager@test.com", RoleName.ROLE_WAREHOUSE_MANAGER);
        customerToken = createToken("customer_wh@test.com", RoleName.ROLE_CUSTOMER);
    }

    private String createToken(String email, RoleName roleName) throws Exception {
        String password = "password";
        if (userRepository.findByEmail(email).isEmpty()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            AppUser user = AppUser.builder()
                    .email(email)
                    .passwordHash(passwordEncoder.encode(password))
                    .status(UserStatus.ACTIVE)
                    .roles(Set.of(role))
                    .build();
            userRepository.save(user);
        }

        LoginRequest loginRequest = LoginRequest.builder()
                .email(email)
                .password(password)
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

    @Test
    void warehouseManagement_workflow_shouldSucceed() throws Exception {
        // 1. Create warehouse as Admin
        WarehouseRequest request = new WarehouseRequest(
                "WH-MAIN",
                "Main Warehouse",
                "123 Industry St",
                true
        );

        String createResponse = mockMvc.perform(post("/api/admin/warehouses")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("WH-MAIN"))
                .andExpect(jsonPath("$.name").value("Main Warehouse"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long warehouseId = objectMapper.readTree(createResponse).get("id").asLong();

        // 2. Try to create duplicate warehouse
        mockMvc.perform(post("/api/admin/warehouses")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Warehouse with code WH-MAIN already exists"));

        // 3. Update warehouse as Admin
        WarehouseRequest updateRequest = new WarehouseRequest(
                "WH-MAIN-UPDATED",
                "Main Warehouse Updated",
                "456 Business Rd",
                false
        );

        mockMvc.perform(put("/api/admin/warehouses/" + warehouseId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("WH-MAIN-UPDATED"))
                .andExpect(jsonPath("$.active").value(false));

        // 4. Get all warehouses as Warehouse Manager
        mockMvc.perform(get("/api/admin/warehouses")
                        .header("Authorization", "Bearer " + warehouseManagerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].code").value("WH-MAIN-UPDATED"));

        // 5. Access denied for Customer
        mockMvc.perform(get("/api/admin/warehouses")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/admin/warehouses")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
