package com.buildstore.purchase.mapper;

import com.buildstore.purchase.dto.PurchaseOrderLineResponse;
import com.buildstore.purchase.dto.PurchaseOrderRequest;
import com.buildstore.purchase.dto.PurchaseOrderResponse;
import com.buildstore.purchase.model.PurchaseOrder;
import com.buildstore.purchase.model.PurchaseOrderLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PurchaseOrderMapper {

    @Mapping(source = "supplier.id", target = "supplierId")
    @Mapping(source = "supplier.name", target = "supplierName")
    PurchaseOrderResponse toResponse(PurchaseOrder order);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    PurchaseOrderLineResponse toLineResponse(PurchaseOrderLine line);
}
