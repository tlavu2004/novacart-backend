package com.tlavu.novacart.modules.catalog.domain.repository;

import com.tlavu.novacart.modules.catalog.domain.entity.Category;

import java.util.Optional;

public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(Long id);

    boolean existsByName(String name);
}
