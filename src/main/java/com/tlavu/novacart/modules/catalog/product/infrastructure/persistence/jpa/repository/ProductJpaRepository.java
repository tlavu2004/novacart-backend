package com.tlavu.novacart.modules.catalog.product.infrastructure.persistence.jpa.repository;

import com.tlavu.novacart.modules.catalog.product.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface ProductJpaRepository
        extends JpaRepository<ProductJpaEntity, Long>,
                JpaSpecificationExecutor<ProductJpaEntity> {

    @EntityGraph(attributePaths = "category")
    Optional<ProductJpaEntity> findWithCategoryById(Long id);

    @Override
    @EntityGraph(attributePaths = "category")
    @NonNull
    Page<ProductJpaEntity> findAll(
            @Nullable Specification<ProductJpaEntity> specification,
            @NonNull Pageable pageable
    );

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySlug(String slug);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM products
                WHERE LOWER(slug) = LOWER(:slug)
            )
            """, nativeQuery = true)
    boolean isSlugReserved(@Param("slug") String slug);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsBySlugAndIdNot(String name, Long id);

    boolean existsByCategoryId(Long id);
}
