package com.tlavu.novacart.modules.catalog.product.presentation.dto.request;

import com.tlavu.novacart.modules.catalog.product.domain.enums.ProductStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateProductStatusRequest(
        @NotNull(message = "Product status is required")
        ProductStatus status
) {}
