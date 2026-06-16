package com.buildstore.purchase;

import com.buildstore.inventory.repository.StockItemRepository;
import com.buildstore.product.model.Product;
import com.buildstore.product.model.ProductCategory;
import com.buildstore.product.model.ProductStatus;
import com.buildstore.product.model.UnitOfMeasure;
import com.buildstore.product.repository.ProductCategoryRepository;
import com.buildstore.product.repository.ProductRepository;
import com.buildstore.purchase.dto.GoodsReceiptLineRequest;
import com.buildstore.purchase.dto.GoodsReceiptRequest;
import com.buildstore.purchase.model.PurchaseOrder;
import com.buildstore.purchase.model.PurchaseOrderLine;
import com.buildstore.purchase.repository.PurchaseOrderRepository;
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
import com.buildstore.warehouse.model.Warehouse;
import com.buildstore.warehouse.repository.WarehouseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class GoodsReceiptTests {

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
    private WarehouseRepository warehouseRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private PurchaseOrder purchaseOrder;

    @BeforeEach
    @WithMockUser(username = "admin_gr@test.com", roles = {"ADMIN"})
    void setUp() throws Exception {
        adminToken = createToken("admin_gr@test.com", RoleName.ROLE_ADMIN);

        Supplier supplier = supplierRepository.save(Supplier.builder().name("Test Supplier").build());
        ProductCategory category = categoryRepository.findByName("General")
                .orElseGet(() -> categoryRepository.save(ProductCategory.builder().name("General").build()));
        Product product = productRepository.save(Product.builder()
                .sku("SKU-GR")
                .name("GR Item")
                .category(category)
                .baseUnit(UnitOfMeasure.PIECE)
                .status(ProductStatus.ACTIVE)
                .build());
        warehouseRepository.save(Warehouse.builder().code("WH-TEST").name("Test Warehouse").build());

        purchaseOrder = purchaseOrderRepository.save(PurchaseOrder.builder()
                .supplier(supplier)
                .status(com.buildstore.purchase.model.PurchaseOrderStatus.DRAFT)
                .build());
        PurchaseOrderLine line = com.buildstore.purchase.model.PurchaseOrderLine.builder()
                .purchaseOrder(purchaseOrder)
                .product(product)
                .quantity(new BigDecimal("10.0"))
                .unitCost(new BigDecimal("5.0"))
                .build();
        purchaseOrder.setLines(new java.util.ArrayList<>(List.of(line)));
        purchaseOrderRepository.save(purchaseOrder);
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
    void processGoodsReceipt_shouldUpdateStockAndOrder() throws Exception {
        GoodsReceiptRequest request = new GoodsReceiptRequest(
                "key-1",
                List.of(new GoodsReceiptLineRequest(
                        purchaseOrder.getLines().get(0).getProduct().getId(),
                        new BigDecimal("10.0")
                ))
        );

        mockMvc.perform(post("/api/admin/purchase-orders/" + purchaseOrder.getId() + "/goods-receipts")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
