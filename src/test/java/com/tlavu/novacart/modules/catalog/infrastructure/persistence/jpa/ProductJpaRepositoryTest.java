package com.tlavu.novacart.modules.catalog.infrastructure.persistence.jpa;

import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.domain.enums.ProductStatus;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.specification.ProductSpecification;
import com.tlavu.novacart.modules.catalog.presentation.dto.request.ProductFilterRequest;
import com.tlavu.novacart.support.AbstractIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class ProductJpaRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private CategoryJpaRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void existsByNameAndSlug_whenMatchingProductExists_returnsTrue() {
        Category category = persistCategory("Electronics", "electronics");
        Product product = persistProduct("Wireless Mouse", "wireless-mouse", category, ProductStatus.DRAFT,
                "19.90", 4);
        clearPersistenceContext();

        assertThat(productRepository.existsByNameIgnoreCase("WIRELESS MOUSE")).isTrue();
        assertThat(productRepository.existsByNameIgnoreCase("Keyboard")).isFalse();
        assertThat(productRepository.existsBySlug("wireless-mouse")).isTrue();
        assertThat(productRepository.existsBySlug("keyboard")).isFalse();
        assertThat(productRepository.findById(product.getId())).isPresent();
    }

    @Test
    void existsByNameOrSlugAndIdNot_excludesCurrentProduct() {
        Category category = persistCategory("Electronics", "electronics");
        Product mouse = persistProduct("Wireless Mouse", "wireless-mouse", category, ProductStatus.DRAFT,
                "19.90", 4);
        Product keyboard = persistProduct("Keyboard", "keyboard", category, ProductStatus.DRAFT,
                "49.90", 2);
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
        Category electronics = persistCategory("Electronics", "electronics");
        Category apparel = persistCategory("Apparel", "apparel");
        persistProduct("Wireless Mouse", "wireless-mouse", electronics, ProductStatus.DRAFT, "19.90", 4);
        clearPersistenceContext();

        assertThat(productRepository.existsByCategoryId(electronics.getId())).isTrue();
        assertThat(productRepository.existsByCategoryId(apparel.getId())).isFalse();
    }

    @Test
    void findMethods_whenProductIsSoftDeleted_excludeItFromResults() {
        Category category = persistCategory("Electronics", "electronics");
        Product visible = persistProduct("Wireless Mouse", "wireless-mouse", category, ProductStatus.DRAFT,
                "19.90", 4);
        Product deleted = persistProduct("Archived Mouse", "archived-mouse", category, ProductStatus.DRAFT,
                "9.90", 0);
        deleted.setDeletedAt(Instant.now());
        productRepository.saveAndFlush(deleted);
        clearPersistenceContext();

        assertThat(productRepository.findById(visible.getId())).isPresent();
        assertThat(productRepository.findById(deleted.getId())).isEmpty();
        assertThat(productRepository.findAll()).extracting(Product::getId).containsExactly(visible.getId());
        assertThat(productRepository.existsByNameIgnoreCase("Archived Mouse")).isFalse();
        assertThat(productRepository.existsBySlug("archived-mouse")).isFalse();
    }

    @Test
    void findAll_withSpecification_filtersByNameStatusCategoryAndPrice() {
        Category electronics = persistCategory("Electronics", "electronics");
        Category apparel = persistCategory("Apparel", "apparel");
        Product matching = persistProduct("Wireless Mouse", "wireless-mouse", electronics, ProductStatus.ACTIVE,
                "29.90", 4);
        persistProduct("Gaming Keyboard", "gaming-keyboard", electronics, ProductStatus.DRAFT, "59.90", 2);
        persistProduct("Mouse Pad", "mouse-pad", apparel, ProductStatus.ACTIVE, "15.00", 10);
        clearPersistenceContext();

        ProductFilterRequest filter = new ProductFilterRequest(
                "mouse", ProductStatus.ACTIVE, electronics.getId(),
                new BigDecimal("20.00"), new BigDecimal("40.00")
        );
        Specification<Product> specification = ProductSpecification.withFilter(filter);

        Page<Product> result = productRepository.findAll(specification, PageRequest.of(0, 10));

        assertThat(result.getContent()).extracting(Product::getId).containsExactly(matching.getId());
    }

    @Test
    void findAll_withEachIndividualFilter_appliesThatFilter() {
        Category electronics = persistCategory("Electronics", "electronics");
        Product active = persistProduct("Wireless Mouse", "wireless-mouse", electronics, ProductStatus.ACTIVE,
                "29.90", 4);
        Product draft = persistProduct("Keyboard", "keyboard", electronics, ProductStatus.DRAFT,
                "59.90", 2);
        clearPersistenceContext();

        assertThat(productRepository.findAll(
                ProductSpecification.withFilter(new ProductFilterRequest("mouse", null, null, null, null)),
                PageRequest.of(0, 10)
        ).getContent()).extracting(Product::getId).containsExactly(active.getId());

        assertThat(productRepository.findAll(
                ProductSpecification.withFilter(new ProductFilterRequest(null, ProductStatus.DRAFT, null, null, null)),
                PageRequest.of(0, 10)
        ).getContent()).extracting(Product::getId).containsExactly(draft.getId());

        assertThat(productRepository.findAll(
                ProductSpecification.withFilter(new ProductFilterRequest(null, null, electronics.getId(), null, null)),
                PageRequest.of(0, 10)
        ).getContent()).extracting(Product::getId).containsExactlyInAnyOrder(active.getId(), draft.getId());
    }

    @Test
    void productMapping_persistsCategoryAndStatus() {
        Category category = persistCategory("Electronics", "electronics");
        Product product = persistProduct("Wireless Mouse", "wireless-mouse", category, ProductStatus.INACTIVE,
                "19.90", 4);
        clearPersistenceContext();

        Product reloaded = productRepository.findById(product.getId()).orElseThrow();

        assertThat(reloaded.getStatus()).isEqualTo(ProductStatus.INACTIVE);
        assertThat(reloaded.getCategory().getId()).isEqualTo(category.getId());
        assertThat(reloaded.getCategory().getName()).isEqualTo("Electronics");
    }

    @Test
    void save_whenPriceOrStockViolatesDatabaseConstraint_throwsDataIntegrityViolation() {
        Category category = persistCategory("Electronics", "electronics");
        Product invalidPrice = product("Invalid Price", "invalid-price", category, ProductStatus.DRAFT, "0", 1);

        assertThatThrownBy(() -> productRepository.saveAndFlush(invalidPrice))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private Category persistCategory(String name, String slug) {
        Instant now = Instant.now();
        return categoryRepository.saveAndFlush(Category.builder()
                .name(name)
                .slug(slug)
                .description(name + " description")
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build());
    }

    private Product persistProduct(
            String name,
            String slug,
            Category category,
            ProductStatus status,
            String price,
            int stock
    ) {
        Product product = product(name, slug, category, status, price, stock);
        return productRepository.saveAndFlush(product);
    }

    private Product product(
            String name,
            String slug,
            Category category,
            ProductStatus status,
            String price,
            int stock
    ) {
        Instant now = Instant.now();
        Product product = new Product();
        product.setName(name);
        product.setSlug(slug);
        product.setDescription(name + " description");
        product.setPrice(new BigDecimal(price));
        product.setStockQuantity(stock);
        product.setCategory(category);
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        if (status == ProductStatus.ACTIVE) {
            product.changeStatus(ProductStatus.ACTIVE);
        } else if (status == ProductStatus.INACTIVE) {
            product.changeStatus(ProductStatus.ACTIVE);
            product.changeStatus(ProductStatus.INACTIVE);
        }
        return product;
    }

    private void clearPersistenceContext() {
        entityManager.flush();
        entityManager.clear();
    }
}
