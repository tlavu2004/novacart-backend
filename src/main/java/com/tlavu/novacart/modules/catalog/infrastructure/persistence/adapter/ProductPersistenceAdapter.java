package com.tlavu.novacart.modules.catalog.infrastructure.persistence.adapter;

import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.repository.ProductRepository;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.jpa.repository.ProductJpaRepository;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product save(Product product) {

        return toDomain(productJpaRepository.save(toJpaEntity(product)));
    }

    @Override
    public Optional<Product> findById(Long id) {

        return productJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Page<Product> findAll(Specification<?> specification, Pageable pageable) {

        @SuppressWarnings("unchecked")
        Specification<ProductJpaEntity> jpaSpecification = (Specification<ProductJpaEntity>) specification;
        return productJpaRepository.findAll(jpaSpecification, pageable).map(this::toDomain);
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {

        return productJpaRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public boolean existsBySlug(String slug) {

        return productJpaRepository.existsBySlug(slug);
    }

    public boolean existsByNameIgnoreCaseAndIdNot(String name, Long id) {

        return productJpaRepository.existsByNameIgnoreCaseAndIdNot(name, id);
    }

    public boolean existsBySlugAndIdNot(String name, Long id) {

        return productJpaRepository.existsBySlugAndIdNot(name, id);
    }

    @Override
    public boolean existsByCategoryId(Long id) {

        return productJpaRepository.existsByCategoryId(id);
    }

    private Product toDomain(ProductJpaEntity entity) {

        Category category = toDomainCategory(entity.getCategory());

        return Product.rehydrate(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getSlug(),
                entity.getStatus(),
                entity.getPrice(),
                entity.getStockQuantity(),
                category, entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    private ProductJpaEntity toJpaEntity(Product product) {

        return ProductJpaEntity.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .slug(product.getSlug())
                .status(product.getStatus())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(toJpaCategory(product.getCategory()))
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .deletedAt(product.getDeletedAt())
                .build();
    }

    private Category toDomainCategory(CategoryJpaEntity entity) {

        return Category.rehydrate(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getSlug(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    private CategoryJpaEntity toJpaCategory(Category category) {

        return CategoryJpaEntity.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .slug(category.getSlug())
                .active(category.isActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .deletedAt(category.getDeletedAt())
                .build();
    }
}
