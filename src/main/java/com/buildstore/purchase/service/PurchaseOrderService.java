package com.buildstore.purchase.service;

import com.buildstore.common.exception.ResourceNotFoundException;
import com.buildstore.product.model.Product;
import com.buildstore.product.repository.ProductRepository;
import com.buildstore.purchase.dto.PurchaseOrderLineRequest;
import com.buildstore.purchase.dto.PurchaseOrderRequest;
import com.buildstore.purchase.dto.PurchaseOrderResponse;
import com.buildstore.purchase.mapper.PurchaseOrderMapper;
import com.buildstore.purchase.model.PurchaseOrder;
import com.buildstore.purchase.model.PurchaseOrderLine;
import com.buildstore.purchase.model.PurchaseOrderStatus;
import com.buildstore.purchase.repository.PurchaseOrderRepository;
import com.buildstore.supplier.model.Supplier;
import com.buildstore.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;

    @Transactional
    public PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request) {
        Supplier supplier = supplierRepository.findById(request.supplierId())
                .filter(Supplier::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Active supplier not found with ID: " + request.supplierId()));

        PurchaseOrder order = PurchaseOrder.builder()
                .supplier(supplier)
                .status(PurchaseOrderStatus.DRAFT)
                .build();

        List<PurchaseOrderLine> lines = request.lines().stream().map(lineRequest -> {
            Product product = productRepository.findById(lineRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + lineRequest.productId()));
            return PurchaseOrderLine.builder()
                    .purchaseOrder(order)
                    .product(product)
                    .quantity(lineRequest.quantity())
                    .unitCost(lineRequest.unitCost())
                    .build();
        }).toList();

        order.setLines(lines);
        return purchaseOrderMapper.toResponse(purchaseOrderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrderResponse> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll().stream()
                .map(purchaseOrderMapper::toResponse)
                .toList();
    }
}
