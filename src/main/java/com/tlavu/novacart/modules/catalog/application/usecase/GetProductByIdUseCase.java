package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.shared.application.exception.common.ResourceNotFoundException;
import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.domain.repository.ProductRepository;
import com.tlavu.novacart.modules.catalog.application.exception.code.CatalogErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetProductByIdUseCase {

    private final ProductRepository productRepository;

    public Product execute(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CatalogErrorCode.PRODUCT_NOT_FOUND,
                        "Product with id=%d not found".formatted(id)
                ));
    }
}
