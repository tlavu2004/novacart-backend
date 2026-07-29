package com.tlavu.novacart.modules.catalog.product.application.usecase;

import com.tlavu.novacart.modules.catalog.category.application.exception.specific.CategoryNotFoundException;
import com.tlavu.novacart.modules.catalog.product.application.exception.specific.DuplicateProductNameException;
import com.tlavu.novacart.modules.catalog.product.application.exception.specific.DuplicateProductSlugException;
import com.tlavu.novacart.modules.catalog.category.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.category.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.product.domain.repository.ProductRepository;
import com.tlavu.novacart.modules.catalog.shared.domain.util.SlugUtils;
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
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CreateProductUseCase useCase;

    @Test
    void execute_whenProductDataIsUnique_createsProductWithNormalizedNameAndCategory() {
        String inputName = "  Wireless   Mouse  ";
        String normalizedName = inputName.trim();
        String slug = SlugUtils.generate(normalizedName);
        Category category = category();
        Product savedProduct = product();

        when(productRepository.existsByNameIgnoreCase(normalizedName)).thenReturn(false);
        when(productRepository.existsBySlug(slug)).thenReturn(false);
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = useCase.execute(
                inputName,
                "Description",
                new BigDecimal("19.90"),
                4,
                category.getId()
        );

        assertThat(result).isSameAs(savedProduct);
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        Product captured = captor.getValue();
        assertThat(captured.getName()).isEqualTo(normalizedName);
        assertThat(captured.getSlug()).isEqualTo(slug);
        assertThat(captured.getDescription()).isEqualTo("Description");
        assertThat(captured.getPrice()).isEqualByComparingTo("19.90");
        assertThat(captured.getStockQuantity()).isEqualTo(4);
        assertThat(captured.getCategory()).isSameAs(category);
    }

    @Test
    void execute_whenNameAlreadyExists_throwsDuplicateProductNameException() {
        when(productRepository.existsByNameIgnoreCase("Wireless Mouse")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(
                "Wireless Mouse", null, BigDecimal.TEN, 1, 7L
        )).isInstanceOf(DuplicateProductNameException.class);

        verify(productRepository, never()).existsBySlug(anyString());
        verifyNoInteractions(categoryRepository);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void execute_whenSlugAlreadyExists_throwsDuplicateProductSlugException() {
        when(productRepository.existsByNameIgnoreCase("Wireless Mouse")).thenReturn(false);
        when(productRepository.existsBySlug("wireless-mouse")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(
                "Wireless Mouse", null, BigDecimal.TEN, 1, 7L
        )).isInstanceOf(DuplicateProductSlugException.class);

        verifyNoInteractions(categoryRepository);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void execute_whenCategoryDoesNotExist_throwsCategoryNotFoundException() {
        when(productRepository.existsByNameIgnoreCase("Wireless Mouse")).thenReturn(false);
        when(productRepository.existsBySlug("wireless-mouse")).thenReturn(false);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(
                "Wireless Mouse", null, BigDecimal.TEN, 1, 99L
        )).isInstanceOf(CategoryNotFoundException.class);

        verify(productRepository, never()).save(any(Product.class));
    }

    private Category category() {
        return Category.builder().id(7L).name("Electronics").slug("electronics").build();
    }

    private Product product() {
        Product product = new Product();
        product.setName("Wireless Mouse");
        product.setSlug("wireless-mouse");
        return product;
    }
}
