package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.domain.repository.ProductRepository;
import com.tlavu.novacart.modules.catalog.presentation.dto.request.ProductFilterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListProductsUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ListProductsUseCase useCase;

    @Test
    void execute_withFilterAndPageable_returnsRepositoryPage() {
        ProductFilterRequest filter = new ProductFilterRequest(
                "mouse", null, 7L, new BigDecimal("10.00"), new BigDecimal("50.00")
        );
        Pageable pageable = PageRequest.of(1, 10);
        Page<Product> expected = new PageImpl<>(List.of(product()), pageable, 1);
        when(productRepository.findAll(any(), same(pageable))).thenReturn(expected);

        Page<Product> result = useCase.execute(filter, pageable);

        assertThat(result).isSameAs(expected);
        verify(productRepository).findAll(
                argThat(Objects::nonNull),
                same(pageable)
        );
    }
    
    @Test
    void execute_withEmptyFilter_stillBuildsSpecificationAndDelegates() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> expected = Page.empty(pageable);
        when(productRepository.findAll(any(), same(pageable))).thenReturn(expected);

        assertThat(useCase.execute(
                new ProductFilterRequest(null, null, null, null, null), pageable
        )).isSameAs(expected);

        verify(productRepository).findAll(any(), same(pageable));
    }

    private Product product() {
        Product product = new Product();
        product.setName("Wireless Mouse");
        return product;
    }
}
