package com.tlavu.novacart.modules.catalog.application.exception.specific;

import com.tlavu.novacart.modules.catalog.application.exception.code.CatalogErrorCode;
import com.tlavu.novacart.shared.application.exception.common.ConflictException;

public class DuplicateCategorySlugException extends ConflictException {

    public DuplicateCategorySlugException(String slug) {

        super(
                CatalogErrorCode.CATEGORY_SLUG_ALREADY_EXISTS,
                "Category with slug '%s' already exists".formatted(slug)
        );
    }
}
