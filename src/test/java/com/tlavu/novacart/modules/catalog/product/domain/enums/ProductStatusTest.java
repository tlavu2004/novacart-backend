package com.tlavu.novacart.modules.catalog.product.domain.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class ProductStatusTest {

    @ParameterizedTest
    @CsvSource({
            "DRAFT, ACTIVE",
            "ACTIVE, INACTIVE",
            "INACTIVE, ACTIVE"
    })
    void canTransitionTo_whenTransitionIsAllowed_returnsTrue(
            ProductStatus current,
            ProductStatus target
    ) {
        assertThat(current.canTransitionTo(target)).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
            "DRAFT, DRAFT",
            "DRAFT, INACTIVE",
            "ACTIVE, DRAFT",
            "ACTIVE, ACTIVE",
            "INACTIVE, DRAFT",
            "INACTIVE, INACTIVE"
    })
    void canTransitionTo_whenTransitionIsNotAllowed_returnsFalse(
            ProductStatus current,
            ProductStatus target
    ) {
        assertThat(current.canTransitionTo(target)).isFalse();
    }

    @Test
    void everyStatusTransitionIsExplicitlyCovered() {
        for (ProductStatus current : ProductStatus.values()) {
            for (ProductStatus target : ProductStatus.values()) {
                assertThat(current.canTransitionTo(target))
                        .isEqualTo(switch (current) {
                            case DRAFT, INACTIVE -> target == ProductStatus.ACTIVE;
                            case ACTIVE -> target == ProductStatus.INACTIVE;
                        });
            }
        }
    }
}
