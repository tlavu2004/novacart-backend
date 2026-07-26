package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.application.exception.specific.CategoryNotFoundException;
import com.tlavu.novacart.modules.catalog.application.exception.specific.DuplicateProductNameException;
import com.tlavu.novacart.modules.catalog.application.exception.specific.DuplicateProductSlugException;
import com.tlavu.novacart.modules.catalog.application.exception.specific.ProductNotFoundException;
import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.domain.repository.ProductRepository;
import com.tlavu.novacart.shared.application.exception.common.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProductUseCaseTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UpdateProductUseCase useCase;

    @Test
    void execute_whenAllFieldsProvided_updatesProduct() {
        Product product = product();
        Category replacement = category(2L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.existsByNameIgnoreCaseAndIdNot("Keyboard", 1L)).thenReturn(false);
        when(productRepository.existsBySlugAndIdNot("keyboard", 1L)).thenReturn(false);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(replacement));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = useCase.execute(
                1L, "Keyboard", "Updated", new BigDecimal("49.90"), 2L
        );

        assertThat(result).isSameAs(product);
        assertThat(product.getName()).isEqualTo("Keyboard");
        assertThat(product.getSlug()).isEqualTo("keyboard");
        assertThat(product.getDescription()).isEqualTo("Updated");
        assertThat(product.getPrice()).isEqualByComparingTo("49.90");
        assertThat(product.getCategory()).isSameAs(replacement);
        verify(productRepository).save(product);
    }

    @Test
    void execute_whenOptionalFieldsAreNull_preservesExistingValues() {
        Product product = product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(1L, null, null, null, null);

        assertThat(product)
                .extracting(Product::getName, Product::getSlug, Product::getDescription, Product::getPrice,
                        Product::getCategory)
                .containsExactly("Wireless Mouse", "wireless-mouse", "Original", new BigDecimal("19.90"),
                        product.getCategory());
        verify(productRepository, never()).existsByNameIgnoreCaseAndIdNot(anyString(), anyLong());
        verify(productRepository, never()).existsBySlugAndIdNot(anyString(), anyLong());
    }

    @Test
    void execute_whenNameContainsExtraWhitespace_trimsNameAndRegeneratesSlug() {
        Product product = product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.existsByNameIgnoreCaseAndIdNot("Mechanical Keyboard", 1L)).thenReturn(false);
        when(productRepository.existsBySlugAndIdNot("mechanical-keyboard", 1L)).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(1L, "  Mechanical Keyboard  ", null, null, null);

        assertThat(product.getName()).isEqualTo("Mechanical Keyboard");
        assertThat(product.getSlug()).isEqualTo("mechanical-keyboard");
    }

    @Test
    void execute_whenProductDoesNotExist_throwsProductNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99L, "Keyboard", null, null, null))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void execute_whenNameIsBlank_throwsInvalidInputException() {
        Product product = product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> useCase.execute(1L, "  \t\n ", null, null, null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("must not be blank");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void execute_whenNameAlreadyExistsForAnotherProduct_throwsDuplicateProductNameException() {
        Product product = product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.existsByNameIgnoreCaseAndIdNot("Keyboard", 1L)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(1L, "Keyboard", null, null, null))
                .isInstanceOf(DuplicateProductNameException.class);
        verify(productRepository, never()).existsBySlugAndIdNot(anyString(), anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void execute_whenSlugAlreadyExistsForAnotherProduct_throwsDuplicateProductSlugException() {
        Product product = product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.existsByNameIgnoreCaseAndIdNot("Keyboard", 1L)).thenReturn(false);
        when(productRepository.existsBySlugAndIdNot("keyboard", 1L)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(1L, "Keyboard", null, null, null))
                .isInstanceOf(DuplicateProductSlugException.class);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void execute_whenReplacementCategoryDoesNotExist_throwsCategoryNotFoundException() {
        Product product = product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(1L, null, null, null, 99L))
                .isInstanceOf(CategoryNotFoundException.class);
        verify(productRepository, never()).save(any(Product.class));
    }

    private Product product() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Wireless Mouse");
        product.setSlug("wireless-mouse");
        product.setDescription("Original");
        product.setPrice(new BigDecimal("19.90"));
        product.setStockQuantity(4);
        product.setCategory(category(1L));
        return product;
    }

    private Category category(Long id) {
        return Category.builder().id(id).name("Electronics").slug("electronics").build();
    }
}
