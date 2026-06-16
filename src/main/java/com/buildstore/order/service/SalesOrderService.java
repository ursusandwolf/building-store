package com.buildstore.order.service;

import com.buildstore.common.exception.ResourceNotFoundException;
import com.buildstore.inventory.service.ReservationService;
import com.buildstore.order.dto.SalesOrderRequest;
import com.buildstore.order.dto.SalesOrderResponse;
import com.buildstore.order.mapper.SalesOrderMapper;
import com.buildstore.order.model.SalesOrder;
import com.buildstore.order.model.SalesOrderLine;
import com.buildstore.order.model.SalesOrderStatus;
import com.buildstore.order.repository.SalesOrderRepository;
import com.buildstore.pricing.service.PriceListService;
import com.buildstore.product.repository.ProductRepository;
import com.buildstore.user.repository.UserRepository;
import com.buildstore.warehouse.repository.WarehouseRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final ProductRepository productRepository;
    private final PriceListService priceListService;
    private final UserRepository userRepository;
    private final SalesOrderMapper salesOrderMapper;
    private final ReservationService reservationService;
    private final WarehouseRepository warehouseRepository;

    public SalesOrderService(SalesOrderRepository salesOrderRepository,
                            ProductRepository productRepository,
                            PriceListService priceListService,
                            UserRepository userRepository,
                            SalesOrderMapper salesOrderMapper,
                            ReservationService reservationService,
                            WarehouseRepository warehouseRepository) {
        this.salesOrderRepository = salesOrderRepository;
        this.productRepository = productRepository;
        this.priceListService = priceListService;
        this.userRepository = userRepository;
        this.salesOrderMapper = salesOrderMapper;
        this.reservationService = reservationService;
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional
    public SalesOrderResponse createOrder(SalesOrderRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        var customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        SalesOrder order = new SalesOrder();
        order.setCustomer(customer);
        order.setStatus(SalesOrderStatus.DRAFT);
        order.setTotalAmount(BigDecimal.ZERO);
        order = salesOrderRepository.save(order);

        AtomicReference<BigDecimal> total = new AtomicReference<>(BigDecimal.ZERO);
        final SalesOrder finalOrder = order;
        List<SalesOrderLine> lines = request.lines().stream().map(lineRequest -> {
            var product = productRepository.findById(lineRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            var warehouse = warehouseRepository.findById(lineRequest.warehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
            BigDecimal price = priceListService.getActivePrice(product.getId()).price();
            
            total.set(total.get().add(price.multiply(lineRequest.quantity())));
            
            SalesOrderLine line = new SalesOrderLine();
            line.setSalesOrder(finalOrder);
            line.setProduct(product);
            line.setProductNameSnapshot(product.getName());
            line.setPriceAtOrder(price);
            line.setQuantity(lineRequest.quantity());
            line.setUnit(product.getBaseUnit());
            line.setWarehouse(warehouse);
            
            reservationService.createReservation(finalOrder, line, product, warehouse, lineRequest.quantity());
            
            return line;
        }).toList();

        order.setLines(new ArrayList<>(lines));
        order.setTotalAmount(total.get());
        return salesOrderMapper.toResponse(salesOrderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<SalesOrderResponse> getMyOrders() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return salesOrderRepository.findByCustomerEmail(email).stream()
                .map(salesOrderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SalesOrderResponse getMyOrder(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return salesOrderRepository.findByIdAndCustomerEmail(id, email)
                .map(salesOrderMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found or access denied"));
    }
}
