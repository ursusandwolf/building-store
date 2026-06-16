package com.buildstore.pricing.service;

import com.buildstore.common.exception.ResourceNotFoundException;
import com.buildstore.pricing.dto.PriceListRequest;
import com.buildstore.pricing.dto.PriceListItemRequest;
import com.buildstore.pricing.dto.PriceResponse;
import com.buildstore.pricing.model.PriceList;
import com.buildstore.pricing.model.PriceListItem;
import com.buildstore.pricing.repository.PriceListItemRepository;
import com.buildstore.pricing.repository.PriceListRepository;
import com.buildstore.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PriceListService {

    private final PriceListRepository priceListRepository;
    private final PriceListItemRepository priceListItemRepository;
    private final ProductRepository productRepository;
    private final DiscountService discountService;

    @Transactional
    public Long createPriceList(PriceListRequest request) {
        PriceList priceList = PriceList.builder()
                .name(request.name())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .active(true)
                .build();
        return priceListRepository.save(priceList).getId();
    }

    @Transactional
    public void addItem(Long priceListId, PriceListItemRequest request) {
        PriceList priceList = priceListRepository.findById(priceListId)
                .orElseThrow(() -> new ResourceNotFoundException("Price list not found"));
        var product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        PriceListItem item = PriceListItem.builder()
                .priceList(priceList)
                .product(product)
                .price(request.price())
                .build();
        priceListItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public PriceResponse getActivePrice(Long productId) {
        Instant now = Instant.now();
        PriceListItem item = priceListItemRepository
                .findByPriceListActiveTrueAndPriceListStartDateBeforeAndPriceListEndDateAfterOrPriceListEndDateIsNullAndProductId(
                        now, now, productId
                )
                .orElseThrow(() -> new ResourceNotFoundException("No active price found for product"));
        
        BigDecimal discountedPrice = discountService.applyDiscounts(productId, item.getPrice());
        return new PriceResponse(discountedPrice);
    }
}
