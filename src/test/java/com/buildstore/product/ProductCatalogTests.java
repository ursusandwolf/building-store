package com.buildstore.product;

import com.buildstore.product.dto.ProductPackageRequest;
import com.buildstore.product.dto.ProductRequest;
import com.buildstore.product.model.ProductCategory;
import com.buildstore.product.model.ProductStatus;
import com.buildstore.product.model.UnitOfMeasure;
import com.buildstore.product.repository.ProductCategoryRepository;
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

import java.math.BigDecimal;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductCatalogTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProductCategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private ProductCategory generalCategory;

    @BeforeEach
    void setUp() throws Exception {
        String email = "admin_catalog@test.com";
        String password = "admin-password";
        if (userRepository.findByEmail(email).isEmpty()) {
            Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Role not found: ROLE_ADMIN"));
            AppUser admin = AppUser.builder()
                    .email(email)
                    .passwordHash(passwordEncoder.encode(password))
                    .status(UserStatus.ACTIVE)
                    .roles(Set.of(adminRole))
                    .build();
            userRepository.save(admin);
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
        adminToken = authResponse.accessToken();

        generalCategory = categoryRepository.findByName("General")
                .orElseGet(() -> categoryRepository.save(
                        ProductCategory.builder()
                                .name("General")
                                .description("General category")
                                .build()
                ));
    }

    @Test
    void createProduct_asAdmin_shouldSucceed() throws Exception {
        ProductRequest request = new ProductRequest(
                "SKU-100",
                "Portland Cement M500",
                generalCategory.getId(),
                "Eurocement",
                "Eurocement 50kg bag",
                UnitOfMeasure.BAG,
                ProductStatus.DRAFT
        );

        mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.sku").value("SKU-100"))
                .andExpect(jsonPath("$.name").value("Portland Cement M500"))
                .andExpect(jsonPath("$.categoryId").value(generalCategory.getId()))
                .andExpect(jsonPath("$.categoryName").value("General"))
                .andExpect(jsonPath("$.brand").value("Eurocement"))
                .andExpect(jsonPath("$.description").value("Eurocement 50kg bag"))
                .andExpect(jsonPath("$.baseUnit").value("BAG"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void createProduct_duplicateSku_shouldReturn400() throws Exception {
        ProductRequest request1 = new ProductRequest(
                "SKU-DUPE",
                "Product 1",
                generalCategory.getId(),
                "Brand",
                "Desc",
                UnitOfMeasure.PIECE,
                ProductStatus.DRAFT
        );

        mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        ProductRequest request2 = new ProductRequest(
                "SKU-DUPE",
                "Product 2",
                generalCategory.getId(),
                "Brand",
                "Desc",
                UnitOfMeasure.PIECE,
                ProductStatus.DRAFT
        );

        mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Product with SKU SKU-DUPE already exists"));
    }

    @Test
    void updateProduct_asAdmin_shouldSucceed() throws Exception {
        ProductRequest createRequest = new ProductRequest(
                "SKU-TO-UPDATE",
                "Old Product Name",
                generalCategory.getId(),
                "Old Brand",
                "Old Desc",
                UnitOfMeasure.PIECE,
                ProductStatus.DRAFT
        );

        String createResponse = mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long productId = objectMapper.readTree(createResponse).get("id").asLong();

        ProductRequest updateRequest = new ProductRequest(
                "SKU-UPDATED",
                "New Product Name",
                generalCategory.getId(),
                "New Brand",
                "New Desc",
                UnitOfMeasure.METER,
                ProductStatus.ACTIVE
        );

        mockMvc.perform(put("/api/admin/products/" + productId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU-UPDATED"))
                .andExpect(jsonPath("$.name").value("New Product Name"))
                .andExpect(jsonPath("$.baseUnit").value("METER"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void catalogEndpoints_shouldFilterStatusAndHandleDetails() throws Exception {
        ProductRequest activeProduct = new ProductRequest(
                "SKU-ACTIVE",
                "Active Material",
                generalCategory.getId(),
                "Brand A",
                "Description A",
                UnitOfMeasure.TON,
                ProductStatus.ACTIVE
        );

        String activeResponse = mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(activeProduct)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long activeId = objectMapper.readTree(activeResponse).get("id").asLong();

        ProductRequest draftProduct = new ProductRequest(
                "SKU-DRAFT",
                "Draft Material",
                generalCategory.getId(),
                "Brand B",
                "Description B",
                UnitOfMeasure.PIECE,
                ProductStatus.DRAFT
        );

        String draftResponse = mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(draftProduct)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long draftId = objectMapper.readTree(draftResponse).get("id").asLong();

        mockMvc.perform(get("/api/catalog/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.sku == 'SKU-ACTIVE')]").exists())
                .andExpect(jsonPath("$[?(@.sku == 'SKU-DRAFT')]").doesNotExist());

        mockMvc.perform(get("/api/catalog/products/" + activeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU-ACTIVE"));

        mockMvc.perform(get("/api/catalog/products/" + draftId))
                .andExpect(status().isNotFound());
    }

    @Test
    void productPackages_workflow_shouldSucceed() throws Exception {
        // 1. Create product
        ProductRequest productRequest = new ProductRequest(
                "SKU-PKG-TEST",
                "Product for package test",
                generalCategory.getId(),
                "Brand",
                "Desc",
                UnitOfMeasure.KILOGRAM,
                ProductStatus.ACTIVE
        );

        String productResponse = mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long productId = objectMapper.readTree(productResponse).get("id").asLong();

        // 2. Add package
        ProductPackageRequest pkgRequest = new ProductPackageRequest(
                "Bag 25kg",
                UnitOfMeasure.BAG,
                new BigDecimal("25.0000"),
                "123456789",
                true,
                true
        );

        mockMvc.perform(post("/api/admin/products/" + productId + "/packages")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pkgRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bag 25kg"))
                .andExpect(jsonPath("$.unit").value("BAG"))
                .andExpect(jsonPath("$.quantityInBaseUnit").value(25.0))
                .andExpect(jsonPath("$.barcode").value("123456789"))
                .andExpect(jsonPath("$.defaultForSale").value(true));

        // 3. Add another package with duplicate barcode
        ProductPackageRequest dupeBarcodeRequest = new ProductPackageRequest(
                "Another Bag",
                UnitOfMeasure.BAG,
                new BigDecimal("25.0000"),
                "123456789",
                false,
                false
        );

        mockMvc.perform(post("/api/admin/products/" + productId + "/packages")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dupeBarcodeRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Package with barcode 123456789 already exists"));

        // 4. Verify packages in admin API
        mockMvc.perform(get("/api/admin/products/" + productId + "/packages")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Bag 25kg"));

        // 5. Verify packages in catalog API
        mockMvc.perform(get("/api/catalog/products/" + productId + "/packages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Bag 25kg"));
    }

    @Test
    void catalogGetPackages_draftProduct_shouldReturn404() throws Exception {
        ProductRequest draftProduct = new ProductRequest(
                "SKU-DRAFT-PKG",
                "Draft Material",
                generalCategory.getId(),
                "Brand",
                "Desc",
                UnitOfMeasure.PIECE,
                ProductStatus.DRAFT
        );

        String draftResponse = mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(draftProduct)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long draftId = objectMapper.readTree(draftResponse).get("id").asLong();

        mockMvc.perform(get("/api/catalog/products/" + draftId + "/packages"))
                .andExpect(status().isNotFound());
    }
}
