package com.tlavu.novacart.modules.catalog.product.application.usecase;

import com.tlavu.novacart.modules.catalog.product.application.exception.ProductNotFoundException;
import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateProductStockUseCase {

    private final ProductRepository productRepository;

    public Product execute(Long id, Integer stockQuantity) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.changeStock(stockQuantity);

        return productRepository.save(product);
    }
}
