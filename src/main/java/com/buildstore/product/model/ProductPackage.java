package com.buildstore.product.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_packages")
public class ProductPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private UnitOfMeasure unit;

    @Column(name = "quantity_in_base_unit", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantityInBaseUnit;

    @Column(unique = true, length = 50)
    private String barcode;

    @Column(name = "is_default_for_sale", nullable = false)
    private boolean defaultForSale = false;

    @Column(name = "is_default_for_purchase", nullable = false)
    private boolean defaultForPurchase = false;

    public ProductPackage() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UnitOfMeasure getUnit() {
        return unit;
    }

    public void setUnit(UnitOfMeasure unit) {
        this.unit = unit;
    }

    public BigDecimal getQuantityInBaseUnit() {
        return quantityInBaseUnit;
    }

    public void setQuantityInBaseUnit(BigDecimal quantityInBaseUnit) {
        this.quantityInBaseUnit = quantityInBaseUnit;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public boolean isDefaultForSale() {
        return defaultForSale;
    }

    public void setDefaultForSale(boolean defaultForSale) {
        this.defaultForSale = defaultForSale;
    }

    public boolean isDefaultForPurchase() {
        return defaultForPurchase;
    }

    public void setDefaultForPurchase(boolean defaultForPurchase) {
        this.defaultForPurchase = defaultForPurchase;
    }
}
