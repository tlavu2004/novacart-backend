package com.tlavu.novacart.modules.catalog.category.application.usecase;

import com.tlavu.novacart.modules.catalog.category.application.exception.DuplicateCategoryNameException;
import com.tlavu.novacart.modules.catalog.category.application.exception.DuplicateCategorySlugException;
import com.tlavu.novacart.modules.catalog.category.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.category.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.shared.domain.util.SlugUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateCategoryUseCaseTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CreateCategoryUseCase useCase;

    @Test
    void execute_whenNameAndSlugAreUnique_returnsSavedCategory() {

        // Arrange
        Long id = 1L;
        String name = "  Electronics   and   Gadgets  ";
        String normalizedName = name.trim().replaceAll("\\s+", " ");
        String slug = SlugUtils.generate(normalizedName);
        String description = "Electronic devices.";

        Category savedCategory = Category.builder()
                .id(id)
                .name(normalizedName)
                .slug(slug)
                .description(description)
                .active(true)
                .build();

        when(categoryRepository.existsByNameIgnoreCase(normalizedName)).thenReturn(false);
        when(categoryRepository.existsBySlug(slug)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // Act
        Category result = useCase.execute(name, description);

        // Assert
        assertThat(result).isEqualTo(savedCategory);

        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(categoryCaptor.capture());

        Category captured = categoryCaptor.getValue();
        assertThat(captured.getName()).isEqualTo(normalizedName);
        assertThat(captured.getSlug()).isEqualTo(slug);
        assertThat(captured.getDescription()).isEqualTo(description);
        assertThat(captured.isActive()).isTrue();
    }

    @Test
    void execute_whenNameAlreadyExists_throwsDuplicateCategoryNameException() {
        // Arrange
        String name = "Electronics";
        String description = "Electronic devices.";

        when(categoryRepository.existsByNameIgnoreCase(name)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(name, description))
                .isInstanceOf(DuplicateCategoryNameException.class);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void execute_whenSlugAlreadyExists_throwsDuplicateCategorySlugException() {
        // Arrange
        String name = "Electronics";
        String description = "Electronic devices.";
        String slug = "electronics";

        when(categoryRepository.existsByNameIgnoreCase(name)).thenReturn(false);
        when(categoryRepository.existsBySlug(slug)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(name, description))
                .isInstanceOf(DuplicateCategorySlugException.class);
        verify(categoryRepository, never()).save(any(Category.class));
    }
}
