package com.buildstore.delivery;

import com.buildstore.delivery.dto.ShipmentLineRequest;
import com.buildstore.delivery.dto.ShipmentRequest;
import com.buildstore.delivery.model.DeliveryType;
import com.buildstore.delivery.repository.ShipmentRepository;
import com.buildstore.inventory.model.ReservationStatus;
import com.buildstore.inventory.model.StockItem;
import com.buildstore.inventory.model.StockReservation;
import com.buildstore.inventory.repository.StockItemRepository;
import com.buildstore.inventory.repository.StockReservationRepository;
import com.buildstore.order.model.SalesOrder;
import com.buildstore.order.model.SalesOrderLine;
import com.buildstore.order.model.SalesOrderStatus;
import com.buildstore.order.repository.SalesOrderLineRepository;
import com.buildstore.order.repository.SalesOrderRepository;
import com.buildstore.product.model.Product;
import com.buildstore.product.model.ProductStatus;
import com.buildstore.product.model.UnitOfMeasure;
import com.buildstore.product.repository.ProductRepository;
import com.buildstore.user.model.AppUser;
import com.buildstore.user.repository.UserRepository;
import com.buildstore.warehouse.model.Warehouse;
import com.buildstore.warehouse.repository.WarehouseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DeliveryTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private SalesOrderRepository salesOrderRepository;
    
    @Autowired
    private SalesOrderLineRepository salesOrderLineRepository;

    @Autowired
    private StockReservationRepository reservationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StockItemRepository stockItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private SalesOrder salesOrder;
    private Warehouse warehouse;
    private Product product;
    private SalesOrderLine orderLine;

    @BeforeEach
    void setUp() {
        AppUser user = new AppUser();
        user.setEmail("wh_manager@test.com");
        user.setPasswordHash("hash");
        user.setStatus(com.buildstore.user.model.UserStatus.ACTIVE);
        userRepository.save(user);

        product = new Product();
        product.setSku("SKU-1");
        product.setName("Product 1");
        product.setBaseUnit(UnitOfMeasure.PIECE);
        product.setStatus(ProductStatus.ACTIVE);
        productRepository.save(product);

        warehouse = new Warehouse();
        warehouse.setCode("WH-1");
        warehouse.setName("Warehouse 1");
        warehouseRepository.save(warehouse);
        
        StockItem stockItem = new StockItem();
        stockItem.setProduct(product);
        stockItem.setWarehouse(warehouse);
        stockItem.setAvailableQuantity(BigDecimal.TEN);
        stockItem.setReservedQuantity(BigDecimal.ONE);
        stockItemRepository.save(stockItem);

        salesOrder = new SalesOrder();
        salesOrder.setCustomer(user);
        salesOrder.setStatus(SalesOrderStatus.CONFIRMED);
        salesOrder.setCreatedBy("wh_manager@test.com");
        salesOrder = salesOrderRepository.save(salesOrder);

        orderLine = new SalesOrderLine();
        orderLine.setSalesOrder(salesOrder);
        orderLine.setProduct(product);
        orderLine.setProductNameSnapshot(product.getName());
        orderLine.setPriceAtOrder(BigDecimal.TEN);
        orderLine.setQuantity(BigDecimal.ONE);
        orderLine.setUnit(UnitOfMeasure.PIECE);
        orderLine.setWarehouse(warehouse);
        salesOrderLineRepository.save(orderLine);

        StockReservation reservation = new StockReservation();
        reservation.setOrder(salesOrder);
        reservation.setOrderLine(orderLine);
        reservation.setProduct(product);
        reservation.setWarehouse(warehouse);
        reservation.setQuantity(BigDecimal.ONE);
        reservation.setUnit(com.buildstore.product.model.UnitOfMeasure.PIECE);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setReservedAt(Instant.now());
        reservationRepository.save(reservation);
    }

    @Test
    @WithMockUser(username = "wh_manager@test.com", roles = {"WAREHOUSE_MANAGER"})
    void createShipment_shouldConsumeReservation() throws Exception {
        ShipmentRequest request = new ShipmentRequest(
                salesOrder.getId(),
                warehouse.getId(),
                DeliveryType.STORE_DELIVERY,
                "123 Main St",
                List.of(new ShipmentLineRequest(orderLine.getId(), BigDecimal.ONE, UnitOfMeasure.PIECE.name()))
        );

        mockMvc.perform(post("/api/deliveries")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
                
        assertTrue(shipmentRepository.findAll().size() > 0);
        
        StockReservation reservation = reservationRepository.findByOrderLineId(orderLine.getId()).orElseThrow();
        assertEquals(ReservationStatus.CONSUMED, reservation.getStatus());
    }
}
