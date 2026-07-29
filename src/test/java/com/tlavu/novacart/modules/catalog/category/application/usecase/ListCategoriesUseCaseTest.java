package com.tlavu.novacart.modules.catalog.category.application.usecase;

import com.tlavu.novacart.modules.catalog.category.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.category.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.shared.domain.query.PageRequest;
import com.tlavu.novacart.modules.catalog.shared.domain.query.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        PageRequest pageRequest = new PageRequest(0, 10, List.of());
        List<Category> categories = List.of(
                Category.builder().id(1L).name("Electronics").build(),
                Category.builder().id(2L).name("Apparel").build()
        );
        PageResult<Category> expected = new PageResult<>(categories, 0, 10, categories.size(), 1);

        when(categoryRepository.findAll(pageRequest)).thenReturn(expected);

        PageResult<Category> result = useCase.execute(pageRequest);

        assertThat(result).isEqualTo(expected);
        verify(categoryRepository).findAll(pageRequest);
    }
}
