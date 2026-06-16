package com.buildstore.product.dto;

import com.buildstore.product.model.UnitOfMeasure;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductPackageRequest(
    @NotBlank(message = "Package name is required")
    @Size(max = 100, message = "Package name must not exceed 100 characters")
    String name,

    @NotNull(message = "Package unit is required")
    UnitOfMeasure unit,

    @NotNull(message = "Quantity in base unit is required")
    @Positive(message = "Quantity in base unit must be greater than zero")
    BigDecimal quantityInBaseUnit,

    @Size(max = 50, message = "Barcode must not exceed 50 characters")
    String barcode,

    Boolean defaultForSale,

    Boolean defaultForPurchase
) {}
