package com.tlavu.novacart.modules.catalog.product.application.usecase;

import com.tlavu.novacart.modules.catalog.product.application.exception.ProductNotFoundException;
import com.tlavu.novacart.modules.catalog.product.application.exception.InvalidProductStatusTransitionException;
import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.product.domain.enums.ProductStatus;
import com.tlavu.novacart.modules.catalog.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateProductStatusUseCase {

    private final ProductRepository productRepository;

    public Product execute(Long id, ProductStatus status) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        try {
            product.changeStatus(status);
        } catch (com.tlavu.novacart.modules.catalog.product.domain.exception.InvalidProductStatusTransitionException ex) {
            throw new InvalidProductStatusTransitionException(ex.getFrom(), ex.getTo());
        }

        return productRepository.save(product);
    }
}
