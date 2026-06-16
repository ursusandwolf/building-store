package com.buildstore.pricing;

import com.buildstore.product.model.Product;
import com.buildstore.product.model.ProductCategory;
import com.buildstore.product.model.ProductStatus;
import com.buildstore.product.model.UnitOfMeasure;
import com.buildstore.product.repository.ProductCategoryRepository;
import com.buildstore.product.repository.ProductRepository;
import com.buildstore.pricing.dto.PriceListRequest;
import com.buildstore.pricing.dto.PriceListItemRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PricingTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;

    @BeforeEach
    void setUp() {
        ProductCategory category = categoryRepository.save(ProductCategory.builder().name("General").build());
        product = productRepository.save(Product.builder()
                .sku("SKU-PRICE")
                .name("Price Item")
                .category(category)
                .baseUnit(UnitOfMeasure.PIECE)
                .status(ProductStatus.ACTIVE)
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void priceListWorkflow_shouldSucceed() throws Exception {
        // 1. Create Price List
        PriceListRequest listRequest = new PriceListRequest(
                "Winter Sale",
                Instant.now().minus(1, ChronoUnit.DAYS),
                Instant.now().plus(1, ChronoUnit.DAYS)
        );

        String listResponse = mockMvc.perform(post("/api/admin/price-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(listRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long priceListId = Long.parseLong(listResponse);

        // 2. Add Item
        PriceListItemRequest itemRequest = new PriceListItemRequest(product.getId(), new BigDecimal("99.99"));
        mockMvc.perform(post("/api/admin/price-lists/" + priceListId + "/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isCreated());

        // 3. Verify Active Price
        mockMvc.perform(get("/api/catalog/products/" + product.getId() + "/price"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(99.99));
    }
}
