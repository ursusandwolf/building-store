package com.buildstore.product.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
