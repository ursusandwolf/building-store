package com.buildstore.supplier.dto;

public record SupplierResponse(
    Long id,
    String name,
    boolean active
) {}
