package com.buildstore.delivery.controller;

import com.buildstore.delivery.dto.ShipmentRequest;
import com.buildstore.delivery.model.Shipment;
import com.buildstore.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Shipment createShipment(@RequestBody ShipmentRequest request) {
        // Will implement logic
        return null; 
    }
}
