package com.tlavu.novacart.modules.catalog.category.infrastructure.persistence.jpa.repository;

import com.tlavu.novacart.modules.catalog.category.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySlug(String slug);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsBySlugAndIdNot(String slug, Long id);
}
