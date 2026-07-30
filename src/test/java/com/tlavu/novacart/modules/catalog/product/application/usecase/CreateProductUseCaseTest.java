package com.tlavu.novacart.modules.catalog.product.application.usecase;

import com.tlavu.novacart.modules.catalog.category.application.exception.CategoryNotFoundException;
import com.tlavu.novacart.modules.catalog.category.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.category.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.product.application.service.ProductSlugAllocationService;
import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductSlugAllocationService productSlugAllocationService;

    @InjectMocks
    private CreateProductUseCase useCase;

    @Test
    void execute_createsProductAndDelegatesSlugAllocation() {
        Category category = Category.builder().id(7L).name("Electronics").slug("electronics").build();
        Product saved = new Product();
        saved.setSlug("wireless-mouse");
        when(categoryRepository.findById(7L)).thenReturn(Optional.of(category));
        when(productSlugAllocationService.allocateAndPersist(any(Product.class))).thenReturn(saved);

        Product result = useCase.execute("  Wireless Mouse  ", "Description", new BigDecimal("19.90"), 4, 7L);

        assertThat(result).isSameAs(saved);
        ArgumentCaptor<Product> product = ArgumentCaptor.forClass(Product.class);
        verify(productSlugAllocationService).allocateAndPersist(product.capture());
        assertThat(product.getValue().getName()).isEqualTo("Wireless Mouse");
        assertThat(product.getValue().getCategory()).isSameAs(category);
    }

    @Test
    void execute_whenCategoryDoesNotExist_throwsCategoryNotFoundException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("Wireless Mouse", null, BigDecimal.TEN, 1, 99L))
                .isInstanceOf(CategoryNotFoundException.class);

        verifyNoInteractions(productSlugAllocationService);
    }
}
