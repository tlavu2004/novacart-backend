package com.tlavu.novacart.modules.catalog.category.application.port.out;

public interface CategoryProductUsagePort {

    boolean hasProductsByCategoryId(Long categoryId);
}
