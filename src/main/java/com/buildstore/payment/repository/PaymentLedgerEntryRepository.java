package com.buildstore.payment.repository;

import com.buildstore.payment.model.PaymentLedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentLedgerEntryRepository extends JpaRepository<PaymentLedgerEntry, Long> {
}
