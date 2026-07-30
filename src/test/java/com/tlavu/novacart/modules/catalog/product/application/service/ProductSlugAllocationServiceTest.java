package com.tlavu.novacart.modules.catalog.product.application.service;

import com.tlavu.novacart.modules.catalog.category.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.product.application.config.ProductSlugProperties;
import com.tlavu.novacart.modules.catalog.product.application.exception.SlugGenerationFailedException;
import com.tlavu.novacart.modules.catalog.product.application.port.out.ProductSlugConstraintViolationPort;
import com.tlavu.novacart.modules.catalog.product.application.port.out.ProductSlugWriteAttemptPort;
import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.product.domain.repository.ProductRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductSlugAllocationServiceTest {

    @Test
    void allocateAndPersist_whenBaseIsReserved_usesNextCandidate() {
        ProductRepository repository = mock(ProductRepository.class);
        ProductSlugWriteAttemptPort writer = mock(ProductSlugWriteAttemptPort.class);
        ProductSlugConstraintViolationPort detector = mock(ProductSlugConstraintViolationPort.class);
        ProductSlugProperties properties = properties(2);
        Product product = product("Áo thun trắng");
        when(repository.isSlugReserved("ao-thun-trang")).thenReturn(true);
        when(repository.isSlugReserved("ao-thun-trang-2")).thenReturn(false);
        when(writer.persistAndFlush(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = service(repository, writer, detector, properties, new SimpleMeterRegistry())
                .allocateAndPersist(product);

        assertThat(result.getSlug()).isEqualTo("ao-thun-trang-2");
        verify(writer).persistAndFlush(product);
    }

    @Test
    void allocateAndPersist_whenAttemptsAreExhausted_incrementsMetricAndThrowsFailure() {
        ProductRepository repository = mock(ProductRepository.class);
        ProductSlugWriteAttemptPort writer = mock(ProductSlugWriteAttemptPort.class);
        ProductSlugConstraintViolationPort detector = mock(ProductSlugConstraintViolationPort.class);
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        when(repository.isSlugReserved(anyString())).thenReturn(true);

        assertThatThrownBy(() -> service(repository, writer, detector, properties(2), registry)
                .allocateAndPersist(product("Ao thun trắng")))
                .isInstanceOf(SlugGenerationFailedException.class)
                .hasMessageContaining("after 2 attempts");

        assertThat(registry.counter("catalog.product.slug.generation.failed").count()).isEqualTo(1);
        verifyNoInteractions(writer);
    }

    @Test
    void allocateAndPersist_whenWriteFailsForAnotherConstraint_doesNotRetry() {
        ProductRepository repository = mock(ProductRepository.class);
        ProductSlugWriteAttemptPort writer = mock(ProductSlugWriteAttemptPort.class);
        ProductSlugConstraintViolationPort detector = mock(ProductSlugConstraintViolationPort.class);
        when(repository.isSlugReserved(anyString())).thenReturn(false);
        org.springframework.dao.DataIntegrityViolationException failure =
                new org.springframework.dao.DataIntegrityViolationException("other");
        doThrow(failure).when(writer).persistAndFlush(any());
        when(detector.isSlugUniqueViolation(failure)).thenReturn(false);

        assertThatThrownBy(() -> service(repository, writer, detector, properties(2), new SimpleMeterRegistry())
                .allocateAndPersist(product("Ao thun trắng"))).isSameAs(failure);

        verify(writer, times(1)).persistAndFlush(any());
    }

    private ProductSlugAllocationService service(
            ProductRepository repository,
            ProductSlugWriteAttemptPort writer,
            ProductSlugConstraintViolationPort detector,
            ProductSlugProperties properties,
            SimpleMeterRegistry registry
    ) {
        return new ProductSlugAllocationService(repository, writer, detector, properties, registry);
    }

    private ProductSlugProperties properties(int maxAttempts) {
        ProductSlugProperties properties = new ProductSlugProperties();
        properties.setMaxAttempts(maxAttempts);
        return properties;
    }

    private Product product(String name) {
        return Product.create(
                name, null, null, BigDecimal.TEN, 1,
                Category.builder().id(1L).name("Category").slug("category").build()
        );
    }
}
