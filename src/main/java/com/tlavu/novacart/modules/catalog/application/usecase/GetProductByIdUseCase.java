package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.application.exception.ResourceNotFoundException;
import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.domain.repository.ProductRepository;
import com.tlavu.novacart.shared.exception.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetProductByIdUseCase {

    private final ProductRepository productRepository;

    public Product execute(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.PRODUCT_NOT_FOUND,
                        "Product with id=%d not found".formatted(id)
                ));
    }
}
