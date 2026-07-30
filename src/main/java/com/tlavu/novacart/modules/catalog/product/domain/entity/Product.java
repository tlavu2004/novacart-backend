package com.tlavu.novacart.modules.catalog.product.domain.entity;

import com.tlavu.novacart.modules.catalog.category.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.product.domain.enums.ProductStatus;
import com.tlavu.novacart.modules.catalog.product.domain.exception.InvalidProductStatusTransitionException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class Product {

    private Long id;
    private String name;
    private String description;
    private String slug;
    private ProductStatus status = ProductStatus.DRAFT;
    private BigDecimal price;
    private Integer stockQuantity;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private Long version;
    private Category category;

    public static Product create(
            String name,
            String description,
            String slug,
            BigDecimal price,
            Integer stockQuantity,
            Category category
    ) {

        Product product = new Product();
        product.name = name;
        product.description = description;
        product.slug = slug;
        product.price = price;
        product.stockQuantity = stockQuantity;
        product.category = category;

        return product;
    }

    public void rename(String name, String slug) {

        this.name = name;
        this.slug = slug;
    }

    public void changeDescription(String description) {

        this.description = description;
    }

    public void changePrice(BigDecimal price) {

        this.price = price;
    }

    public void changeStock(Integer stockQuantity) {

        this.stockQuantity = stockQuantity;
    }

    public void moveToCategory(Category category) {

        this.category = category;
    }

    public void softDelete(Instant deletedAt) {

        this.deletedAt = deletedAt;
    }

    public void changeStatus(ProductStatus newStatus) {

        if (!this.status.canTransitionTo(newStatus)) {

            throw new InvalidProductStatusTransitionException(this.status, newStatus);
        }

        this.status = newStatus;
    }
}
