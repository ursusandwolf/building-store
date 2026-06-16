package com.buildstore.order.dto;

import java.math.BigDecimal;

public record SalesOrderLineResponse(
    Long productId,
    String productName,
    BigDecimal quantity,
    BigDecimal priceAtOrder
) {}
