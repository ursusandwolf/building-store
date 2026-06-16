package com.buildstore.pricing.controller;

import com.buildstore.pricing.dto.PriceListRequest;
import com.buildstore.pricing.dto.PriceListItemRequest;
import com.buildstore.pricing.dto.PriceResponse;
import com.buildstore.pricing.service.PriceListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PriceListController {

    private final PriceListService priceListService;

    @PostMapping("/api/admin/price-lists")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Long createPriceList(@Valid @RequestBody PriceListRequest request) {
        return priceListService.createPriceList(request);
    }

    @PostMapping("/api/admin/price-lists/{id}/items")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public void addItem(@PathVariable Long id, @Valid @RequestBody PriceListItemRequest request) {
        priceListService.addItem(id, request);
    }

    @GetMapping("/api/catalog/products/{id}/price")
    public PriceResponse getActivePrice(@PathVariable Long id) {
        return priceListService.getActivePrice(id);
    }
}
