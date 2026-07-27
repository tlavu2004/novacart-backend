package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.application.exception.specific.CategoryNotFoundException;
import com.tlavu.novacart.modules.catalog.application.exception.specific.DuplicateCategoryNameException;
import com.tlavu.novacart.modules.catalog.application.exception.specific.DuplicateCategorySlugException;
import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.infrastructure.util.SlugUtils;
import com.tlavu.novacart.shared.application.exception.common.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateCategoryUseCaseTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private UpdateCategoryUseCase useCase;

    private Category existingCategory(Long id) {

        return Category.builder()
                .id(id)
                .name("Electronics")
                .slug("electronics")
                .description("Electronic devices.")
                .active(true)
                .build();
    }

    @Test
    void execute_whenAllFieldsProvided_updatesAllFieldsAndReturnsSavedCategory() {

        // Arrange
        Long id = 1L;
        String name = "Electronics Updated";
        String slug = SlugUtils.generate(name);
        String description = "Updated description for electronic devices.";
        Boolean active = false;

        Category category = Category.builder()
                .id(id)
                .name("Electronics")
                .slug("electronics")
                .description("Electronic devices.")
                .active(true)
                .build();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameIgnoreCaseAndIdNot(name, id)).thenReturn(false);
        when(categoryRepository.existsBySlugAndIdNot(slug, id)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        Category result = useCase.execute(id, name, description, active);

        // Assert
        assertThat(result).isEqualTo(category);

        Category captured = capturedSavedCategory();
        assertThat(captured.getName()).isEqualTo(name);
        assertThat(captured.getDescription()).isEqualTo(description);
        assertThat(captured.getSlug()).isEqualTo(slug);
        assertThat(captured.isActive()).isEqualTo(active);
    }

    @Test
    void execute_whenCategoryNotFound_throwsCategoryNotFoundException() {

        // Arrange
        Long id = 99L;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(id, "Electronics", null, null))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining(String.valueOf(id));

        verify(categoryRepository).findById(id);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void execute_whenNameIsBlank_throwsInvalidInputException() {

        // Arrange
        Long id = 1L;
        Category category = existingCategory(id);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(id, "  \t\n  ", null, null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("must not be blank");

        verify(categoryRepository).findById(id);
        verify(categoryRepository, never()).existsByNameIgnoreCaseAndIdNot(anyString(), anyLong());
        verify(categoryRepository, never()).existsBySlugAndIdNot(anyString(), anyLong());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void execute_whenNameAlreadyExistsForAnotherCategory_throwsDuplicateCategoryNameException() {

        // Arrange
        Long id = 1L;
        String name = "Electronics";
        Category category = existingCategory(id);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameIgnoreCaseAndIdNot(name, id)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(id, name, null, null))
                .isInstanceOf(DuplicateCategoryNameException.class)
                .hasMessageContaining(name);

        verify(categoryRepository).findById(id);
        verify(categoryRepository).existsByNameIgnoreCaseAndIdNot(name, id);
        verify(categoryRepository, never()).existsBySlugAndIdNot(anyString(), anyLong());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void execute_whenSlugAlreadyExistsForAnotherCategory_throwsDuplicateCategorySlugException() {

        // Arrange
        Long id = 1L;
        String name = "Electronics";
        String slug = SlugUtils.generate(name);
        Category category = existingCategory(id);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameIgnoreCaseAndIdNot(name, id)).thenReturn(false);
        when(categoryRepository.existsBySlugAndIdNot(slug, id)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(id, name, null, null))
                .isInstanceOf(DuplicateCategorySlugException.class)
                .hasMessageContaining(slug);

        verify(categoryRepository).findById(id);
        verify(categoryRepository).existsByNameIgnoreCaseAndIdNot(name, id);
        verify(categoryRepository).existsBySlugAndIdNot(slug, id);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void execute_whenNameContainsExtraWhitespace_normalizesNameAndSlug() {

        // Arrange
        Long id = 1L;
        String name = "  Electronics   Updated  ";
        String normalizedName = "Electronics Updated";
        String slug = SlugUtils.generate(normalizedName);
        Category category = existingCategory(id);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameIgnoreCaseAndIdNot(normalizedName, id)).thenReturn(false);
        when(categoryRepository.existsBySlugAndIdNot(slug, id)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        useCase.execute(id, name, null, null);

        // Assert
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(categoryCaptor.capture());
        verify(categoryRepository).existsByNameIgnoreCaseAndIdNot(normalizedName, id);
        verify(categoryRepository).existsBySlugAndIdNot(slug, id);

        Category captured = categoryCaptor.getValue();
        assertThat(captured.getName()).isEqualTo(normalizedName);
        assertThat(captured.getSlug()).isEqualTo(slug);
    }

    @Test
    void execute_whenNameIsNull_keepsExistingNameAndSlug() {

        // Arrange
        Long id = 1L;
        Category category = existingCategory(id);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        useCase.execute(id, null, "Updated description.", null);

        // Assert
        Category captured = capturedSavedCategory();
        assertThat(captured.getName()).isEqualTo("Electronics");
        assertThat(captured.getSlug()).isEqualTo("electronics");
        verify(categoryRepository, never()).existsByNameIgnoreCaseAndIdNot(anyString(), anyLong());
        verify(categoryRepository, never()).existsBySlugAndIdNot(anyString(), anyLong());
    }

    @Test
    void execute_whenNameIsUpdatedAndDescriptionIsNull_keepsExistingDescription() {

        // Arrange
        Long id = 1L;
        String name = "Electronics Updated";
        String slug = SlugUtils.generate(name);
        Category category = existingCategory(id);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameIgnoreCaseAndIdNot(name, id)).thenReturn(false);
        when(categoryRepository.existsBySlugAndIdNot(slug, id)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        useCase.execute(id, name, null, null);

        // Assert
        Category captured = capturedSavedCategory();
        assertThat(captured.getDescription()).isEqualTo("Electronic devices.");
    }

    @Test
    void execute_whenDescriptionProvided_updatesDescription() {

        // Arrange
        Long id = 1L;
        String description = "Updated description.";
        Category category = existingCategory(id);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        useCase.execute(id, null, description, null);

        // Assert
        Category captured = capturedSavedCategory();
        assertThat(captured.getDescription()).isEqualTo(description);
    }

    @Test
    void execute_whenDescriptionIsUpdatedAndActiveIsNull_keepsExistingActive() {

        // Arrange
        Long id = 1L;
        Category category = existingCategory(id);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        useCase.execute(id, null, "Updated description.", null);

        // Assert
        Category captured = capturedSavedCategory();
        assertThat(captured.isActive()).isTrue();
    }

    @Test
    void execute_whenActiveProvided_updatesActive() {

        // Arrange
        Long id = 1L;
        Category category = existingCategory(id);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        useCase.execute(id, null, null, false);

        // Assert
        Category captured = capturedSavedCategory();
        assertThat(captured.isActive()).isFalse();
    }

    @Test
    void execute_whenAllFieldsAreNull_keepsCategoryUnchanged() {

        // Arrange
        Long id = 1L;
        Category category = existingCategory(id);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        useCase.execute(id, null, null, null);

        // Assert
        Category captured = capturedSavedCategory();
        assertThat(captured)
                .extracting(Category::getName, Category::getSlug, Category::getDescription, Category::isActive)
                .containsExactly("Electronics", "electronics", "Electronic devices.", true);
        verify(categoryRepository, never()).existsByNameIgnoreCaseAndIdNot(anyString(), anyLong());
        verify(categoryRepository, never()).existsBySlugAndIdNot(anyString(), anyLong());
    }

    private Category capturedSavedCategory() {

        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(categoryCaptor.capture());
        return categoryCaptor.getValue();
    }
}
