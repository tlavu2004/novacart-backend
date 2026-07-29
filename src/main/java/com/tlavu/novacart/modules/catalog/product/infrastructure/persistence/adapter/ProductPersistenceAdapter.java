package com.tlavu.novacart.modules.catalog.product.infrastructure.persistence.adapter;

import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.product.domain.repository.ProductRepository;
import com.tlavu.novacart.modules.catalog.shared.domain.repository.query.PageRequest;
import com.tlavu.novacart.modules.catalog.shared.domain.repository.query.PageResult;
import com.tlavu.novacart.modules.catalog.shared.domain.repository.query.ProductFilter;
import com.tlavu.novacart.modules.catalog.shared.domain.repository.query.SortOrder;
import com.tlavu.novacart.modules.catalog.product.infrastructure.persistence.jpa.repository.ProductJpaRepository;
import com.tlavu.novacart.modules.catalog.product.infrastructure.persistence.mapper.entity.ProductPersistenceMapper;
import com.tlavu.novacart.modules.catalog.product.infrastructure.persistence.jpa.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final ProductPersistenceMapper productPersistenceMapper;

    @Override
    public Product save(Product product) {

        return productPersistenceMapper.toDomain(
                productJpaRepository.save(productPersistenceMapper.toJpaEntity(product))
        );
    }

    @Override
    public Optional<Product> findById(Long id) {

        return productJpaRepository.findWithCategoryById(id).map(productPersistenceMapper::toDomain);
    }

    @Override
    public PageResult<Product> findAll(ProductFilter filter, PageRequest pageRequest) {

        org.springframework.data.domain.Page<Product> page = productJpaRepository.findAll(
                ProductSpecification.withFilter(filter),
                toPageable(pageRequest)
        ).map(productPersistenceMapper::toDomain);

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

        return productJpaRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public boolean existsBySlug(String slug) {

        return productJpaRepository.existsBySlug(slug);
    }

    public boolean existsByNameIgnoreCaseAndIdNot(String name, Long id) {

        return productJpaRepository.existsByNameIgnoreCaseAndIdNot(name, id);
    }

    public boolean existsBySlugAndIdNot(String name, Long id) {

        return productJpaRepository.existsBySlugAndIdNot(name, id);
    }

    @Override
    public boolean existsByCategoryId(Long id) {

        return productJpaRepository.existsByCategoryId(id);
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
