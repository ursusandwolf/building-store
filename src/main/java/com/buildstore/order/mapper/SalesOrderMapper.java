package com.buildstore.order.mapper;

import com.buildstore.order.dto.SalesOrderLineResponse;
import com.buildstore.order.dto.SalesOrderResponse;
import com.buildstore.order.model.SalesOrder;
import com.buildstore.order.model.SalesOrderLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SalesOrderMapper {

    SalesOrderResponse toResponse(SalesOrder order);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "productNameSnapshot", target = "productName")
    SalesOrderLineResponse toLineResponse(SalesOrderLine line);
}
