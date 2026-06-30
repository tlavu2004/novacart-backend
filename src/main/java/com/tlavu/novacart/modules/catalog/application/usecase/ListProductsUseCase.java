package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.domain.repository.ProductRepository;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.specification.ProductSpecification;
import com.tlavu.novacart.modules.catalog.presentation.dto.request.ProductFilterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ListProductsUseCase {

    private final ProductRepository productRepository;

    public Page<Product> execute(ProductFilterRequest filter, Pageable pageable) {

        Specification<Product> specification = ProductSpecification.withFilter(filter);

        return productRepository.findAll(specification, pageable);
    }
}
