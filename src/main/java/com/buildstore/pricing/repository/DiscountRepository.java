package com.buildstore.pricing.repository;

import com.buildstore.pricing.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findByActiveTrueAndProductIsNullOrProductId(Long productId);
}
