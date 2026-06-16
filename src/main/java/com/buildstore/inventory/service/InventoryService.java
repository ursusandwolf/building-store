package com.buildstore.inventory.service;

import com.buildstore.common.exception.ResourceNotFoundException;
import com.buildstore.inventory.dto.StockAdjustmentRequest;
import com.buildstore.inventory.model.StockItem;
import com.buildstore.inventory.model.StockMovement;
import com.buildstore.inventory.repository.StockItemRepository;
import com.buildstore.inventory.repository.StockMovementRepository;
import com.buildstore.product.model.Product;
import com.buildstore.product.repository.ProductRepository;
import com.buildstore.warehouse.model.Warehouse;
import com.buildstore.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final StockItemRepository stockItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Transactional
    public void adjustStock(StockAdjustmentRequest request) {
        log.info("Adjusting stock for product {}, warehouse {}, type {}", request.productId(), request.warehouseId(), request.type());

        // Idempotency check could be added here if needed, 
        // for now, we'll focus on the adjustment logic.

        StockItem stockItem = stockItemRepository.findByProductIdAndWarehouseId(request.productId(), request.warehouseId())
                .orElseGet(() -> {
                    Product product = productRepository.findById(request.productId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
                    Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                            .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
                    return StockItem.builder().product(product).warehouse(warehouse).build();
                });

        BigDecimal change = request.quantity();
        BigDecimal newBalance = stockItem.getAvailableQuantity().add(change);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient stock. Resulting balance would be negative.");
        }

        stockItem.setAvailableQuantity(newBalance);
        stockItemRepository.save(stockItem);

        StockMovement movement = StockMovement.builder()
                .product(stockItem.getProduct())
                .warehouse(stockItem.getWarehouse())
                .type(request.type())
                .quantity(change)
                .unit(stockItem.getProduct().getBaseUnit())
                .balanceAfter(newBalance)
                .referenceType("IDEMPOTENCY_KEY")
                .referenceId(0L) // Simplified for now, should map to request.idempotencyKey()
                .reason(request.reason())
                .build();
        
        stockMovementRepository.save(movement);
    }
}
