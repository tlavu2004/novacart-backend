package com.tlavu.novacart.modules.catalog.product.application.service;

import com.tlavu.novacart.modules.catalog.product.application.exception.SlugGenerationFailedException;
import com.tlavu.novacart.modules.catalog.product.application.port.out.ProductSlugConstraintViolationPort;
import com.tlavu.novacart.modules.catalog.product.application.port.out.ProductSlugWriteAttemptPort;
import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.product.domain.repository.ProductRepository;
import com.tlavu.novacart.modules.catalog.shared.domain.util.SlugUtils;
import com.tlavu.novacart.shared.application.exception.code.global.GlobalErrorCode;
import com.tlavu.novacart.shared.application.exception.common.InvalidInputException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSlugAllocationService {

    private static final int DEFAULT_MAX_ATTEMPTS = 20;

    private final ProductRepository productRepository;
    private final ProductSlugWriteAttemptPort productSlugWriteAttemptPort;
    private final ProductSlugConstraintViolationPort productSlugConstraintViolationPort;

    public Product allocateAndPersist(Product product) {
        String baseSlug = SlugUtils.generate(product.getName());

        if (baseSlug.isBlank()) {
            throw new InvalidInputException(
                    GlobalErrorCode.INVALID_INPUT,
                    "Product name must contain at least one letter or number"
            );
        }

        for (int candidateNumber = 1; candidateNumber <= DEFAULT_MAX_ATTEMPTS; candidateNumber++) {
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

        throw new SlugGenerationFailedException(baseSlug, DEFAULT_MAX_ATTEMPTS);
    }
}
