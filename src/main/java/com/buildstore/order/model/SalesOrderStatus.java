package com.buildstore.order.model;

public enum SalesOrderStatus {
    DRAFT,
    PENDING_CONFIRMATION,
    CONFIRMED,
    RESERVED,
    PARTIALLY_PAID,
    PAID,
    PARTIALLY_SHIPPED,
    SHIPPED,
    CANCELLED,
    RETURNED
}
