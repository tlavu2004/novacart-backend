package com.tlavu.novacart.modules.catalog.product.application.port.out;

import org.springframework.dao.DataIntegrityViolationException;

public interface ProductSlugConstraintViolationPort {

    boolean isSlugUniqueViolation(DataIntegrityViolationException exception);
}
