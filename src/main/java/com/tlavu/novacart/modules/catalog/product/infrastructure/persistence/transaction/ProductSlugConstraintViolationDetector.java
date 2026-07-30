package com.tlavu.novacart.modules.catalog.product.infrastructure.persistence.transaction;

import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class ProductSlugConstraintViolationDetector {

    public static final String PRODUCT_SLUG_UNIQUE_CONSTRAINT = "uk_products_slug";
    private static final String UNIQUE_VIOLATION_SQL_STATE = "23505";

    public boolean isSlugUniqueViolation(DataIntegrityViolationException exception) {
        Throwable current = exception;

        while (current != null) {
            if (current instanceof PSQLException postgresException
                    && UNIQUE_VIOLATION_SQL_STATE.equals(postgresException.getSQLState())
                    && postgresException.getServerErrorMessage() != null
                    && PRODUCT_SLUG_UNIQUE_CONSTRAINT.equals(
                            postgresException.getServerErrorMessage().getConstraint()
                    )) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }
}
