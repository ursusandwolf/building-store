package com.buildstore.pricing;

import com.buildstore.pricing.dto.DiscountRequest;
import com.buildstore.pricing.service.DiscountService;
import com.buildstore.product.model.Product;
import com.buildstore.product.model.ProductCategory;
import com.buildstore.product.model.ProductStatus;
import com.buildstore.product.model.UnitOfMeasure;
import com.buildstore.product.repository.ProductCategoryRepository;
import com.buildstore.product.repository.ProductRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DiscountTests {

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
        ProductCategory category = categoryRepository.findByName("General")
                .orElseGet(() -> categoryRepository.save(ProductCategory.builder().name("General").build()));
        
        product = productRepository.save(Product.builder()
                .sku("SKU-DISC")
                .name("Discount Item")
                .category(category)
                .baseUnit(UnitOfMeasure.PIECE)
                .status(ProductStatus.ACTIVE)
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDiscount_shouldSucceed() throws Exception {
        DiscountRequest request = new DiscountRequest(
                "Winter Sale",
                new BigDecimal("20.00"),
                1,
                true,
                product.getId()
        );

        mockMvc.perform(post("/api/admin/discounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
