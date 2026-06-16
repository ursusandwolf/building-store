package com.buildstore.order.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record SalesOrderLineRequest(
    @NotNull(message = "Product ID is required")
    Long productId,
    @NotNull(message = "Quantity is required")
    BigDecimal quantity
) {}
