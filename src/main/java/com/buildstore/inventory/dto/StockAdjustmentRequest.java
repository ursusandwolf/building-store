package com.buildstore.inventory.dto;

import com.buildstore.inventory.model.StockMovementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record StockAdjustmentRequest(
    @NotNull(message = "Product ID is required")
    Long productId,

    @NotNull(message = "Warehouse ID is required")
    Long warehouseId,

    @NotNull(message = "Movement type is required")
    StockMovementType type,

    @NotNull(message = "Quantity is required")
    BigDecimal quantity,

    String reason,

    @NotNull(message = "Idempotency key is required")
    String idempotencyKey
) {}
