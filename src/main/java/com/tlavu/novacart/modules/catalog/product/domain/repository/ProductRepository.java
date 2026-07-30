package com.tlavu.novacart.modules.catalog.product.domain.repository;

import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.shared.domain.query.PageRequest;
import com.tlavu.novacart.modules.catalog.shared.domain.query.PageResult;
import com.tlavu.novacart.modules.catalog.product.domain.repository.query.ProductFilter;

import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    PageResult<Product> findAll(ProductFilter filter, PageRequest pageRequest);

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySlug(String slug);

    boolean isSlugReserved(String slug);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsBySlugAndIdNot(String name, Long id);

}
