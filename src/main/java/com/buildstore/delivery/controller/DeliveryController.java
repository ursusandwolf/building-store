package com.buildstore.delivery.controller;

import com.buildstore.delivery.dto.ShipmentRequest;
import com.buildstore.delivery.model.Shipment;
import com.buildstore.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_WAREHOUSE_MANAGER', 'ROLE_ADMIN')")
    public Shipment createShipment(@RequestBody ShipmentRequest request) {
        return deliveryService.createShipment(request);
    }
}
