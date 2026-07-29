package com.tlavu.novacart.modules.catalog.product.application.usecase;

import com.tlavu.novacart.modules.catalog.product.application.exception.specific.ProductNotFoundException;
import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.product.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProductStockUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UpdateProductStockUseCase useCase;

    @Test
    void execute_whenStockIsZero_updatesAndSavesProduct() {
        Product product = product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product result = useCase.execute(1L, 0);

        assertThat(result).isSameAs(product);
        assertThat(product.getStockQuantity()).isZero();
        verify(productRepository).save(product);
    }

    @Test
    void execute_whenProductDoesNotExist_throwsProductNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99L, 5))
                .isInstanceOf(ProductNotFoundException.class);
        verify(productRepository, never()).save(any(Product.class));
    }

    private Product product() {
        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(4);
        return product;
    }
}
