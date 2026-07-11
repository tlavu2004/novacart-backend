package com.tlavu.novacart.modules.catalog.infrastructure.validation;

import com.tlavu.novacart.shared.application.exception.code.global.GlobalErrorCode;
import com.tlavu.novacart.shared.application.exception.common.InvalidSortFieldException;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Pageable;

import java.util.Set;

/**
 * Validates that all sort properties in a {@link Pageable} are within an allowed set,
 * preventing {@link org.springframework.data.mapping.PropertyReferenceException}
 * from leaking as an unhandled 500 error.
 *
 * <p><b>IMPORTANT:</b> Must be called explicitly in every Controller that accepts
 * a {@link Pageable} parameter, right after receiving it and before passing it
 * to any UseCase or Repository. This is NOT automatically enforced —
 * forgetting to call it will cause invalid sort fields to throw
 * {@link org.springframework.data.mapping.PropertyReferenceException}, resulting in
 * an unhandled 500 response instead of a proper 400.
 *
 * <p>Example:
 * <pre>{@code
 * @GetMapping
 * public ... list(Pageable pageable) {
 *     SortValidator.validate(pageable, ALLOWED_SORT_PROPERTIES);
 *     ...
 * }
 * }</pre>
 */
@UtilityClass
public class SortValidator {

    public void validate(Pageable pageable, Set<String> allowedSortProperties) {

        pageable.getSort().forEach(order -> {
            if (!allowedSortProperties.contains(order.getProperty())) {
                throw new InvalidSortFieldException(
                        GlobalErrorCode.INVALID_SORT_FIELD,
                        "Invalid sort field: '%s'. Allowed: %s".formatted(order.getProperty(), allowedSortProperties)
                );
            }
        });
    }
}
