package com.buildstore.pricing.controller;

import com.buildstore.pricing.dto.DiscountRequest;
import com.buildstore.pricing.service.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/discounts")
@RequiredArgsConstructor
public class AdminDiscountController {

    private final DiscountService discountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public void createDiscount(@Valid @RequestBody DiscountRequest request) {
        discountService.createDiscount(request);
    }
}
