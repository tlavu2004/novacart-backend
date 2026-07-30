package com.tlavu.novacart.modules.catalog.product.infrastructure.persistence.transaction;

import com.tlavu.novacart.modules.catalog.product.application.port.out.ProductSlugWriteAttemptPort;
import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.product.infrastructure.persistence.jpa.repository.ProductJpaRepository;
import com.tlavu.novacart.modules.catalog.product.infrastructure.persistence.mapper.ProductPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductSlugWriteAttemptService implements ProductSlugWriteAttemptPort {

    private final ProductJpaRepository productJpaRepository;
    private final ProductPersistenceMapper productPersistenceMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Product persistAndFlush(Product product) {
        return productPersistenceMapper.toDomain(
                productJpaRepository.saveAndFlush(productPersistenceMapper.toJpaEntity(product))
        );
    }
}
