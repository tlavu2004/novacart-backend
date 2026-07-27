package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.application.exception.specific.CategoryNotFoundException;
import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetCategoryByIdUseCaseTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private GetCategoryByIdUseCase useCase;

    @Test
    void execute_whenCategoryExists_returnsCategory() {

        // Arrange
        Long id = 1L;

        Category category = Category.builder()
                .id(id)
                .name("Electronics")
                .build();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        // Act
        Category result = useCase.execute(id);

        // Assert
        assertThat(result).isEqualTo(category);
        verify(categoryRepository).findById(id);
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
    }
}
