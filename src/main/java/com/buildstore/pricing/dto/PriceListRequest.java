package com.buildstore.pricing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record PriceListRequest(
    @NotBlank(message = "Name is required")
    String name,
    @NotNull(message = "Start date is required")
    Instant startDate,
    Instant endDate
) {}
