package com.buildstore.product.controller;

import com.buildstore.product.dto.ProductPackageRequest;
import com.buildstore.product.dto.ProductPackageResponse;
import com.buildstore.product.dto.ProductRequest;
import com.buildstore.product.dto.ProductResponse;
import com.buildstore.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse createProduct(@Valid @RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @PostMapping("/{id}/packages")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ProductPackageResponse addPackage(@PathVariable Long id, @Valid @RequestBody ProductPackageRequest request) {
        return productService.addPackageToProduct(id, request);
    }

    @GetMapping("/{id}/packages")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProductPackageResponse> getPackages(@PathVariable Long id) {
        return productService.getPackagesByProductId(id);
    }
}
