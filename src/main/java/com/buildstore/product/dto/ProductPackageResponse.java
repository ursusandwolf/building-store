package com.buildstore.product.dto;

import com.buildstore.product.model.UnitOfMeasure;

import java.math.BigDecimal;

public record ProductPackageResponse(
    Long id,
    Long productId,
    String name,
    UnitOfMeasure unit,
    BigDecimal quantityInBaseUnit,
    String barcode,
    boolean defaultForSale,
    boolean defaultForPurchase
) {}
