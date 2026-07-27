package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListCategoriesUseCaseTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ListCategoriesUseCase useCase;

    @Test
    void execute_returnsPageOfCategories() {

        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(
                Category.builder().id(1L).name("Electronics").build(),
                Category.builder().id(2L).name("Apparel").build()
        );
        Page<Category> pageCategories = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(pageCategories);

        // Act
        Page<Category> result = useCase.execute(pageable);

        // Assert
        assertThat(result).isEqualTo(pageCategories);
        verify(categoryRepository).findAll(pageable);
    }
}
