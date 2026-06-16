package com.buildstore.product.repository;

import com.buildstore.product.model.ProductPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductPackageRepository extends JpaRepository<ProductPackage, Long> {
    List<ProductPackage> findAllByProductId(Long productId);
    Optional<ProductPackage> findByBarcode(String barcode);
}
