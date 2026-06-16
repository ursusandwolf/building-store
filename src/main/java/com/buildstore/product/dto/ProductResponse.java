package com.buildstore.product.dto;

import com.buildstore.product.model.ProductStatus;
import com.buildstore.product.model.UnitOfMeasure;

import java.time.Instant;

public record ProductResponse(
    Long id,
    String sku,
    String name,
    Long categoryId,
    String categoryName,
    String brand,
    String description,
    UnitOfMeasure baseUnit,
    ProductStatus status,
    Instant createdAt,
    Instant updatedAt
) {}
