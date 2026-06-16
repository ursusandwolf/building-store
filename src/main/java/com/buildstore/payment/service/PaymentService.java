package com.buildstore.payment.service;

import com.buildstore.common.exception.ResourceNotFoundException;
import com.buildstore.order.model.SalesOrder;
import com.buildstore.order.repository.SalesOrderRepository;
import com.buildstore.payment.model.*;
import com.buildstore.payment.repository.PaymentLedgerEntryRepository;
import com.buildstore.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentLedgerEntryRepository ledgerRepository;
    private final SalesOrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          PaymentLedgerEntryRepository ledgerRepository,
                          SalesOrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.ledgerRepository = ledgerRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void processPayment(Long orderId, PaymentMethod method, BigDecimal amount, String idempotencyKey) {
        if (paymentRepository.findByIdempotencyKey(idempotencyKey).isPresent()) {
            throw new IllegalArgumentException("Payment with this idempotency key already processed");
        }

        SalesOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        Payment payment = new Payment();
        payment.setCustomer(order.getCustomer());
        payment.setOrder(order);
        payment.setStatus(PaymentStatus.CAPTURED);
        payment.setMethod(method);
        payment.setAmount(amount);
        payment.setCurrency(order.getCurrency());
        payment.setIdempotencyKey(idempotencyKey);
        payment = paymentRepository.save(payment);

        PaymentLedgerEntry entry = new PaymentLedgerEntry();
        entry.setCustomer(order.getCustomer());
        entry.setOrder(order);
        entry.setPayment(payment);
        entry.setType(PaymentLedgerEntryType.PAYMENT_RECEIVED);
        entry.setAmount(amount);
        entry.setCurrency(order.getCurrency());
        entry.setBalanceAfter(order.getTotalAmount().subtract(amount)); // Simplified
        entry.setCreatedBy("SYSTEM"); 
        ledgerRepository.save(entry);
    }
}
