package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.application.exception.specific.CategoryHasActiveProductsException;
import com.tlavu.novacart.modules.catalog.application.exception.specific.CategoryNotFoundException;
import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.domain.repository.ProductRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteCategoryUseCaseTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private DeleteCategoryUseCase useCase;

    private Category existingCategory(Long id) {

        return Category.builder()
                .id(id)
                .name("Electronics")
                .active(true)
                .deletedAt(null)
                .build();
    }

    @Test
    void execute_whenCategoryHasNoProducts_softDeletesCategory() {

        // Arrange
        Long id = 1L;
        Category category = existingCategory(id);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryId(id)).thenReturn(false);

        // Act
        useCase.execute(id);

        // Assert
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(categoryCaptor.capture());

        Category captured = categoryCaptor.getValue();
        assertThat(captured.isActive()).isFalse();
        assertThat(captured.getDeletedAt()).isNotNull();
        verify(categoryRepository).findById(id);
        verify(productRepository).existsByCategoryId(id);
    }

    @Test
    void execute_whenCategoryNotFound_throwsCategoryNotFoundException() {

        // Arrange
        Long id = 99L;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining(String.valueOf(id));

        verify(categoryRepository).findById(id);
        verify(categoryRepository, never()).save(any(Category.class));
        verifyNoInteractions(productRepository);
    }

    @Test
    void execute_whenCategoryHasProducts_throwsCategoryHasActiveProductsException() {

        // Arrange
        Long id = 99L;
        Category category = existingCategory(id);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryId(id)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(CategoryHasActiveProductsException.class)
                .hasMessageContaining(String.valueOf(id));

        assertThat(category.isActive()).isTrue();
        assertThat(category.getDeletedAt()).isNull();
        verify(categoryRepository).findById(id);
        verify(productRepository).existsByCategoryId(id);
        verify(categoryRepository, never()).save(any(Category.class));
    }
}
