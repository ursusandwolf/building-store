package com.buildstore.purchase.controller;

import com.buildstore.purchase.dto.GoodsReceiptRequest;
import com.buildstore.purchase.service.GoodsReceiptService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/purchase-orders")
public class AdminGoodsReceiptController {

    private final GoodsReceiptService goodsReceiptService;

    public AdminGoodsReceiptController(GoodsReceiptService goodsReceiptService) {
        this.goodsReceiptService = goodsReceiptService;
    }

    @PostMapping("/{id}/goods-receipts")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public void processGoodsReceipt(@PathVariable Long id, @Valid @RequestBody GoodsReceiptRequest request) {
        goodsReceiptService.processGoodsReceipt(id, request);
    }
}
