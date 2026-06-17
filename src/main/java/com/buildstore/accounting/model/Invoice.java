package com.buildstore.accounting.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(nullable = false, unique = true)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    @Column(nullable = false)
    private String currency;

    @Column(name = "subtotal_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal subtotalAmount;

    @Column(name = "tax_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Version
    private Long version;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceLine> lines;
}
