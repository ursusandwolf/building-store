package com.buildstore.accounting.service;

import com.buildstore.accounting.model.AccountingEntry;
import com.buildstore.accounting.model.AccountingEntryType;
import com.buildstore.accounting.model.Invoice;
import com.buildstore.accounting.repository.AccountingEntryRepository;
import com.buildstore.accounting.repository.InvoiceRepository;
import com.buildstore.audit.model.AuditEvent;
import com.buildstore.audit.service.AuditService;
import com.buildstore.security.service.SystemUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import java.math.BigDecimal;
import com.buildstore.accounting.model.InvoiceStatus;
import com.buildstore.accounting.model.InvoiceLine;
import java.util.List;
import com.buildstore.order.model.SalesOrder;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountingService {

    private final InvoiceRepository invoiceRepository;
    private final AccountingEntryRepository accountingEntryRepository;
    private final AuditService auditService;
    private final SystemUserProvider systemUserProvider;

    @Transactional
    public void createDraftInvoice(SalesOrder order) {
        String createdBy = SecurityContextHolder.getContext().getAuthentication().getName();
        
        Invoice invoice = Invoice.builder()
                .orderId(order.getId())
                .customerId(order.getCustomer().getId())
                .number("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .status(InvoiceStatus.DRAFT)
                .currency(order.getCurrency())
                .subtotalAmount(order.getTotalAmount())
                .taxAmount(BigDecimal.ZERO)
                .totalAmount(order.getTotalAmount())
                .createdBy(createdBy)
                .build();

        List<InvoiceLine> lines = order.getLines().stream().map(line -> 
            InvoiceLine.builder()
                    .invoice(invoice)
                    .productId(line.getProduct().getId())
                    .description(line.getProductNameSnapshot())
                    .quantity(line.getQuantity())
                    .unit(line.getUnit().name())
                    .unitPrice(line.getPriceAtOrder())
                    .lineTotal(line.getPriceAtOrder().multiply(line.getQuantity()))
                    .build()
        ).collect(Collectors.toList());

        invoice.setLines(lines);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        
        auditService.logEvent(AuditEvent.builder()
                .actorType("USER")
                .actorId(systemUserProvider.getSystemUser().getId())
                .action("INVOICE_CREATED")
                .subjectType("INVOICE")
                .reason("Invoice created: " + savedInvoice.getId() + " for order " + order.getId())
                .build());
    }

    @Transactional
    public void recordEntry(AccountingEntryType type, 
                            String debitAccount, 
                            String creditAccount, 
                            java.math.BigDecimal amount, 
                            String currency, 
                            String referenceType, 
                            UUID referenceId, 
                            String description, 
                            String createdBy) {
        
        AccountingEntry entry = AccountingEntry.builder()
                .entryDate(LocalDateTime.now())
                .type(type)
                .debitAccount(debitAccount)
                .creditAccount(creditAccount)
                .amount(amount)
                .currency(currency)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .description(description)
                .createdAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();
        
        AccountingEntry savedEntry = accountingEntryRepository.save(entry);
        
        auditService.logEvent(AuditEvent.builder()
                .actorType("USER")
                .actorId(systemUserProvider.getSystemUser().getId())
                .action("ACCOUNTING_ENTRY_RECORDED")
                .subjectType("ACCOUNTING_ENTRY")
                .reason("Entry recorded: " + savedEntry.getId() + ". Description: " + description)
                .build());
    }
}
