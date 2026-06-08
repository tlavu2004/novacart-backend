package com.tlavu.novacart.modules.catalog.infrastructure.persistence.adapter;

import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.domain.repository.ProductRepository;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.jpa.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public boolean existsByName(String name) {
        return productJpaRepository.existsByName(name);
    }
}
