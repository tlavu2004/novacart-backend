package com.tlavu.novacart.modules.catalog.domain.repository;

import com.tlavu.novacart.modules.catalog.domain.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    List<Product> findAll();

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySlug(String slug);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsBySlugAndIdNot(String name, Long id);

    boolean existsByCategoryId(Long id);
}
