package com.buildstore.product.controller;

import com.buildstore.product.dto.ProductPackageResponse;
import com.buildstore.product.dto.ProductResponse;
import com.buildstore.product.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/products")
public class CatalogController {

    private final ProductService productService;

    public CatalogController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> getActiveProducts() {
        return productService.getAllProducts(true);
    }

    @GetMapping("/{id}")
    public ProductResponse getActiveProductById(@PathVariable Long id) {
        return productService.getProductById(id, true);
    }

    @GetMapping("/{id}/packages")
    public List<ProductPackageResponse> getProductPackages(@PathVariable Long id) {
        // We only show packages for active products in the catalog
        productService.getProductById(id, true);
        return productService.getPackagesByProductId(id);
    }
}
