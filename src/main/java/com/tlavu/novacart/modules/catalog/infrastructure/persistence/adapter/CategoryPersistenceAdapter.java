package com.tlavu.novacart.modules.catalog.infrastructure.persistence.adapter;

import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.jpa.repository.CategoryJpaRepository;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.mapper.category.CategoryPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryPersistenceMapper categoryPersistenceMapper;

    @Override
    public Category save(Category category) {

        return categoryPersistenceMapper.toDomain(
                categoryJpaRepository.save(categoryPersistenceMapper.toJpaEntity(category))
        );
    }

    @Override
    public Optional<Category> findById(Long id) {

        return categoryJpaRepository.findById(id).map(categoryPersistenceMapper::toDomain);
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {

        return categoryJpaRepository.findAll(pageable).map(categoryPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {

        return categoryJpaRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public boolean existsBySlug(String slug) {

        return categoryJpaRepository.existsBySlug(slug);
    }

    @Override
    public boolean existsByNameIgnoreCaseAndIdNot(String name, Long id) {

        return categoryJpaRepository.existsByNameIgnoreCaseAndIdNot(name, id);
    }

    @Override
    public boolean existsBySlugAndIdNot(String slug, Long id) {

        return categoryJpaRepository.existsBySlugAndIdNot(slug, id);
    }

}
