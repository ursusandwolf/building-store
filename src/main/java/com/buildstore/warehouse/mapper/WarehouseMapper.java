package com.buildstore.warehouse.mapper;

import com.buildstore.warehouse.dto.WarehouseRequest;
import com.buildstore.warehouse.dto.WarehouseResponse;
import com.buildstore.warehouse.model.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WarehouseMapper {

    WarehouseResponse toResponse(Warehouse warehouse);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Warehouse toEntity(WarehouseRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromRequest(WarehouseRequest request, @MappingTarget Warehouse warehouse);
}
