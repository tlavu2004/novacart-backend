package com.tlavu.novacart.modules.catalog.infrastructure.validation;

import com.tlavu.novacart.shared.application.exception.common.InvalidSortFieldException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SortValidatorTest {

    private static final Set<String> ALLOWED_PROPERTIES = Set.of(
            "name",
            "price",
            "createdAt"
    );

    @Test
    void validate_whenPageableHasNoSort_acceptsIt() {
        assertThatCode(() -> SortValidator.validate(
                PageRequest.of(0, 20),
                ALLOWED_PROPERTIES
        )).doesNotThrowAnyException();
    }

    @Test
    void validate_whenAllSortPropertiesAreAllowed_acceptsMultipleOrders() {
        Sort sort = Sort.by(
                Sort.Order.asc("name"),
                Sort.Order.desc("price"),
                Sort.Order.asc("createdAt")
        );

        assertThatCode(() -> SortValidator.validate(
                PageRequest.of(0, 20, sort),
                ALLOWED_PROPERTIES
        )).doesNotThrowAnyException();
    }

    @Test
    void validate_whenSortPropertyIsNotAllowed_throwsInvalidSortFieldException() {
        assertThatThrownBy(() -> SortValidator.validate(
                PageRequest.of(0, 20, Sort.by("unknown")),
                ALLOWED_PROPERTIES
        ))
                .isInstanceOf(InvalidSortFieldException.class)
                .hasMessageContaining("Invalid sort field: 'unknown'")
                .hasMessageContaining("Allowed:");
    }
}
