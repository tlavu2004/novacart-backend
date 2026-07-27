package com.tlavu.novacart.modules.catalog.domain.entity;

import com.tlavu.novacart.modules.catalog.domain.enums.ProductStatus;
import com.tlavu.novacart.modules.catalog.domain.exception.InvalidProductStatusTransitionException;
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
    private Category category;

    private Product(Long id, String name, String description, String slug, ProductStatus status, BigDecimal price,
                    Integer stockQuantity, Category category, Instant createdAt, Instant updatedAt, Instant deletedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.slug = slug;
        this.status = status;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static Product create(
            String name,
            String description,
            String slug,
            BigDecimal price,
            Integer stockQuantity,
            Category category
    ) {

        return new Product(
                null,
                name,
                description,
                slug,
                ProductStatus.DRAFT,
                price,
                stockQuantity,
                category,
                null,
                null,
                null
        );
    }

    public static Product rehydrate(
            Long id,
            String name,
            String description,
            String slug,
            ProductStatus status,
            BigDecimal price,
            Integer stockQuantity,
            Category category,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {

        return new Product(
                id,
                name,
                description,
                slug,
                status,
                price,
                stockQuantity,
                category,
                createdAt,
                updatedAt,
                deletedAt
        );
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
