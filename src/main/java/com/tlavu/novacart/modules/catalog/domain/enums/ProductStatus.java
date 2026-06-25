package com.tlavu.novacart.modules.catalog.domain.enums;

public enum ProductStatus {

    DRAFT,
    ACTIVE,
    INACTIVE;

    public boolean canTransitionTo(ProductStatus target) {
        return switch (this) {
            case DRAFT, INACTIVE -> target == ACTIVE;
            case ACTIVE -> target == INACTIVE;
        };
    }
}
