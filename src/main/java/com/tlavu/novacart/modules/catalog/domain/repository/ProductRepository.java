package com.tlavu.novacart.modules.catalog.domain.repository;

import com.tlavu.novacart.modules.catalog.domain.entity.Product;

public interface ProductRepository {

    Product save(Product product);

    boolean existsByName(String name);
}
