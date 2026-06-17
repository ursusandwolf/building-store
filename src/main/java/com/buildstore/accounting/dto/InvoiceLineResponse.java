package com.buildstore.accounting.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class InvoiceLineResponse {
    private Long productId;
    private String description;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
}
