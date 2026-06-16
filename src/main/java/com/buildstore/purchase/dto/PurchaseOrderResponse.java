package com.buildstore.purchase.dto;

import com.buildstore.purchase.model.PurchaseOrderStatus;
import java.time.Instant;
import java.util.List;

public record PurchaseOrderResponse(
    Long id,
    Long supplierId,
    String supplierName,
    PurchaseOrderStatus status,
    List<PurchaseOrderLineResponse> lines,
    Instant createdAt,
    String createdBy
) {}
