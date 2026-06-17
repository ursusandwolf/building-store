package com.buildstore.accounting.dto;

import com.buildstore.accounting.model.InvoiceStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class InvoiceResponse {
    private UUID id;
    private Long orderId;
    private Long customerId;
    private String number;
    private InvoiceStatus status;
    private String currency;
    private BigDecimal subtotalAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private LocalDateTime issuedAt;
    private LocalDateTime dueDate;
    private List<InvoiceLineResponse> lines;
}
