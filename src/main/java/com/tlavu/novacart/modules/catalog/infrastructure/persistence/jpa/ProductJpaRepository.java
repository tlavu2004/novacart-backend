package com.tlavu.novacart.modules.catalog.infrastructure.persistence.jpa;

import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySlug(String slug);

    boolean existsByCategoryId(Long id);
}
