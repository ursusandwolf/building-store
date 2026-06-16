package com.buildstore.product.mapper;

import com.buildstore.product.dto.ProductRequest;
import com.buildstore.product.dto.ProductResponse;
import com.buildstore.product.model.Product;
import com.buildstore.product.dto.ProductPackageRequest;
import com.buildstore.product.dto.ProductPackageResponse;
import com.buildstore.product.model.ProductPackage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Product toEntity(ProductRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromRequest(ProductRequest request, @MappingTarget Product product);

    @Mapping(source = "product.id", target = "productId")
    ProductPackageResponse toPackageResponse(ProductPackage pkg);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductPackage toPackageEntity(ProductPackageRequest request);
}
