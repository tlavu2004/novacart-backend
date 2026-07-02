package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.application.exception.ResourceNotFoundException;
import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.domain.enums.ProductStatus;
import com.tlavu.novacart.modules.catalog.domain.repository.ProductRepository;
import com.tlavu.novacart.shared.application.exception.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteProductUseCase {

    private final ProductRepository productRepository;

    public void execute(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.PRODUCT_NOT_FOUND,
                        "Product with id=%d not found".formatted(id)
                ));

        // TODO: Check for active orders (PENDING/CONFIRMED) before deleting. Implement after Order module is completed

        product.setStatus(ProductStatus.INACTIVE);
        product.setDeletedAt(Instant.now());

        productRepository.save(product);
    }
}
