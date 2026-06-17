package com.buildstore.accounting.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accounting_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountingEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "entry_date", nullable = false)
    private LocalDateTime entryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountingEntryType type;

    @Column(name = "debit_account", nullable = false)
    private String debitAccount;

    @Column(name = "credit_account", nullable = false)
    private String creditAccount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(name = "reference_type", nullable = false)
    private String referenceType;

    @Column(name = "reference_id", nullable = false)
    private UUID referenceId;

    @Column
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false)
    private String createdBy;
}
