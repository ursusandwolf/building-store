package com.buildstore.returnorder;

import com.buildstore.order.model.SalesOrder;
import com.buildstore.order.model.SalesOrderStatus;
import com.buildstore.order.repository.SalesOrderRepository;
import com.buildstore.returnorder.dto.ReturnOrderLineRequest;
import com.buildstore.returnorder.dto.ReturnOrderRequest;
import com.buildstore.user.model.AppUser;
import com.buildstore.user.model.UserStatus;
import com.buildstore.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public class ReturnOrderTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testInitiateReturn() {
        String uniqueEmail = "customer-" + java.util.UUID.randomUUID() + "@test.com";
        AppUser user = AppUser.builder()
                .email(uniqueEmail)
                .passwordHash("pass")
                .status(UserStatus.ACTIVE)
                .roles(Set.of())
                .build();
        userRepository.save(user);

        SalesOrder order = new SalesOrder();
        order.setCustomer(user);
        order.setStatus(SalesOrderStatus.SHIPPED);
        order.setCurrency("USD");
        order.setTotalAmount(BigDecimal.TEN);
        order.setCreatedBy("system");
        order.setCreatedAt(java.time.Instant.now());
        salesOrderRepository.save(order);

        ReturnOrderRequest request = new ReturnOrderRequest(order.getId(), List.of(
                new ReturnOrderLineRequest(1L, BigDecimal.ONE, "Defective")
        ));
    }
}
