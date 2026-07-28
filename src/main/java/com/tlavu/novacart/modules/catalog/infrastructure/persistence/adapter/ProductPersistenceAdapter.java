package com.tlavu.novacart.modules.catalog.infrastructure.persistence.adapter;

import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.domain.repository.ProductRepository;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.jpa.repository.ProductJpaRepository;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.mapper.product.ProductPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

        return productJpaRepository.findById(id).map(productPersistenceMapper::toDomain);
    }

    @Override
    public Page<Product> findAll(Specification<?> specification, Pageable pageable) {

        @SuppressWarnings("unchecked")
        Specification<ProductJpaEntity> jpaSpecification = (Specification<ProductJpaEntity>) specification;
        return productJpaRepository.findAll(jpaSpecification, pageable).map(productPersistenceMapper::toDomain);
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

}
