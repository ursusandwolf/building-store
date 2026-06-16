package com.buildstore.pricing.model;

import com.buildstore.product.model.Product;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "discounts")
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal percentage;

    @Column(nullable = false)
    private Integer priority;

    @Column(nullable = false)
    private boolean compatible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private boolean active = true;

    public Discount() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPercentage() { return percentage; }
    public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public boolean isCompatible() { return compatible; }
    public void setCompatible(boolean compatible) { this.compatible = compatible; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
