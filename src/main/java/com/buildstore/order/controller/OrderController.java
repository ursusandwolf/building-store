package com.buildstore.order.controller;

import com.buildstore.order.dto.SalesOrderRequest;
import com.buildstore.order.dto.SalesOrderResponse;
import com.buildstore.order.service.SalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final SalesOrderService salesOrderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CUSTOMER')")
    public SalesOrderResponse createOrder(@Valid @RequestBody SalesOrderRequest request) {
        return salesOrderService.createOrder(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<SalesOrderResponse> getMyOrders() {
        return salesOrderService.getMyOrders();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public SalesOrderResponse getMyOrder(@PathVariable Long id) {
        return salesOrderService.getMyOrder(id);
    }
}
