package com.buildstore.purchase.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PurchaseOrderRequest(
    @NotNull(message = "Supplier ID is required")
    Long supplierId,

    @NotNull(message = "Lines are required")
    List<@Valid PurchaseOrderLineRequest> lines
) {}
