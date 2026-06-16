package com.buildstore.pricing.service;

import com.buildstore.pricing.dto.DiscountRequest;
import com.buildstore.pricing.model.Discount;
import com.buildstore.pricing.repository.DiscountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class DiscountService {

    private final DiscountRepository discountRepository;

    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    @Transactional
    public void createDiscount(DiscountRequest request) {
        Discount discount = new Discount();
        discount.setName(request.name());
        discount.setPercentage(request.percentage());
        discount.setPriority(request.priority());
        discount.setCompatible(request.compatible());
        discount.setActive(true);
        discountRepository.save(discount);
    }

    @Transactional(readOnly = true)
    public BigDecimal applyDiscounts(Long productId, BigDecimal basePrice) {
        List<Discount> activeDiscounts = discountRepository.findByActiveTrueAndProductIsNullOrProductId(productId);
        
        // Sorting by priority (highest first)
        activeDiscounts.sort(Comparator.comparing(Discount::getPriority).reversed());

        BigDecimal finalPrice = basePrice;
        for (Discount discount : activeDiscounts) {
            BigDecimal reduction = finalPrice.multiply(discount.getPercentage()).divide(new BigDecimal("100"), 4, java.math.RoundingMode.HALF_UP);
            finalPrice = finalPrice.subtract(reduction);
            
            // Non-compatible logic: if not compatible, this was the only discount allowed
            if (!discount.isCompatible()) {
                break;
            }
        }
        
        return finalPrice.max(BigDecimal.ZERO);
    }
}
