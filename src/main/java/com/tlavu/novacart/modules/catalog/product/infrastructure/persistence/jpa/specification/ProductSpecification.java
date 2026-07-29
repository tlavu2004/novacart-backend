package com.tlavu.novacart.modules.catalog.product.infrastructure.persistence.jpa.specification;

import com.tlavu.novacart.modules.catalog.product.domain.enums.ProductStatus;
import com.tlavu.novacart.modules.catalog.shared.domain.repository.query.ProductFilter;
import com.tlavu.novacart.modules.catalog.product.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<ProductJpaEntity> withFilter(ProductFilter filter) {

        return alwaysTrue()
                .and(hasName(filter.name()))
                .and(hasStatus(filter.status()))
                .and(hasCategoryId(filter.categoryId()))
                .and(hasPriceGreaterThanOrEqual(filter.minPrice()))
                .and(hasPriceLessThanOrEqual(filter.maxPrice()));
    }

    private static Specification<ProductJpaEntity> alwaysTrue() {

        return (root, query, cb) -> null;  // match all, no WHERE condition
    }

    private static Specification<ProductJpaEntity> hasName(String name) {

        return (name == null || name.isBlank())
                ? null
                : (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("name")),
                            "%" + name.toLowerCase() + "%"
                    );
    }

    private static Specification<ProductJpaEntity> hasStatus(ProductStatus status) {

        return (status == null)
                ? null
                : (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(
                            root.get("status"),
                            status
                    );
    }

    private static Specification<ProductJpaEntity> hasCategoryId(Long categoryId) {

        return (categoryId == null)
                ? null
                : (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(
                            root.get("category").get("id"),
                            categoryId
                    );
    }

    private static Specification<ProductJpaEntity> hasPriceGreaterThanOrEqual(BigDecimal minPrice) {

        return (minPrice == null)
                ? null
                : (root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(
                            root.get("price"),
                            minPrice
                    );
    }

    private static Specification<ProductJpaEntity> hasPriceLessThanOrEqual(BigDecimal maxPrice) {

        return (maxPrice == null)
                ? null
                : (root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(
                            root.get("price"),
                            maxPrice
                    );
    }
}
