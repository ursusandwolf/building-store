package com.buildstore.purchase.dto;

import java.math.BigDecimal;

public record PurchaseOrderLineResponse(
    Long productId,
    String productName,
    BigDecimal quantity,
    BigDecimal unitCost
) {}
