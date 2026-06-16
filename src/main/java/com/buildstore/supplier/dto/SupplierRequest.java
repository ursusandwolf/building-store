package com.buildstore.supplier.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierRequest(
    @NotBlank(message = "Supplier name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    String name,

    Boolean active
) {}
