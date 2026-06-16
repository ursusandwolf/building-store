package com.buildstore.product.service;

import com.buildstore.common.exception.ResourceNotFoundException;
import com.buildstore.product.dto.ProductRequest;
import com.buildstore.product.dto.ProductResponse;
import com.buildstore.product.mapper.ProductMapper;
import com.buildstore.product.model.Product;
import com.buildstore.product.model.ProductCategory;
import com.buildstore.product.model.ProductStatus;
import com.buildstore.product.repository.ProductCategoryRepository;
import com.buildstore.product.repository.ProductRepository;
import com.buildstore.product.dto.ProductPackageRequest;
import com.buildstore.product.dto.ProductPackageResponse;
import com.buildstore.product.model.ProductPackage;
import com.buildstore.product.repository.ProductPackageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductPackageRepository productPackageRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating product with SKU: {}", request.sku());
        if (productRepository.findBySku(request.sku()).isPresent()) {
            throw new IllegalArgumentException("Product with SKU " + request.sku() + " already exists");
        }

        ProductCategory category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.categoryId()));
        }

        Product product = productMapper.toEntity(request);
        product.setCategory(category);

        Product saved = productRepository.save(product);
        return productMapper.toResponse(saved);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Updating product ID: {}, SKU: {}", id, request.sku());
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        if (!product.getSku().equals(request.sku())) {
            if (productRepository.findBySku(request.sku()).isPresent()) {
                throw new IllegalArgumentException("Product with SKU " + request.sku() + " already exists");
            }
        }

        ProductCategory category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.categoryId()));
        }

        productMapper.updateEntityFromRequest(request, product);
        product.setCategory(category);

        Product saved = productRepository.save(product);
        return productMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts(boolean onlyActive) {
        log.info("Fetching products (onlyActive={})", onlyActive);
        List<Product> products = onlyActive
                ? productRepository.findAllByStatus(ProductStatus.ACTIVE)
                : productRepository.findAll();
        return products.stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id, boolean onlyActive) {
        log.info("Fetching product ID: {} (onlyActive={})", id, onlyActive);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        if (onlyActive && product.getStatus() != ProductStatus.ACTIVE) {
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        }

        return productMapper.toResponse(product);
    }

    @Transactional
    public ProductPackageResponse addPackageToProduct(Long productId, ProductPackageRequest request) {
        log.info("Adding package to product ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (request.barcode() != null && !request.barcode().isBlank()) {
            if (productPackageRepository.findByBarcode(request.barcode()).isPresent()) {
                throw new IllegalArgumentException("Package with barcode " + request.barcode() + " already exists");
            }
        }

        if (Boolean.TRUE.equals(request.defaultForSale())) {
            List<ProductPackage> existing = productPackageRepository.findAllByProductId(productId);
            for (ProductPackage p : existing) {
                if (p.isDefaultForSale()) {
                    p.setDefaultForSale(false);
                    productPackageRepository.save(p);
                }
            }
        }

        if (Boolean.TRUE.equals(request.defaultForPurchase())) {
            List<ProductPackage> existing = productPackageRepository.findAllByProductId(productId);
            for (ProductPackage p : existing) {
                if (p.isDefaultForPurchase()) {
                    p.setDefaultForPurchase(false);
                    productPackageRepository.save(p);
                }
            }
        }

        ProductPackage pkg = productMapper.toPackageEntity(request);
        pkg.setProduct(product);
        if (request.defaultForSale() != null) {
            pkg.setDefaultForSale(request.defaultForSale());
        }
        if (request.defaultForPurchase() != null) {
            pkg.setDefaultForPurchase(request.defaultForPurchase());
        }

        ProductPackage saved = productPackageRepository.save(pkg);
        return productMapper.toPackageResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ProductPackageResponse> getPackagesByProductId(Long productId) {
        log.info("Fetching packages for product ID: {}", productId);
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with ID: " + productId);
        }
        return productPackageRepository.findAllByProductId(productId).stream()
                .map(productMapper::toPackageResponse)
                .toList();
    }
}
