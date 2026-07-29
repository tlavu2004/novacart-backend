package com.tlavu.novacart.modules.catalog.product.infrastructure.persistence.jpa;

import com.tlavu.novacart.bootstrap.config.JpaConfig;
import com.tlavu.novacart.modules.catalog.category.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import com.tlavu.novacart.modules.catalog.product.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import com.tlavu.novacart.modules.catalog.product.domain.enums.ProductStatus;
import com.tlavu.novacart.modules.catalog.category.infrastructure.persistence.jpa.repository.CategoryJpaRepository;
import com.tlavu.novacart.modules.catalog.product.infrastructure.persistence.jpa.repository.ProductJpaRepository;
import com.tlavu.novacart.modules.catalog.product.infrastructure.persistence.jpa.specification.ProductSpecification;
import com.tlavu.novacart.modules.catalog.product.domain.repository.query.ProductFilter;
import com.tlavu.novacart.support.AbstractIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(JpaConfig.class)
@Transactional
class ProductJpaRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private CategoryJpaRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void clearDatabase() {
        productRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        entityManager.clear();
    }

    @Test
    void existsByNameAndSlug_whenMatchingProductExists_returnsTrue() {
        CategoryJpaEntity CategoryJpaEntity = persistCategory(
                "Electronics",
                "electronics"
        );

        ProductJpaEntity ProductJpaEntity = persistProduct(
                "Wireless Mouse",
                "wireless-mouse",
                CategoryJpaEntity,
                ProductStatus.DRAFT,
                "19.90",
                4
        );

        clearPersistenceContext();

        assertThat(productRepository.existsByNameIgnoreCase("WIRELESS MOUSE")).isTrue();
        assertThat(productRepository.existsByNameIgnoreCase("Keyboard")).isFalse();
        assertThat(productRepository.existsBySlug("wireless-mouse")).isTrue();
        assertThat(productRepository.existsBySlug("keyboard")).isFalse();
        assertThat(productRepository.findById(ProductJpaEntity.getId())).isPresent();
    }

    @Test
    void existsByNameOrSlugAndIdNot_excludesCurrentProduct() {

        CategoryJpaEntity CategoryJpaEntity = persistCategory(
                "Electronics",
                "electronics"
        );

        ProductJpaEntity mouse = persistProduct(
                "Wireless Mouse",
                "wireless-mouse",
                CategoryJpaEntity,
                ProductStatus.DRAFT,
                "19.90",
                4
        );

        ProductJpaEntity keyboard = persistProduct(
                "Keyboard",
                "keyboard",
                CategoryJpaEntity,
                ProductStatus.DRAFT,
                "49.90",
                2
        );

        clearPersistenceContext();

        assertThat(productRepository.existsByNameIgnoreCaseAndIdNot("WIRELESS MOUSE", mouse.getId())).isFalse();
        assertThat(productRepository.existsBySlugAndIdNot("wireless-mouse", mouse.getId())).isFalse();
        assertThat(productRepository.existsByNameIgnoreCaseAndIdNot("Keyboard", mouse.getId())).isTrue();
        assertThat(productRepository.existsBySlugAndIdNot("keyboard", mouse.getId())).isTrue();
        assertThat(productRepository.existsByNameIgnoreCaseAndIdNot("Keyboard", keyboard.getId())).isFalse();
        assertThat(productRepository.existsBySlugAndIdNot("keyboard", keyboard.getId())).isFalse();
    }

    @Test
    void existsByCategoryId_whenProductsBelongToCategory_returnsExpectedValue() {

        CategoryJpaEntity electronics = persistCategory(
                "Electronics",
                "electronics"
        );

        CategoryJpaEntity apparel = persistCategory(
                "Apparel",
                "apparel"
        );

        persistProduct(
                "Wireless Mouse",
                "wireless-mouse",
                electronics,
                ProductStatus.DRAFT,
                "19.90",
                4
        );

        clearPersistenceContext();

        assertThat(productRepository.existsByCategoryId(electronics.getId())).isTrue();
        assertThat(productRepository.existsByCategoryId(apparel.getId())).isFalse();
    }

    @Test
    void findMethods_whenProductIsSoftDeleted_excludeItFromResults() {

        CategoryJpaEntity CategoryJpaEntity = persistCategory(
                "Electronics",
                "electronics"
        );

        ProductJpaEntity visible = persistProduct(
                "Wireless Mouse",
                "wireless-mouse",
                CategoryJpaEntity,
                ProductStatus.DRAFT,
                "19.90",
                4
        );

        ProductJpaEntity deleted = persistProduct(
                "Archived Mouse",
                "archived-mouse",
                CategoryJpaEntity,
                ProductStatus.DRAFT,
                "9.90",
                0
        );

        deleted.setDeletedAt(Instant.now());
        productRepository.saveAndFlush(deleted);
        clearPersistenceContext();

        assertThat(productRepository.findById(visible.getId())).isPresent();
        assertThat(productRepository.findById(deleted.getId())).isEmpty();
        assertThat(productRepository.findAll()).extracting(ProductJpaEntity::getId).containsExactly(visible.getId());
        assertThat(productRepository.existsByNameIgnoreCase("Archived Mouse")).isFalse();
        assertThat(productRepository.existsBySlug("archived-mouse")).isFalse();
    }

    @Test
    void findAll_withSpecification_filtersByNameStatusCategoryAndPrice() {

        CategoryJpaEntity electronics = persistCategory(
                "Electronics",
                "electronics"
        );

        CategoryJpaEntity apparel = persistCategory(
                "Apparel",
                "apparel"
        );

        ProductJpaEntity matching = persistProduct(
                "Wireless Mouse",
                "wireless-mouse",
                electronics,
                ProductStatus.ACTIVE,
                "29.90",
                4
        );

        persistProduct(
                "Gaming Keyboard",
                "gaming-keyboard",
                electronics,
                ProductStatus.DRAFT,
                "59.90",
                2
        );

        persistProduct(
                "Mouse Pad",
                "mouse-pad",
                apparel,
                ProductStatus.ACTIVE,
                "15.00",
                10
        );

        clearPersistenceContext();

        ProductFilter filter = new ProductFilter(
                "mouse",
                ProductStatus.ACTIVE,
                electronics.getId(),
                new BigDecimal("20.00"),
                new BigDecimal("40.00")
        );

        Specification<ProductJpaEntity> specification = ProductSpecification.withFilter(filter);

        Page<ProductJpaEntity> result = productRepository.findAll(specification, PageRequest.of(0, 10));

        assertThat(result.getContent()).extracting(ProductJpaEntity::getId).containsExactly(matching.getId());
        assertThat(Hibernate.isInitialized(result.getContent().getFirst().getCategory())).isTrue();
    }

    @Test
    void findWithCategoryById_fetchesCategory() {

        CategoryJpaEntity category = persistCategory("Electronics", "electronics");
        ProductJpaEntity product = persistProduct(
                "Wireless Mouse",
                "wireless-mouse",
                category,
                ProductStatus.DRAFT,
                "19.90",
                4
        );

        clearPersistenceContext();

        ProductJpaEntity result = productRepository.findWithCategoryById(product.getId()).orElseThrow();

        assertThat(Hibernate.isInitialized(result.getCategory())).isTrue();
        assertThat(result.getCategory().getId()).isEqualTo(category.getId());
    }

    @Test
    void findAll_withEachIndividualFilter_appliesThatFilter() {

        CategoryJpaEntity electronics = persistCategory("Electronics", "electronics");

        ProductJpaEntity active = persistProduct(
                "Wireless Mouse",
                "wireless-mouse",
                electronics,
                ProductStatus.ACTIVE,
                "29.90",
                4
        );

        ProductJpaEntity draft = persistProduct(
                "Keyboard",
                "keyboard",
                electronics,
                ProductStatus.DRAFT,
                "59.90",
                2
        );

        clearPersistenceContext();

        assertThat(
                productRepository.findAll(
                        ProductSpecification.withFilter(
                                new ProductFilter(
                                        "mouse",
                                        null,
                                        null,
                                        null,
                                        null
                                )
                        ),
                        PageRequest.of(0, 10)
                ).getContent()
        ).extracting(ProductJpaEntity::getId).containsExactly(active.getId());

        assertThat(
                productRepository.findAll(
                        ProductSpecification.withFilter(
                                new ProductFilter(
                                        "   ",
                                        null,
                                        null,
                                        null,
                                        null
                                )
                        ),
                        PageRequest.of(0, 10)
                ).getContent()
        ).extracting(ProductJpaEntity::getId).containsExactlyInAnyOrder(active.getId(), draft.getId());

        assertThat(
                productRepository.findAll(
                        ProductSpecification.withFilter(
                                new ProductFilter(
                                        null,
                                        ProductStatus.DRAFT,
                                        null,
                                        null,
                                        null)
                        ),
                        PageRequest.of(0, 10)
                ).getContent()
        ).extracting(ProductJpaEntity::getId).containsExactly(draft.getId());

        assertThat(productRepository.findAll(
                ProductSpecification.withFilter(
                        new ProductFilter(
                                null,
                                null,
                                electronics.getId(),
                                null,
                                null
                        )
                ),
                PageRequest.of(0, 10)
        ).getContent()).extracting(ProductJpaEntity::getId).containsExactlyInAnyOrder(active.getId(), draft.getId());
    }

    @Test
    void productMapping_persistsCategoryAndStatus() {

        CategoryJpaEntity CategoryJpaEntity = persistCategory(
                "Electronics",
                "electronics"
        );

        ProductJpaEntity ProductJpaEntity = persistProduct(
                "Wireless Mouse",
                "wireless-mouse",
                CategoryJpaEntity,
                ProductStatus.INACTIVE,
                "19.90",
                4
        );

        clearPersistenceContext();

        ProductJpaEntity reloaded = productRepository.findById(ProductJpaEntity.getId()).orElseThrow();

        assertThat(reloaded.getStatus()).isEqualTo(ProductStatus.INACTIVE);
        assertThat(Hibernate.isInitialized(reloaded.getCategory())).isFalse();
        assertThat(reloaded.getCategory().getId()).isEqualTo(CategoryJpaEntity.getId());
        assertThat(reloaded.getCategory().getName()).isEqualTo("Electronics");
    }

    @Test
    void save_whenPriceIsNotPositive_throwsDataIntegrityViolation() {

        CategoryJpaEntity CategoryJpaEntity = persistCategory(
                "Electronics",
                "electronics"
        );

        ProductJpaEntity invalidPrice = ProductJpaEntity(
                "Invalid Price",
                "invalid-price",
                CategoryJpaEntity,
                ProductStatus.DRAFT,
                "0",
                1
        );

        assertThatThrownBy(() -> productRepository.saveAndFlush(invalidPrice))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void save_whenStockIsNegative_throwsDataIntegrityViolation() {

        CategoryJpaEntity CategoryJpaEntity = persistCategory(
                "Electronics",
                "electronics"
        );

        ProductJpaEntity invalidStock = ProductJpaEntity(
                "Invalid Stock",
                "invalid-stock",
                CategoryJpaEntity,
                ProductStatus.DRAFT,
                "10",
                -1
        );

        assertThatThrownBy(() -> productRepository.saveAndFlush(invalidStock))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void save_whenAuditTimestampsAreNull_populatesCreatedAtAndUpdatedAt() {

        CategoryJpaEntity CategoryJpaEntity = persistCategory(
                "Electronics",
                "electronics"
        );

        ProductJpaEntity ProductJpaEntity = ProductJpaEntity(
                "Audited ProductJpaEntity",
                "audited-ProductJpaEntity",
                CategoryJpaEntity,
                ProductStatus.DRAFT,
                "10",
                1
        );
        ProductJpaEntity.setCreatedAt(null);
        ProductJpaEntity.setUpdatedAt(null);

        ProductJpaEntity saved = productRepository.saveAndFlush(ProductJpaEntity);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    private CategoryJpaEntity persistCategory(String name, String slug) {

        Instant now = Instant.now();
        return categoryRepository.saveAndFlush(CategoryJpaEntity.builder()
                .name(name)
                .slug(slug)
                .description(name + " description")
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build());
    }

    private ProductJpaEntity persistProduct(
            String name,
            String slug,
            CategoryJpaEntity CategoryJpaEntity,
            ProductStatus status,
            String price,
            int stock
    ) {

        ProductJpaEntity ProductJpaEntity = ProductJpaEntity(name, slug, CategoryJpaEntity, status, price, stock);
        return productRepository.saveAndFlush(ProductJpaEntity);
    }

    private ProductJpaEntity ProductJpaEntity(
            String name,
            String slug,
            CategoryJpaEntity CategoryJpaEntity,
            ProductStatus status,
            String price,
            int stock
    ) {

        Instant now = Instant.now();

        ProductJpaEntity ProductJpaEntity = new ProductJpaEntity();
        ProductJpaEntity.setName(name);
        ProductJpaEntity.setSlug(slug);
        ProductJpaEntity.setDescription(name + " description");
        ProductJpaEntity.setPrice(new BigDecimal(price));
        ProductJpaEntity.setStockQuantity(stock);
        ProductJpaEntity.setCategory(CategoryJpaEntity);
        ProductJpaEntity.setCreatedAt(now);
        ProductJpaEntity.setUpdatedAt(now);

        if (status == ProductStatus.ACTIVE) {
            ProductJpaEntity.setStatus(ProductStatus.ACTIVE);
        } else if (status == ProductStatus.INACTIVE) {
            ProductJpaEntity.setStatus(ProductStatus.ACTIVE);
            ProductJpaEntity.setStatus(ProductStatus.INACTIVE);
        }

        return ProductJpaEntity;
    }

    private void clearPersistenceContext() {

        entityManager.flush();
        entityManager.clear();
    }
}
