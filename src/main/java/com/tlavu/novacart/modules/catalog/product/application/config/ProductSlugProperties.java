package com.tlavu.novacart.modules.catalog.product.application.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@Getter
@Setter
@ConfigurationProperties(prefix = "catalog.product.slug")
public class ProductSlugProperties {

    @Min(value = 1, message = "Product slug max attempts must be at least 1")
    @Max(value = 50, message = "Product slug max attempts must not exceed 50")
    private int maxAttempts = 20;
}
