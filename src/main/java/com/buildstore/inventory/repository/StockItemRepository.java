package com.buildstore.inventory.repository;

import com.buildstore.inventory.model.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface StockItemRepository extends JpaRepository<StockItem, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<StockItem> findByProductIdAndWarehouseId(Long productId, Long warehouseId);
}
