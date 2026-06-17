package com.buildstore.order.service;

import com.buildstore.accounting.service.AccountingService;
import com.buildstore.audit.model.AuditEvent;
import com.buildstore.audit.service.AuditService;
import com.buildstore.common.exception.ResourceNotFoundException;
import com.buildstore.inventory.service.ReservationService;
import com.buildstore.order.dto.SalesOrderRequest;
import com.buildstore.order.dto.SalesOrderResponse;
import com.buildstore.order.mapper.SalesOrderMapper;
import com.buildstore.order.model.SalesOrder;
import com.buildstore.order.model.SalesOrderLine;
import com.buildstore.order.model.SalesOrderStatus;
import com.buildstore.order.repository.SalesOrderLineRepository;
import com.buildstore.order.repository.SalesOrderRepository;
import com.buildstore.pricing.service.PriceListService;
import com.buildstore.product.repository.ProductRepository;
import com.buildstore.security.service.SystemUserProvider;
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
    private final SalesOrderLineRepository salesOrderLineRepository;
    private final ProductRepository productRepository;
    private final PriceListService priceListService;
    private final UserRepository userRepository;
    private final SalesOrderMapper salesOrderMapper;
    private final ReservationService reservationService;
    private final WarehouseRepository warehouseRepository;
    private final AuditService auditService;
    private final AccountingService accountingService;
    private final SystemUserProvider systemUserProvider;

    public SalesOrderService(SalesOrderRepository salesOrderRepository,
                            SalesOrderLineRepository salesOrderLineRepository,
                            ProductRepository productRepository,
                            PriceListService priceListService,
                            UserRepository userRepository,
                            SalesOrderMapper salesOrderMapper,
                            ReservationService reservationService,
                            WarehouseRepository warehouseRepository,
                            AuditService auditService,
                            AccountingService accountingService,
                            SystemUserProvider systemUserProvider) {
        this.salesOrderRepository = salesOrderRepository;
        this.salesOrderLineRepository = salesOrderLineRepository;
        this.productRepository = productRepository;
        this.priceListService = priceListService;
        this.userRepository = userRepository;
        this.salesOrderMapper = salesOrderMapper;
        this.reservationService = reservationService;
        this.warehouseRepository = warehouseRepository;
        this.auditService = auditService;
        this.accountingService = accountingService;
        this.systemUserProvider = systemUserProvider;
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
            
            // Persist line explicitly
            line = salesOrderLineRepository.save(line);
            
            reservationService.createReservation(finalOrder, line, product, warehouse, lineRequest.quantity());
            
            return line;
        }).toList();

        order.setLines(new ArrayList<>(lines));
        order.setTotalAmount(total.get());
        SalesOrder savedOrder = salesOrderRepository.save(order);
        
        auditService.logEvent(AuditEvent.builder()
                .actorType("CUSTOMER")
                .actorId(customer.getId())
                .action("SALES_ORDER_CREATED")
                .subjectType("SALES_ORDER")
                .subjectId(savedOrder.getId())
                .reason("Order created via API")
                .build());

        return salesOrderMapper.toResponse(savedOrder);
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

    @Transactional
    public void confirmOrder(Long orderId) {
        SalesOrder order = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        order.setStatus(SalesOrderStatus.CONFIRMED);
        salesOrderRepository.save(order);
        
        // Create Invoice
        accountingService.createDraftInvoice(order);
        
        // Record Accounting Entry for Sale
        accountingService.recordEntry(
                com.buildstore.accounting.model.AccountingEntryType.SALE_REVENUE,
                "ACCOUNTS_RECEIVABLE",
                "SALES_REVENUE",
                order.getTotalAmount(),
                order.getCurrency(),
                "SALES_ORDER",
                java.util.UUID.nameUUIDFromBytes(order.getId().toString().getBytes()),
                "Sale of goods for order " + order.getId(),
                systemUserProvider.getSystemUser().getEmail()
        );
        
        auditService.logEvent(AuditEvent.builder()
                .actorType("USER")
                .actorId(systemUserProvider.getSystemUser().getId())
                .action("SALES_ORDER_CONFIRMED")
                .subjectType("SALES_ORDER")
                .subjectId(order.getId())
                .reason("Order confirmed via API")
                .build());
    }
}
