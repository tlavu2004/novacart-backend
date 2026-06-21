package com.tlavu.novacart.modules.catalog.infrastructure.persistence.adapter;

import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.jpa.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public Category save(Category category) {

        return categoryJpaRepository.save(category);
    }

    @Override
    public Optional<Category> findById(Long id) {

        return categoryJpaRepository.findById(id);
    }

    @Override
    public List<Category> findAll() {

        return categoryJpaRepository.findAll();
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {

        return categoryJpaRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public boolean existsBySlug(String slug) {

        return categoryJpaRepository.existsBySlug(slug);
    }
}
