package com.buildstore.purchase.service;

import com.buildstore.common.exception.ResourceNotFoundException;
import com.buildstore.inventory.model.StockMovementType;
import com.buildstore.inventory.service.InventoryService;
import com.buildstore.inventory.dto.StockAdjustmentRequest;
import com.buildstore.inventory.repository.StockMovementRepository;
import com.buildstore.purchase.dto.GoodsReceiptRequest;
import com.buildstore.purchase.model.PurchaseOrder;
import com.buildstore.purchase.model.PurchaseOrderLine;
import com.buildstore.purchase.model.PurchaseOrderStatus;
import com.buildstore.purchase.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoodsReceiptService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final InventoryService inventoryService;
    private final StockMovementRepository stockMovementRepository;

    public GoodsReceiptService(PurchaseOrderRepository purchaseOrderRepository, 
                               InventoryService inventoryService,
                               StockMovementRepository stockMovementRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.inventoryService = inventoryService;
        this.stockMovementRepository = stockMovementRepository;
    }

    @Transactional
    public void processGoodsReceipt(Long orderId, GoodsReceiptRequest request) {
        if (stockMovementRepository.findByReferenceTypeAndReferenceId("GOODS_RECEIPT", Long.valueOf(request.idempotencyKey().hashCode())).isPresent()) {
            return; // Already processed
        }

        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));

        if (order.getStatus() == PurchaseOrderStatus.COMPLETED || order.getStatus() == PurchaseOrderStatus.CANCELLED) {
            throw new IllegalStateException("Purchase order is already closed");
        }

        for (var lineRequest : request.lines()) {
            PurchaseOrderLine line = order.getLines().stream()
                    .filter(l -> l.getProduct().getId().equals(lineRequest.productId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Product not in purchase order"));

            inventoryService.adjustStock(new StockAdjustmentRequest(
                    line.getProduct().getId(),
                    1L, // Assuming a default warehouse
                    StockMovementType.PURCHASE_RECEIPT,
                    lineRequest.quantity(),
                    "Receipt for PO " + orderId,
                    request.idempotencyKey()
            ));
        }

        order.setStatus(PurchaseOrderStatus.COMPLETED);
        purchaseOrderRepository.save(order);
    }
}
