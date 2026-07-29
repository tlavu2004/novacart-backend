package com.tlavu.novacart.modules.catalog.product.application.usecase;

import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.product.domain.repository.ProductRepository;
import com.tlavu.novacart.modules.catalog.shared.domain.repository.query.PageRequest;
import com.tlavu.novacart.modules.catalog.shared.domain.repository.query.PageResult;
import com.tlavu.novacart.modules.catalog.shared.domain.repository.query.ProductFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListProductsUseCase {

    private final ProductRepository productRepository;

    public PageResult<Product> execute(ProductFilter filter, PageRequest pageRequest) {

        return productRepository.findAll(filter, pageRequest);
    }
}
