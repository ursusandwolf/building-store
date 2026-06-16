package com.buildstore.order;

import com.buildstore.inventory.service.InventoryService;
import com.buildstore.order.dto.SalesOrderRequest;
import com.buildstore.product.model.Product;
import com.buildstore.product.model.ProductCategory;
import com.buildstore.product.model.ProductStatus;
import com.buildstore.product.model.UnitOfMeasure;
import com.buildstore.product.repository.ProductCategoryRepository;
import com.buildstore.product.repository.ProductRepository;
import com.buildstore.pricing.dto.PriceListRequest;
import com.buildstore.pricing.dto.PriceListItemRequest;
import com.buildstore.pricing.service.PriceListService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrderTests {

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfig {
        @org.springframework.context.annotation.Bean
        public org.springframework.data.domain.AuditorAware<String> auditorProvider() {
            return () -> {
                org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
                if (auth == null || !auth.isAuthenticated()) return java.util.Optional.of("SYSTEM");
                return java.util.Optional.of(auth.getName());
            };
        }
    }

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
    private PriceListService priceListService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ObjectMapper objectMapper;

    private String customerToken;
    private Product product;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() throws Exception {
        customerToken = createToken("cust_ord@test.com", RoleName.ROLE_CUSTOMER);

        ProductCategory category = categoryRepository.findByName("General")
                .orElseGet(() -> {
                    ProductCategory c = new ProductCategory();
                    c.setName("General");
                    return categoryRepository.save(c);
                });

        product = new Product();
        product.setSku("SKU-ORD");
        product.setName("Order Item");
        product.setCategory(category);
        product.setBaseUnit(UnitOfMeasure.PIECE);
        product.setStatus(ProductStatus.ACTIVE);
        product = productRepository.save(product);

        warehouse = new Warehouse();
        warehouse.setCode("WH-ORD");
        warehouse.setName("Order Warehouse");
        warehouse.setActive(true);
        warehouse = warehouseRepository.save(warehouse);
        
        inventoryService.adjustStock(new com.buildstore.inventory.dto.StockAdjustmentRequest(
                product.getId(),
                warehouse.getId(),
                com.buildstore.inventory.model.StockMovementType.PURCHASE_RECEIPT,
                new BigDecimal("100.0"),
                "Initial stock",
                "init-key"
        ));

        // Create PriceList for item
        Long priceListId = priceListService.createPriceList(new PriceListRequest(
                "Default",
                Instant.now().minus(1, ChronoUnit.DAYS),
                null
        ));
        priceListService.addItem(priceListId, new PriceListItemRequest(product.getId(), new BigDecimal("10.0")));
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
    void createOrderDraft_shouldSucceed() throws Exception {
        var request = new com.buildstore.order.dto.SalesOrderRequest(
                List.of(new com.buildstore.order.dto.SalesOrderLineRequest(
                        product.getId(),
                        new BigDecimal("2.0"),
                        warehouse.getId()
                ))
        );

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.totalAmount").value(20.0));
    }
}
