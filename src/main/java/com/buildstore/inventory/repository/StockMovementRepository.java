package com.buildstore.inventory.repository;

import com.buildstore.inventory.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    Optional<StockMovement> findByReferenceTypeAndReferenceId(String referenceType, Long referenceId);
}
