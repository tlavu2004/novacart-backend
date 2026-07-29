package com.tlavu.novacart.modules.catalog.infrastructure.persistence.adapter;

import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.domain.repository.query.PageRequest;
import com.tlavu.novacart.modules.catalog.domain.repository.query.PageResult;
import com.tlavu.novacart.modules.catalog.domain.repository.query.SortOrder;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.jpa.repository.CategoryJpaRepository;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.mapper.entity.CategoryPersistenceMapper;
import lombok.RequiredArgsConstructor;
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
    public PageResult<Category> findAll(PageRequest pageRequest) {

        org.springframework.data.domain.Page<Category> page = categoryJpaRepository.findAll(toPageable(pageRequest))
                .map(categoryPersistenceMapper::toDomain);

        return new PageResult<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
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

    private org.springframework.data.domain.Pageable toPageable(PageRequest pageRequest) {

        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by(
                pageRequest.sortOrders().stream()
                        .map(this::toSortOrder)
                        .toList()
        );

        return org.springframework.data.domain.PageRequest.of(pageRequest.page(), pageRequest.size(), sort);
    }

    private org.springframework.data.domain.Sort.Order toSortOrder(SortOrder sortOrder) {

        return new org.springframework.data.domain.Sort.Order(
                org.springframework.data.domain.Sort.Direction.valueOf(sortOrder.direction().name()),
                sortOrder.property()
        );
    }

}
