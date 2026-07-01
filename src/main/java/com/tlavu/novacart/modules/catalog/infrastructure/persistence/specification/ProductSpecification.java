package com.tlavu.novacart.modules.catalog.infrastructure.persistence.specification;

import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.domain.enums.ProductStatus;
import com.tlavu.novacart.modules.catalog.presentation.dto.request.ProductFilterRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> withFilter(ProductFilterRequest filter) {
        return alwaysTrue()
                .and(hasName(filter.name()))
                .and(hasStatus(filter.status()))
                .and(hasCategoryId(filter.categoryId()))
                .and(hasPriceGreaterThanOrEqual(filter.minPrice()))
                .and(hasPriceLessThanOrEqual(filter.maxPrice()));
    }

    private static Specification<Product> alwaysTrue() {
        return (root, query, cb) -> null;  // match all, no WHERE condition
    }

    private static Specification<Product> hasName(String name) {

        return (name == null || name.isBlank())
                ? null
                : (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("name")),
                            "%" + name.toLowerCase() + "%"
                    );
    }

    private static Specification<Product> hasStatus(ProductStatus status) {

        return (status == null)
                ? null
                : (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(
                            root.get("status"),
                            status
                    );
    }

    private static Specification<Product> hasCategoryId(Long categoryId) {

        return (categoryId == null)
                ? null
                : (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(
                            root.get("category").get("id"),
                            categoryId
                    );
    }

    private static Specification<Product> hasPriceGreaterThanOrEqual(BigDecimal minPrice) {

        return (minPrice == null)
                ? null
                : (root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(
                            root.get("price"),
                            minPrice
                    );
    }

    private static Specification<Product> hasPriceLessThanOrEqual(BigDecimal maxPrice) {

        return (maxPrice == null)
                ? null
                : (root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(
                            root.get("price"),
                            maxPrice
                    );
    }
}
