package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.application.exception.InvalidInputException;
import com.tlavu.novacart.modules.catalog.application.exception.ResourceNotFoundException;
import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.domain.enums.ProductStatus;
import com.tlavu.novacart.modules.catalog.domain.repository.ProductRepository;
import com.tlavu.novacart.shared.application.exception.code.ErrorCode;
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
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.PRODUCT_NOT_FOUND,
                        "Product with id=%d not found".formatted(id)
                ));

        if (!product.getStatus().canTransitionTo(status)) {
            throw new InvalidInputException(
                    ErrorCode.INVALID_STATUS_TRANSITION,
                    "Cannot transition from %s to %s".formatted(product.getStatus(), status)
            );
        }

        product.setStatus(status);

        return productRepository.save(product);
    }
}
