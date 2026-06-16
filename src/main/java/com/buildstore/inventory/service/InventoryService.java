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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);
    private final StockItemRepository stockItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public InventoryService(StockItemRepository stockItemRepository,
                            StockMovementRepository stockMovementRepository,
                            ProductRepository productRepository,
                            WarehouseRepository warehouseRepository) {
        this.stockItemRepository = stockItemRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional
    public void reserveStock(Long productId, Long warehouseId, BigDecimal quantity) {
        StockItem stockItem = stockItemRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock item not found"));

        if (stockItem.getAvailableQuantity().compareTo(quantity) < 0) {
            throw new IllegalArgumentException("Insufficient available stock");
        }

        stockItem.setAvailableQuantity(stockItem.getAvailableQuantity().subtract(quantity));
        stockItem.setReservedQuantity(stockItem.getReservedQuantity().add(quantity));
        stockItemRepository.save(stockItem);
    }

    @Transactional
    public void releaseStock(Long productId, Long warehouseId, BigDecimal quantity) {
        StockItem stockItem = stockItemRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock item not found"));

        if (stockItem.getReservedQuantity().compareTo(quantity) < 0) {
            throw new IllegalArgumentException("Insufficient reserved stock");
        }

        stockItem.setReservedQuantity(stockItem.getReservedQuantity().subtract(quantity));
        stockItem.setAvailableQuantity(stockItem.getAvailableQuantity().add(quantity));
        stockItemRepository.save(stockItem);
    }

    @Transactional
    public void adjustStock(StockAdjustmentRequest request) {
        log.info("Adjusting stock for product {}, warehouse {}, type {}", request.productId(), request.warehouseId(), request.type());

        StockItem stockItem = stockItemRepository.findByProductIdAndWarehouseId(request.productId(), request.warehouseId())
                .orElseGet(() -> {
                    Product product = productRepository.findById(request.productId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
                    Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                            .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
                    StockItem newItem = new StockItem();
                    newItem.setProduct(product);
                    newItem.setWarehouse(warehouse);
                    newItem.setAvailableQuantity(BigDecimal.ZERO);
                    newItem.setReservedQuantity(BigDecimal.ZERO);
                    newItem.setDamagedQuantity(BigDecimal.ZERO);
                    return newItem;
                });

        BigDecimal change = request.quantity();
        BigDecimal newBalance = stockItem.getAvailableQuantity().add(change);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient stock. Resulting balance would be negative.");
        }

        stockItem.setAvailableQuantity(newBalance);
        stockItemRepository.save(stockItem);

        StockMovement movement = new StockMovement();
        movement.setProduct(stockItem.getProduct());
        movement.setWarehouse(stockItem.getWarehouse());
        movement.setType(request.type());
        movement.setQuantity(change);
        movement.setUnit(stockItem.getProduct().getBaseUnit());
        movement.setBalanceAfter(newBalance);
        movement.setReferenceType("IDEMPOTENCY_KEY");
        movement.setReferenceId(0L); 
        movement.setReason(request.reason());
        
        stockMovementRepository.save(movement);
    }
}
