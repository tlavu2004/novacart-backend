package com.tlavu.novacart.modules.catalog.integration.persistence;

import com.tlavu.novacart.modules.catalog.category.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.product.domain.enums.ProductStatus;
import com.tlavu.novacart.modules.catalog.category.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import com.tlavu.novacart.modules.catalog.product.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import com.tlavu.novacart.modules.catalog.category.infrastructure.persistence.mapper.CategoryPersistenceMapper;
import com.tlavu.novacart.modules.catalog.category.infrastructure.persistence.mapper.CategoryPersistenceMapperImpl;
import com.tlavu.novacart.modules.catalog.product.infrastructure.persistence.mapper.ProductPersistenceMapper;
import com.tlavu.novacart.modules.catalog.product.infrastructure.persistence.mapper.ProductPersistenceMapperImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceMapperTest {

    private final CategoryPersistenceMapper categoryMapper = new CategoryPersistenceMapperImpl();
    private final ProductPersistenceMapper productMapper = new ProductPersistenceMapperImpl(categoryMapper);

    @Test
    void categoryMapper_mapsPersistedStateInBothDirections() {

        Instant createdAt = Instant.parse("2026-07-28T01:00:00Z");
        Instant updatedAt = Instant.parse("2026-07-28T02:00:00Z");
        CategoryJpaEntity entity = CategoryJpaEntity.builder()
                .id(7L)
                .name("Electronics")
                .description("Devices")
                .slug("electronics")
                .active(true)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        Category domain = categoryMapper.toDomain(entity);
        CategoryJpaEntity mappedEntity = categoryMapper.toJpaEntity(domain);

        assertThat(domain.getId()).isEqualTo(7L);
        assertThat(domain.getName()).isEqualTo("Electronics");
        assertThat(domain.getCreatedAt()).isEqualTo(createdAt);
        assertThat(mappedEntity).usingRecursiveComparison().isEqualTo(entity);
    }

    @Test
    void productMapper_mapsCategoryAndAllPersistedStateInBothDirections() {

        Instant createdAt = Instant.parse("2026-07-28T01:00:00Z");
        Instant updatedAt = Instant.parse("2026-07-28T02:00:00Z");
        CategoryJpaEntity category = CategoryJpaEntity.builder()
                .id(7L)
                .name("Electronics")
                .slug("electronics")
                .active(true)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
        ProductJpaEntity entity = ProductJpaEntity.builder()
                .id(11L)
                .name("Wireless Mouse")
                .description("Bluetooth")
                .slug("wireless-mouse")
                .status(ProductStatus.ACTIVE)
                .price(new BigDecimal("29.99"))
                .stockQuantity(12)
                .category(category)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        Product domain = productMapper.toDomain(entity);
        ProductJpaEntity mappedEntity = productMapper.toJpaEntity(domain);

        assertThat(domain.getId()).isEqualTo(11L);
        assertThat(domain.getStatus()).isEqualTo(ProductStatus.ACTIVE);
        assertThat(domain.getCategory().getId()).isEqualTo(7L);
        assertThat(mappedEntity).usingRecursiveComparison().isEqualTo(entity);
    }

    @Test
    void mappers_returnNullForNullSource() {

        assertThat(categoryMapper.toDomain(null)).isNull();
        assertThat(categoryMapper.toJpaEntity(null)).isNull();
        assertThat(productMapper.toDomain(null)).isNull();
        assertThat(productMapper.toJpaEntity(null)).isNull();
    }
}
