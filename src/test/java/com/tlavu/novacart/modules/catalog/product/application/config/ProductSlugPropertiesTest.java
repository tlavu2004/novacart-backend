package com.tlavu.novacart.modules.catalog.product.application.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductSlugPropertiesTest {

    @Test
    void defaultsToTwentyAttempts() {
        assertThat(new ProductSlugProperties().getMaxAttempts()).isEqualTo(20);
    }
}
