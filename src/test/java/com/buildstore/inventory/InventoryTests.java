package com.buildstore.inventory;

import com.buildstore.inventory.dto.StockAdjustmentRequest;
import com.buildstore.inventory.model.StockMovementType;
import com.buildstore.product.model.Product;
import com.buildstore.product.model.ProductCategory;
import com.buildstore.product.model.ProductStatus;
import com.buildstore.product.model.UnitOfMeasure;
import com.buildstore.product.repository.ProductCategoryRepository;
import com.buildstore.product.repository.ProductRepository;
import com.buildstore.user.dto.AuthResponse;
import com.buildstore.user.dto.LoginRequest;
import com.buildstore.user.model.AppUser;
import com.buildstore.user.model.Role;
import com.buildstore.user.model.RoleName;
import com.buildstore.user.model.UserStatus;
import com.buildstore.user.repository.RoleRepository;
import com.buildstore.user.repository.UserRepository;
import com.buildstore.warehouse.model.Warehouse;
import com.buildstore.warehouse.repository.WarehouseRepository;
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

import java.math.BigDecimal;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class InventoryTests {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AuditorAware<String> auditorProvider() {
            return () -> {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth == null || !auth.isAuthenticated()) return Optional.of("SYSTEM");
                return Optional.of(auth.getName());
            };
        }
    }
    // ... rest of the file

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository categoryRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private Product product;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() throws Exception {
        adminToken = createToken("admin_inv@test.com", RoleName.ROLE_ADMIN);

        ProductCategory category = categoryRepository.findByName("General")
                .orElseGet(() -> {
                    ProductCategory c = new ProductCategory();
                    c.setName("General");
                    return categoryRepository.save(c);
                });
        
        product = productRepository.findBySku("SKU-INV")
                .orElseGet(() -> {
                    Product p = new Product();
                    p.setSku("SKU-INV");
                    p.setName("Inventory Item");
                    p.setCategory(category);
                    p.setBaseUnit(UnitOfMeasure.PIECE);
                    p.setStatus(ProductStatus.ACTIVE);
                    return productRepository.save(p);
                });

        warehouse = warehouseRepository.findByCode("WH-TEST")
                .orElseGet(() -> {
                    Warehouse w = new Warehouse();
                    w.setCode("WH-TEST");
                    w.setName("Test Warehouse");
                    return warehouseRepository.save(w);
                });
    }

    private String createToken(String email, RoleName roleName) throws Exception {
        String password = "password";
        if (userRepository.findByEmail(email).isEmpty()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            AppUser user = new AppUser();
            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode(password));
            user.setStatus(UserStatus.ACTIVE);
            user.setRoles(Set.of(role));
            userRepository.save(user);
        }

        LoginRequest loginRequest = new LoginRequest(email, password);

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
    void adjustStock_shouldSucceed() throws Exception {
        StockAdjustmentRequest request = new StockAdjustmentRequest(
                product.getId(),
                warehouse.getId(),
                StockMovementType.PURCHASE_RECEIPT,
                new BigDecimal("10.0000"),
                "Initial stock",
                "key-1"
        );

        mockMvc.perform(post("/api/admin/stock-adjustments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void adjustStock_negativeBalance_shouldFail() throws Exception {
        StockAdjustmentRequest request = new StockAdjustmentRequest(
                product.getId(),
                warehouse.getId(),
                StockMovementType.SALES_SHIPMENT,
                new BigDecimal("-5.0000"),
                "Should fail",
                "key-2"
        );

        mockMvc.perform(post("/api/admin/stock-adjustments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
