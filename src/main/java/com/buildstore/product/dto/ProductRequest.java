package com.buildstore.product.dto;

import com.buildstore.product.model.ProductStatus;
import com.buildstore.product.model.UnitOfMeasure;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductRequest(
    @NotBlank(message = "SKU is required")
    @Size(max = 50, message = "SKU must not exceed 50 characters")
    String sku,

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    String name,

    Long categoryId,

    @Size(max = 100, message = "Brand must not exceed 100 characters")
    String brand,

    String description,

    @NotNull(message = "Base unit is required")
    UnitOfMeasure baseUnit,

    @NotNull(message = "Status is required")
    ProductStatus status
) {}
