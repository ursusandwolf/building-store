package com.buildstore.pricing.service;

import com.buildstore.pricing.dto.DiscountRequest;
import com.buildstore.pricing.model.Discount;
import com.buildstore.pricing.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;

    @Transactional
    public void createDiscount(DiscountRequest request) {
        Discount discount = Discount.builder()
                .name(request.name())
                .percentage(request.percentage())
                .priority(request.priority())
                .compatible(request.compatible())
                // product assignment handled here if needed
                .build();
        discountRepository.save(discount);
    }

    @Transactional(readOnly = true)
    public BigDecimal applyDiscounts(Long productId, BigDecimal basePrice) {
        List<Discount> activeDiscounts = discountRepository.findByActiveTrueAndProductIsNullOrProductId(productId);
        
        // Sorting by priority (highest first)
        activeDiscounts.sort(Comparator.comparing(Discount::getPriority).reversed());

        BigDecimal finalPrice = basePrice;
        for (Discount discount : activeDiscounts) {
            BigDecimal reduction = finalPrice.multiply(discount.getPercentage()).divide(new BigDecimal("100"));
            finalPrice = finalPrice.subtract(reduction);
            
            // Non-compatible logic: if not compatible, this was the only discount allowed
            if (!discount.isCompatible()) {
                break;
            }
        }
        
        return finalPrice.max(BigDecimal.ZERO);
    }
}
