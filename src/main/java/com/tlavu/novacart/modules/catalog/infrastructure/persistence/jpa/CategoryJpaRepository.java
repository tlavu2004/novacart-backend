package com.tlavu.novacart.modules.catalog.infrastructure.persistence.jpa;

import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);
}
