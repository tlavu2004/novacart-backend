package com.tlavu.novacart.modules.catalog.product.application.port.out;

import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;

public interface ProductSlugWriteAttemptPort {

    Product persistAndFlush(Product product);
}
