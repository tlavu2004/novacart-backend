package com.tlavu.novacart.modules.catalog.infrastructure.persistence.adapter;

import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.jpa.repository.CategoryJpaRepository;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public Category save(Category category) {

        return toDomain(categoryJpaRepository.save(toJpaEntity(category)));
    }

    @Override
    public Optional<Category> findById(Long id) {

        return categoryJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {

        return categoryJpaRepository.findAll(pageable).map(this::toDomain);
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

    private Category toDomain(CategoryJpaEntity entity) {

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

    private CategoryJpaEntity toJpaEntity(Category category) {

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
