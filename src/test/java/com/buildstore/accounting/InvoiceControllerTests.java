package com.buildstore.accounting;

import com.buildstore.accounting.model.Invoice;
import com.buildstore.accounting.model.InvoiceStatus;
import com.buildstore.accounting.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class InvoiceControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InvoiceRepository invoiceRepository;

    private UUID invoiceId;

    @BeforeEach
    void setUp() {
        invoiceRepository.deleteAll();
        Invoice invoice = Invoice.builder()
                .orderId(1L)
                .customerId(1L)
                .number("INV-001")
                .status(InvoiceStatus.DRAFT)
                .currency("USD")
                .subtotalAmount(new BigDecimal("100.00"))
                .taxAmount(new BigDecimal("20.00"))
                .totalAmount(new BigDecimal("120.00"))
                .createdBy("admin")
                .build();
        invoiceId = invoiceRepository.save(invoice).getId();
    }

    @Test
    @WithMockUser(roles = "ACCOUNTANT")
    void getInvoices_ShouldReturnList_WhenUserIsAccountant() throws Exception {
        mockMvc.perform(get("/api/accounting/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].number").value("INV-001"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getInvoices_ShouldReturnForbidden_WhenUserIsCustomer() throws Exception {
        mockMvc.perform(get("/api/accounting/invoices"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getInvoices_ShouldReturnUnauthorized_WhenUserIsAnonymous() throws Exception {
        mockMvc.perform(get("/api/accounting/invoices"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getInvoiceById_ShouldReturnInvoice_WhenUserIsAdmin() throws Exception {
        mockMvc.perform(get("/api/accounting/invoices/{id}", invoiceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("INV-001"));
    }
}
