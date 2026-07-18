package com.tlavu.novacart.modules.catalog.infrastructure.validation;

import com.tlavu.novacart.shared.application.exception.code.global.GlobalErrorCode;
import com.tlavu.novacart.shared.application.exception.common.InvalidSortFieldException;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Pageable;

import java.util.Set;

/**
 * Validates that every sort property in a {@link Pageable} belongs to an allowed set,
 * preventing {@link org.springframework.data.mapping.PropertyReferenceException}
 * from occurring in the first place and ensuring a consistent {@code 400 Bad Request}
 * response for invalid sort fields.
 *
 * <p><b>IMPORTANT:</b> Each controller that accepts a {@link Pageable} should call this
 * validator immediately after receiving the request and before passing the pageable to
 * a use case or repository. This is not enforced automatically. The global exception
 * handler also maps a {@link org.springframework.data.mapping.PropertyReferenceException}
 * to {@code 400 Bad Request} as a safety net, but validating at the boundary gives the
 * client the dedicated invalid-sort error and avoids unnecessary repository work.
 *
 * <p>Example:
 * <pre>{@code
 * private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of(
 *         "name", "price", "createdAt", "updatedAt"
 * );
 *
 * @GetMapping
 * public ResponseEntity<?> listProducts(Pageable pageable) {
 *     SortValidator.validate(pageable, ALLOWED_SORT_PROPERTIES);
 *     return ...; // pass the validated pageable to the use case
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
