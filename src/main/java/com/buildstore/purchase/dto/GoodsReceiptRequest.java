package com.buildstore.purchase.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record GoodsReceiptRequest(
    @NotNull(message = "Idempotency key is required")
    String idempotencyKey,

    @NotNull(message = "Lines are required")
    List<@Valid GoodsReceiptLineRequest> lines
) {}
