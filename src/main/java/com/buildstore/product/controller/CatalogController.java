package com.buildstore.product.controller;

import com.buildstore.product.dto.ProductResponse;
import com.buildstore.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/products")
@RequiredArgsConstructor
public class CatalogController {

    private final ProductService productService;

    @GetMapping
    public List<ProductResponse> getActiveProducts() {
        return productService.getAllProducts(true);
    }

    @GetMapping("/{id}")
    public ProductResponse getActiveProductById(@PathVariable Long id) {
        return productService.getProductById(id, true);
    }
}
