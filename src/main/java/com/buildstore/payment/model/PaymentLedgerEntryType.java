package com.buildstore.payment.model;

public enum PaymentLedgerEntryType {
    PAYMENT_RECEIVED,
    PAYMENT_CANCELLED,
    REFUND_ISSUED,
    MANUAL_ADJUSTMENT,
    CREDIT_LIMIT_USED,
    CREDIT_LIMIT_REPAID
}
