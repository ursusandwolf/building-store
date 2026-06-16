package com.buildstore.supplier.controller;

import com.buildstore.supplier.dto.SupplierRequest;
import com.buildstore.supplier.dto.SupplierResponse;
import com.buildstore.supplier.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/suppliers")
public class AdminSupplierController {

    private final SupplierService supplierService;

    public AdminSupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public SupplierResponse createSupplier(@Valid @RequestBody SupplierRequest request) {
        return supplierService.createSupplier(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASING_MANAGER')")
    public List<SupplierResponse> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }
}
