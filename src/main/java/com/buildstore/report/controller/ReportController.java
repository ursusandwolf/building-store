package com.buildstore.report.controller;

import com.buildstore.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sales")
    public ResponseEntity<Map<String, BigDecimal>> getSalesReport() {
        return ResponseEntity.ok(reportService.getSalesVolumeReport());
    }

    @GetMapping("/inventory")
    public ResponseEntity<Map<String, BigDecimal>> getInventoryReport() {
        return ResponseEntity.ok(reportService.getInventoryReport());
    }
}
