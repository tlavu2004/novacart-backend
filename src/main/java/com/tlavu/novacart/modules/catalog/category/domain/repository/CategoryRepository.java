package com.tlavu.novacart.modules.catalog.category.domain.repository;

import com.tlavu.novacart.modules.catalog.category.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.shared.domain.repository.query.PageRequest;
import com.tlavu.novacart.modules.catalog.shared.domain.repository.query.PageResult;

import java.util.Optional;

public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(Long id);

    PageResult<Category> findAll(PageRequest pageRequest);

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySlug(String slug);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsBySlugAndIdNot(String slug, Long id);
}
