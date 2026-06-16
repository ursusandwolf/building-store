package com.buildstore.pricing.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PriceListItemRequest(
    @NotNull(message = "Product ID is required")
    Long productId,
    @NotNull(message = "Price is required")
    BigDecimal price
) {}
