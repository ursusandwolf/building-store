package com.buildstore.pricing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record DiscountRequest(
    @NotBlank(message = "Name is required")
    String name,
    @NotNull(message = "Percentage is required")
    BigDecimal percentage,
    @NotNull(message = "Priority is required")
    Integer priority,
    @NotNull(message = "Compatibility is required")
    boolean compatible,
    Long productId
) {}
