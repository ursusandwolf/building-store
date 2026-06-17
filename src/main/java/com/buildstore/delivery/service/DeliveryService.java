package com.buildstore.delivery.service;

import com.buildstore.audit.service.AuditService;
import com.buildstore.common.exception.ResourceNotFoundException;
import com.buildstore.delivery.dto.ShipmentRequest;
import com.buildstore.delivery.model.*;
import com.buildstore.delivery.repository.ShipmentRepository;
import com.buildstore.inventory.repository.StockReservationRepository;
import com.buildstore.inventory.service.ReservationService;
import com.buildstore.order.repository.SalesOrderRepository;
import com.buildstore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final ShipmentRepository shipmentRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final ReservationService reservationService;
    private final StockReservationRepository reservationRepository;
    private final AuditService auditService;
    private final UserRepository userRepository;

    @Transactional
    public Shipment createShipment(ShipmentRequest request) {
        var order = salesOrderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        Shipment shipment = new Shipment();
        shipment.setOrderId(order.getId());
        shipment.setWarehouseId(request.warehouseId());
        shipment.setStatus(ShipmentStatus.DRAFT);
        shipment.setDeliveryType(request.deliveryType());
        shipment.setDeliveryAddress(request.deliveryAddress());
        shipment.setScheduledAt(Instant.now());
        shipment = shipmentRepository.save(shipment);
        
        final Shipment finalShipment = shipment;
        
        List<ShipmentLine> lines = request.lines().stream().map(lineReq -> {
            var reservation = reservationRepository.findByOrderLineId(lineReq.orderLineId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
            
            reservationService.consumeReservation(reservation.getId(), finalShipment.getId());
            
            ShipmentLine line = new ShipmentLine();
            line.setShipment(finalShipment);
            line.setOrderLineId(lineReq.orderLineId());
            line.setQuantity(lineReq.quantity());
            line.setUnit(lineReq.unit());
            return line;
        }).collect(Collectors.toList());
        
        shipment.setLines(lines);
        shipment.setStatus(ShipmentStatus.READY_FOR_PICKUP);
        
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        var actor = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        auditService.logEvent(com.buildstore.audit.model.AuditEvent.builder()
                .actorType("USER")
                .actorId(actor.getId())
                .action("SHIPMENT_CREATED")
                .subjectType("SHIPMENT")
                .subjectId(finalShipment.getId())
                .reason("Shipment created for order " + order.getId())
                .build());

        return shipmentRepository.save(shipment);
    }
}
