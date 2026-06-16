package com.buildstore.supplier.mapper;

import com.buildstore.supplier.dto.SupplierRequest;
import com.buildstore.supplier.dto.SupplierResponse;
import com.buildstore.supplier.model.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplierMapper {

    SupplierResponse toResponse(Supplier supplier);

    @Mapping(target = "id", ignore = true)
    Supplier toEntity(SupplierRequest request);
}
