package com.tlavu.novacart.modules.catalog.infrastructure.persistence.jpa.repository;

import com.tlavu.novacart.modules.catalog.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductJpaRepository
        extends JpaRepository<ProductJpaEntity, Long>,
                JpaSpecificationExecutor<ProductJpaEntity> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySlug(String slug);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsBySlugAndIdNot(String name, Long id);

    boolean existsByCategoryId(Long id);
}
