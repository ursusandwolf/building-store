package com.buildstore.pricing.controller;

import com.buildstore.pricing.dto.PriceResponse;
import com.buildstore.pricing.service.DiscountService;
import com.buildstore.pricing.repository.PriceListItemRepository;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalog/discounts")
public class CatalogDiscountController {

    private final DiscountService discountService;
    private final PriceListItemRepository priceListItemRepository;

    public CatalogDiscountController(DiscountService discountService, PriceListItemRepository priceListItemRepository) {
        this.discountService = discountService;
        this.priceListItemRepository = priceListItemRepository;
    }

    @GetMapping("/preview/{productId}")
    public PriceResponse previewDiscount(@PathVariable Long productId) {
        // Find base price first, then apply discounts
        BigDecimal basePrice = priceListItemRepository.findByPriceListActiveTrueAndPriceListStartDateBeforeAndPriceListEndDateAfterOrPriceListEndDateIsNullAndProductId(
                java.time.Instant.now(), java.time.Instant.now(), productId
        ).orElseThrow().getPrice();
        
        return new PriceResponse(discountService.applyDiscounts(productId, basePrice));
    }
}
