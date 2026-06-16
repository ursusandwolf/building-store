package com.buildstore.warehouse.controller;

import com.buildstore.warehouse.dto.WarehouseRequest;
import com.buildstore.warehouse.dto.WarehouseResponse;
import com.buildstore.warehouse.service.WarehouseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/warehouses")
public class AdminWarehouseController {

    private final WarehouseService warehouseService;

    public AdminWarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public WarehouseResponse createWarehouse(@Valid @RequestBody WarehouseRequest request) {
        return warehouseService.createWarehouse(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public WarehouseResponse updateWarehouse(@PathVariable Long id, @Valid @RequestBody WarehouseRequest request) {
        return warehouseService.updateWarehouse(id, request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'AUDITOR')")
    public List<WarehouseResponse> getAllWarehouses() {
        return warehouseService.getAllWarehouses();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'AUDITOR')")
    public WarehouseResponse getWarehouse(@PathVariable Long id) {
        return warehouseService.getWarehouseById(id);
    }
}
