package com.buildstore.returnorder.service;

import com.buildstore.order.model.SalesOrder;
import com.buildstore.order.repository.SalesOrderRepository;
import com.buildstore.returnorder.model.ReturnOrder;
import com.buildstore.returnorder.model.ReturnOrderLine;
import com.buildstore.returnorder.model.ReturnOrderStatus;
import com.buildstore.returnorder.repository.ReturnOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReturnOrderService {

    private final ReturnOrderRepository returnOrderRepository;
    private final SalesOrderRepository salesOrderRepository;

    @Transactional
    public ReturnOrder initiateReturnOrder(Long salesOrderId, List<ReturnOrderLine> lines) {
        SalesOrder salesOrder = salesOrderRepository.findById(salesOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Sales order not found"));

        ReturnOrder returnOrder = ReturnOrder.builder()
                .salesOrder(salesOrder)
                .status(ReturnOrderStatus.INITIATED)
                .build();

        for (ReturnOrderLine line : lines) {
            line.setReturnOrder(returnOrder);
        }
        returnOrder.setLines(lines);

        return returnOrderRepository.save(returnOrder);
    }
}
