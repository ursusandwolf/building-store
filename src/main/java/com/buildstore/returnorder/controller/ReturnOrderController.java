package com.buildstore.returnorder.controller;

import com.buildstore.order.model.SalesOrderLine;
import com.buildstore.order.repository.SalesOrderLineRepository;
import com.buildstore.returnorder.dto.ReturnOrderLineRequest;
import com.buildstore.returnorder.dto.ReturnOrderRequest;
import com.buildstore.returnorder.model.ReturnOrder;
import com.buildstore.returnorder.model.ReturnOrderLine;
import com.buildstore.returnorder.service.ReturnOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/return-orders")
@RequiredArgsConstructor
public class ReturnOrderController {

    private final ReturnOrderService returnOrderService;
    private final SalesOrderLineRepository salesOrderLineRepository;

    @PostMapping
    public ResponseEntity<Long> initiateReturn(@RequestBody ReturnOrderRequest request) {
        List<ReturnOrderLine> lines = request.lines().stream()
                .map(req -> {
                    SalesOrderLine sol = salesOrderLineRepository.findById(req.salesOrderLineId())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid Sales Order Line ID"));
                    return ReturnOrderLine.builder()
                            .salesOrderLine(sol)
                            .quantity(req.quantity())
                            .reason(req.reason())
                            .build();
                })
                .collect(Collectors.toList());

        ReturnOrder ro = returnOrderService.initiateReturnOrder(request.salesOrderId(), lines);
        return ResponseEntity.ok(ro.getId());
    }
}
