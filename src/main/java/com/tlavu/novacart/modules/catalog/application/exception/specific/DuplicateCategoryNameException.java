package com.tlavu.novacart.modules.catalog.application.exception.specific;

import com.tlavu.novacart.modules.catalog.application.exception.code.CatalogErrorCode;
import com.tlavu.novacart.shared.application.exception.common.ConflictException;

public class DuplicateCategoryNameException extends ConflictException {

    public DuplicateCategoryNameException(String name) {

        super(
                CatalogErrorCode.CATEGORY_ALREADY_EXISTS,
                "Category with name '%s' already exists".formatted(name)
        );
    }
}
