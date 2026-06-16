package com.buildstore.inventory.controller;

import com.buildstore.inventory.dto.StockAdjustmentRequest;
import com.buildstore.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/stock-adjustments")
public class AdminInventoryController {

    private final InventoryService inventoryService;

    public AdminInventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public void adjustStock(@Valid @RequestBody StockAdjustmentRequest request) {
        inventoryService.adjustStock(request);
    }
}
