package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.domain.repository.ProductRepository;
import com.tlavu.novacart.modules.catalog.domain.repository.query.PageRequest;
import com.tlavu.novacart.modules.catalog.domain.repository.query.PageResult;
import com.tlavu.novacart.modules.catalog.domain.repository.query.ProductFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListProductsUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ListProductsUseCase useCase;

    @Test
    void execute_withFilterAndPageRequest_returnsRepositoryPage() {

        ProductFilter filter = new ProductFilter(
                "mouse", null, 7L, new BigDecimal("10.00"), new BigDecimal("50.00")
        );
        PageRequest pageRequest = new PageRequest(1, 10, List.of());
        PageResult<Product> expected = new PageResult<>(List.of(product()), 1, 10, 1, 1);
        when(productRepository.findAll(filter, pageRequest)).thenReturn(expected);

        PageResult<Product> result = useCase.execute(filter, pageRequest);

        assertThat(result).isSameAs(expected);
        verify(productRepository).findAll(filter, pageRequest);
    }

    @Test
    void execute_withEmptyFilter_delegatesToRepository() {

        ProductFilter filter = new ProductFilter(null, null, null, null, null);
        PageRequest pageRequest = new PageRequest(0, 20, List.of());
        PageResult<Product> expected = new PageResult<>(List.of(), 0, 20, 0, 0);
        when(productRepository.findAll(filter, pageRequest)).thenReturn(expected);

        assertThat(useCase.execute(filter, pageRequest)).isSameAs(expected);

        verify(productRepository).findAll(filter, pageRequest);
    }

    private Product product() {
        Product product = new Product();
        product.setName("Wireless Mouse");
        return product;
    }
}
