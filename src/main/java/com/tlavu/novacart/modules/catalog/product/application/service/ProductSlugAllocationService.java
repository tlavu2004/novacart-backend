package com.tlavu.novacart.modules.catalog.product.application.service;

import com.tlavu.novacart.modules.catalog.product.application.exception.SlugGenerationFailedException;
import com.tlavu.novacart.modules.catalog.product.application.config.ProductSlugProperties;
import com.tlavu.novacart.modules.catalog.product.application.port.out.ProductSlugConstraintViolationPort;
import com.tlavu.novacart.modules.catalog.product.application.port.out.ProductSlugWriteAttemptPort;
import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.product.domain.repository.ProductRepository;
import com.tlavu.novacart.modules.catalog.shared.domain.util.SlugUtils;
import com.tlavu.novacart.shared.application.exception.code.global.GlobalErrorCode;
import com.tlavu.novacart.shared.application.exception.common.InvalidInputException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSlugAllocationService {

    private static final String SLUG_GENERATION_FAILURE_METRIC = "catalog.product.slug.generation.failed";

    private final ProductRepository productRepository;
    private final ProductSlugWriteAttemptPort productSlugWriteAttemptPort;
    private final ProductSlugConstraintViolationPort productSlugConstraintViolationPort;
    private final ProductSlugProperties productSlugProperties;
    private final MeterRegistry meterRegistry;

    public Product allocateAndPersist(Product product) {
        String baseSlug = SlugUtils.generate(product.getName());

        if (baseSlug.isBlank()) {
            throw new InvalidInputException(
                    GlobalErrorCode.INVALID_INPUT,
                    "Product name must contain at least one letter or number"
            );
        }

        int maxAttempts = productSlugProperties.getMaxAttempts();

        for (int candidateNumber = 1; candidateNumber <= maxAttempts; candidateNumber++) {
            String candidate = SlugUtils.createCandidate(baseSlug, candidateNumber);

            if (productRepository.isSlugReserved(candidate)) {
                continue;
            }

            product.setSlug(candidate);

            try {
                return productSlugWriteAttemptPort.persistAndFlush(product);
            } catch (DataIntegrityViolationException exception) {
                if (!productSlugConstraintViolationPort.isSlugUniqueViolation(exception)) {
                    throw exception;
                }
            }
        }

        log.error(
                "event=product_slug_generation_failed base_slug={} max_attempts={}",
                baseSlug,
                maxAttempts
        );
        meterRegistry.counter(SLUG_GENERATION_FAILURE_METRIC).increment();

        throw new SlugGenerationFailedException(baseSlug, maxAttempts);
    }
}
