package com.buildstore.pricing.dto;

import java.math.BigDecimal;

public record PriceResponse(
    BigDecimal price
) {}
