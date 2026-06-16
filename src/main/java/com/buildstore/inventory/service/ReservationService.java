package com.buildstore.inventory.service;

import com.buildstore.inventory.model.ReservationStatus;
import com.buildstore.inventory.model.StockReservation;
import com.buildstore.inventory.repository.StockReservationRepository;
import com.buildstore.order.model.SalesOrder;
import com.buildstore.order.model.SalesOrderLine;
import com.buildstore.product.model.Product;
import com.buildstore.warehouse.model.Warehouse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class ReservationService {

    private final StockReservationRepository reservationRepository;
    private final InventoryService inventoryService;

    public ReservationService(StockReservationRepository reservationRepository,
                              InventoryService inventoryService) {
        this.reservationRepository = reservationRepository;
        this.inventoryService = inventoryService;
    }

    @Transactional
    public void createReservation(SalesOrder order, SalesOrderLine line, Product product, Warehouse warehouse, BigDecimal quantity) {
        inventoryService.reserveStock(product.getId(), warehouse.getId(), quantity);

        StockReservation reservation = new StockReservation();
        reservation.setOrder(order);
        reservation.setOrderLine(line);
        reservation.setProduct(product);
        reservation.setWarehouse(warehouse);
        reservation.setQuantity(quantity);
        reservation.setUnit(line.getUnit());
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setReservedAt(Instant.now());
        reservationRepository.save(reservation);
    }
}
