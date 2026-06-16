package com.buildstore.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SalesOrderRequest(
    @NotNull(message = "Lines are required")
    List<@Valid SalesOrderLineRequest> lines
) {}
