package com.tlavu.novacart.modules.catalog.product.application.usecase;

import com.tlavu.novacart.modules.catalog.product.application.exception.ProductNotFoundException;
import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.product.domain.enums.ProductStatus;
import com.tlavu.novacart.modules.catalog.product.application.exception.InvalidProductStatusTransitionException;
import com.tlavu.novacart.modules.catalog.product.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProductStatusUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UpdateProductStatusUseCase useCase;

    @Test
    void execute_whenTransitionIsValid_updatesAndSavesProduct() {
        Product product = product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product result = useCase.execute(1L, ProductStatus.ACTIVE);

        assertThat(result).isSameAs(product);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
        verify(productRepository).save(product);
    }

    @Test
    void execute_whenTransitionIsInvalid_throwsAndDoesNotSave() {
        Product product = product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> useCase.execute(1L, ProductStatus.INACTIVE))
                .isInstanceOf(InvalidProductStatusTransitionException.class)
                .hasMessageContaining("DRAFT")
                .hasMessageContaining("INACTIVE");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void execute_whenProductDoesNotExist_throwsProductNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99L, ProductStatus.ACTIVE))
                .isInstanceOf(ProductNotFoundException.class);
        verify(productRepository, never()).save(any(Product.class));
    }

    private Product product() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Wireless Mouse");
        return product;
    }
}
