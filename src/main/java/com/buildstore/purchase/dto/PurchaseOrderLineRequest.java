package com.buildstore.purchase.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PurchaseOrderLineRequest(
    @NotNull(message = "Product ID is required")
    Long productId,

    @NotNull(message = "Quantity is required")
    BigDecimal quantity,

    @NotNull(message = "Unit cost is required")
    BigDecimal unitCost
) {}
