package com.buildstore.purchase;

import com.buildstore.product.model.Product;
import com.buildstore.product.model.ProductCategory;
import com.buildstore.product.model.ProductStatus;
import com.buildstore.product.model.UnitOfMeasure;
import com.buildstore.product.repository.ProductCategoryRepository;
import com.buildstore.product.repository.ProductRepository;
import com.buildstore.purchase.dto.PurchaseOrderRequest;
import com.buildstore.supplier.model.Supplier;
import com.buildstore.supplier.repository.SupplierRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PurchaseOrderTests {

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
    // ...

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private Supplier supplier;
    private Product product;

    @BeforeEach
    void setUp() throws Exception {
        adminToken = createToken("admin_po@test.com", RoleName.ROLE_ADMIN);

        Supplier s = new Supplier();
        s.setName("Test Supplier");
        supplier = supplierRepository.save(s);
        
        ProductCategory category = categoryRepository.findByName("General")
                .orElseGet(() -> {
                    ProductCategory c = new ProductCategory();
                    c.setName("General");
                    return categoryRepository.save(c);
                });
        
        Product p = new Product();
        p.setSku("SKU-PO");
        p.setName("PO Item");
        p.setCategory(category);
        p.setBaseUnit(UnitOfMeasure.PIECE);
        p.setStatus(ProductStatus.ACTIVE);
        product = productRepository.save(p);
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
    void createPurchaseOrder_shouldSucceed() throws Exception {
        PurchaseOrderRequest request = new PurchaseOrderRequest(
                supplier.getId(),
                List.of(new com.buildstore.purchase.dto.PurchaseOrderLineRequest(
                        product.getId(),
                        new BigDecimal("10.0"),
                        new BigDecimal("5.0")
                ))
        );

        mockMvc.perform(post("/api/admin/purchase-orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.supplierName").value("Test Supplier"));
    }
}
