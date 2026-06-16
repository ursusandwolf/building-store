package com.buildstore.order.dto;

import com.buildstore.order.model.SalesOrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record SalesOrderResponse(
    Long id,
    SalesOrderStatus status,
    BigDecimal totalAmount,
    List<SalesOrderLineResponse> lines,
    Instant createdAt
) {}
