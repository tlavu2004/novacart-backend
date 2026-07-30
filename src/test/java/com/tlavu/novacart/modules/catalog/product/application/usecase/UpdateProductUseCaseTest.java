package com.tlavu.novacart.modules.catalog.product.application.usecase;

import com.tlavu.novacart.modules.catalog.category.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.product.application.exception.ProductVersionConflictException;
import com.tlavu.novacart.modules.catalog.product.application.service.ProductSlugAllocationService;
import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.product.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class UpdateProductUseCaseTest {

    @Mock private ProductRepository productRepository;
    @Mock private ProductSlugAllocationService productSlugAllocationService;
    @InjectMocks private UpdateProductUseCase useCase;

    @Test
    void execute_whenNameChanges_delegatesSlugAllocation() {
        Product product = product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productSlugAllocationService.allocateAndPersist(product)).thenReturn(product);

        Product result = useCase.execute(1L, 0L, "Keyboard", null, null, null);

        assertThat(result).isSameAs(product);
        assertThat(product.getName()).isEqualTo("Keyboard");
        verify(productSlugAllocationService).allocateAndPersist(product);
        verify(productRepository, never()).save(any());
    }

    @Test
    void execute_whenNameIsUnchanged_keepsSlugAndUsesRegularSave() {
        Product product = product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        useCase.execute(1L, 0L, "Wireless Mouse", "Updated", null, null);

        assertThat(product.getSlug()).isEqualTo("wireless-mouse");
        verify(productSlugAllocationService, never()).allocateAndPersist(any());
        verify(productRepository).save(product);
    }

    @Test
    void execute_whenVersionIsStale_throwsConflictBeforeChangingProduct() {
        Product product = product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> useCase.execute(1L, 1L, "Keyboard", null, null, null))
                .isInstanceOf(ProductVersionConflictException.class);

        verifyNoInteractions(productSlugAllocationService);
        verify(productRepository, never()).save(any());
    }

    private Product product() {
        Product product = new Product();
        product.setId(1L);
        product.setVersion(0L);
        product.setName("Wireless Mouse");
        product.setSlug("wireless-mouse");
        product.setDescription("Original");
        product.setPrice(new BigDecimal("19.90"));
        product.setCategory(Category.builder().id(1L).name("Electronics").slug("electronics").build());
        return product;
    }
}
