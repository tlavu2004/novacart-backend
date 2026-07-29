package com.tlavu.novacart.modules.catalog.product.domain.entity;

import com.tlavu.novacart.modules.catalog.product.domain.enums.ProductStatus;
import com.tlavu.novacart.modules.catalog.product.domain.exception.InvalidProductStatusTransitionException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    void newProduct_startsInDraftStatus() {
        Product product = new Product();

        assertThat(product.getStatus()).isEqualTo(ProductStatus.DRAFT);
    }

    @Test
    void changeStatus_whenTransitionIsAllowed_updatesStatus() {
        Product product = new Product();

        product.changeStatus(ProductStatus.ACTIVE);
        product.changeStatus(ProductStatus.INACTIVE);
        product.changeStatus(ProductStatus.ACTIVE);

        assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void changeStatus_whenTransitionIsNotAllowed_throwsDomainExceptionAndKeepsStatus() {
        Product product = new Product();

        assertThatThrownBy(() -> product.changeStatus(ProductStatus.INACTIVE))
                .isInstanceOf(InvalidProductStatusTransitionException.class)
                .hasMessage("Cannot transition product status from DRAFT to INACTIVE");

        assertThat(product.getStatus()).isEqualTo(ProductStatus.DRAFT);
    }

    @Test
    void changeStatus_whenTargetIsNull_throwsDomainExceptionAndKeepsStatus() {
        Product product = new Product();

        assertThatThrownBy(() -> product.changeStatus(null))
                .isInstanceOf(InvalidProductStatusTransitionException.class)
                .hasMessage("Cannot transition product status from DRAFT to null");

        assertThat(product.getStatus()).isEqualTo(ProductStatus.DRAFT);
    }
}
