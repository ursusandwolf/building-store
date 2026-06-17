package com.buildstore.accounting.controller;

import com.buildstore.accounting.dto.InvoiceResponse;
import com.buildstore.accounting.mapper.InvoiceMapper;
import com.buildstore.accounting.repository.InvoiceRepository;
import com.buildstore.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounting/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ACCOUNTANT') or hasRole('ROLE_ADMIN')")
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(invoiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ACCOUNTANT') or hasRole('ROLE_ADMIN')")
    public InvoiceResponse getInvoice(@PathVariable UUID id) {
        return invoiceRepository.findById(id)
                .map(invoiceMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    }
}
