package com.buildstore.report.service;

import com.buildstore.inventory.repository.StockItemRepository;
import com.buildstore.order.repository.SalesOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final SalesOrderRepository salesOrderRepository;
    private final StockItemRepository stockItemRepository;

    public Map<String, BigDecimal> getSalesVolumeReport() {
        return salesOrderRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCreatedAt().toString().substring(0, 10), // Simple date grouping
                        Collectors.reducing(BigDecimal.ZERO, order -> order.getTotalAmount(), BigDecimal::add)
                ));
    }

    public Map<String, BigDecimal> getInventoryReport() {
        return stockItemRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        item -> item.getProduct().getName(),
                        Collectors.reducing(BigDecimal.ZERO, item -> item.getQuantity(), BigDecimal::add)
                ));
    }
}
