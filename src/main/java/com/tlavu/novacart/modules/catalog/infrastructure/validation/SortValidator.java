package com.tlavu.novacart.modules.catalog.infrastructure.validation;

import com.tlavu.novacart.modules.catalog.application.exception.InvalidSortFieldException;
import com.tlavu.novacart.shared.application.exception.code.ErrorCode;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Pageable;

import java.util.Set;

@UtilityClass
public class SortValidator {

    public void validate(Pageable pageable, Set<String> allowedSortProperties) {

        pageable.getSort().forEach(order -> {
            if (!allowedSortProperties.contains(order.getProperty())) {
                throw new InvalidSortFieldException(
                        ErrorCode.INVALID_SORT_FIELD,
                        "Invalid sort field: '%s'. Allowed: %s".formatted(order.getProperty(), allowedSortProperties)
                );
            }
        });
    }
}
