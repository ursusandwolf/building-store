package com.buildstore.inventory.dto;

import com.buildstore.inventory.model.StockMovementType;
import com.buildstore.product.model.UnitOfMeasure;
import java.math.BigDecimal;
import java.time.Instant;

public record StockMovementResponse(
    Long id,
    Long productId,
    Long warehouseId,
    StockMovementType type,
    BigDecimal quantity,
    UnitOfMeasure unit,
    BigDecimal balanceAfter,
    String reason,
    Instant createdAt
) {}
