package com.tlavu.novacart.modules.catalog.infrastructure.persistence.jpa;

import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.support.AbstractIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class CategoryJpaRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private CategoryJpaRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void existsByNameIgnoreCase_whenMatchingCategoryExists_returnsTrue() {

        // Arrange
        categoryRepository.saveAndFlush(category("Electronics", "electronics"));
        clearPersistenceContext();

        // Act & Assert
        assertThat(categoryRepository.existsByNameIgnoreCase("ELECTRONICS")).isTrue();
        assertThat(categoryRepository.existsByNameIgnoreCase("Apparel")).isFalse();
    }

    @Test
    void existsBySlug_whenMatchingCategoryExists_returnsTrue() {

        // Arrange
        categoryRepository.saveAndFlush(category("Electronics", "electronics"));
        clearPersistenceContext();

        // Act & Assert
        assertThat(categoryRepository.existsBySlug("electronics")).isTrue();
        assertThat(categoryRepository.existsBySlug("apparel")).isFalse();
    }

    @Test
    void existsByNameOrSlugAndIdNot_excludesTheCurrentCategory() {

        // Arrange
        Category electronics = categoryRepository.saveAndFlush(category("Electronics", "electronics"));
        Category apparel = categoryRepository.saveAndFlush(category("Apparel", "apparel"));
        clearPersistenceContext();

        // Act & Assert
        assertThat(categoryRepository.existsByNameIgnoreCaseAndIdNot("electronics", electronics.getId())).isFalse();
        assertThat(categoryRepository.existsBySlugAndIdNot("electronics", electronics.getId())).isFalse();
        assertThat(categoryRepository.existsByNameIgnoreCaseAndIdNot("apparel", electronics.getId())).isTrue();
        assertThat(categoryRepository.existsBySlugAndIdNot("apparel", electronics.getId())).isTrue();
        assertThat(categoryRepository.existsByNameIgnoreCaseAndIdNot("apparel", apparel.getId())).isFalse();
        assertThat(categoryRepository.existsBySlugAndIdNot("apparel", apparel.getId())).isFalse();
    }

    @Test
    void findMethods_whenCategoryIsSoftDeleted_excludeItFromResults() {

        // Arrange
        Category visibleCategory = categoryRepository.saveAndFlush(category("Electronics", "electronics"));
        Category deletedCategory = categoryRepository.saveAndFlush(category("Archived", "archived"));
        deletedCategory.setDeletedAt(Instant.now());
        categoryRepository.saveAndFlush(deletedCategory);
        clearPersistenceContext();

        // Act & Assert
        assertThat(categoryRepository.findById(visibleCategory.getId())).isPresent();
        assertThat(categoryRepository.findById(deletedCategory.getId())).isEmpty();
        assertThat(categoryRepository.findAll())
                .extracting(Category::getId)
                .containsExactly(visibleCategory.getId());
        assertThat(categoryRepository.existsByNameIgnoreCase("Archived")).isFalse();
        assertThat(categoryRepository.existsBySlug("archived")).isFalse();
    }

    private Category category(String name, String slug) {

        Instant now = Instant.now();
        return Category.builder()
                .name(name)
                .slug(slug)
                .description(name + " description")
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private void clearPersistenceContext() {

        entityManager.flush();
        entityManager.clear();
    }
}
