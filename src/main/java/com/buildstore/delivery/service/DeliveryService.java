package com.buildstore.delivery.service;

import com.buildstore.delivery.model.Shipment;
import com.buildstore.delivery.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final ShipmentRepository shipmentRepository;

    @Transactional
    public Shipment saveShipment(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }
}
