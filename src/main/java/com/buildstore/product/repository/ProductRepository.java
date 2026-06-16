package com.buildstore.product.repository;

import com.buildstore.product.model.Product;
import com.buildstore.product.model.ProductStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = "category")
    List<Product> findAllByStatus(ProductStatus status);

    @EntityGraph(attributePaths = "category")
    @Override
    List<Product> findAll();

    @EntityGraph(attributePaths = "category")
    @Override
    Optional<Product> findById(Long id);

    Optional<Product> findBySku(String sku);
}
