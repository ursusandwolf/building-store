package com.buildstore.warehouse.dto;

import java.time.Instant;

public record WarehouseResponse(
    Long id,
    String code,
    String name,
    String address,
    boolean active,
    Instant createdAt,
    Instant updatedAt
) {}
