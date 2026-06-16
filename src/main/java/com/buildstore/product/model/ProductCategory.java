package com.buildstore.product.model;

import jakarta.persistence.*;

@Entity
@Table(name = "product_categories")
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    public ProductCategory() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public static ProductCategoryBuilder builder() {
        return new ProductCategoryBuilder();
    }

    public static class ProductCategoryBuilder {
        private String name;
        private String description;

        public ProductCategoryBuilder name(String name) { this.name = name; return this; }
        public ProductCategoryBuilder description(String description) { this.description = description; return this; }

        public ProductCategory build() {
            ProductCategory pc = new ProductCategory();
            pc.setName(name);
            pc.setDescription(description);
            return pc;
        }
    }
}
