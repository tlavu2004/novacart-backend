package com.tlavu.novacart.modules.catalog.product.application.exception;

import com.tlavu.novacart.modules.catalog.shared.application.exception.code.catalog.CatalogErrorCode;
import com.tlavu.novacart.shared.application.exception.base.BaseException;
import org.springframework.http.HttpStatus;

public class SlugGenerationFailedException extends BaseException {

    public SlugGenerationFailedException(String baseSlug, int maxAttempts) {
        super(
                CatalogErrorCode.PRODUCT_SLUG_GENERATION_FAILED,
                HttpStatus.CONFLICT,
                "Unable to generate a unique product slug from '%s' after %d attempts"
                        .formatted(baseSlug, maxAttempts)
        );
    }
}
