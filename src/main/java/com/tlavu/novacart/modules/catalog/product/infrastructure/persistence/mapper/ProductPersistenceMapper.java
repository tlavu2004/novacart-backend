package com.tlavu.novacart.modules.catalog.product.infrastructure.persistence.mapper;

import com.tlavu.novacart.modules.catalog.category.infrastructure.persistence.mapper.CategoryPersistenceMapper;
import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.product.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import com.tlavu.novacart.modules.catalog.shared.infrastructure.persistence.mapper.MapStructPersistenceConfig;
import org.mapstruct.Mapper;

@Mapper(
        config = MapStructPersistenceConfig.class,
        uses = CategoryPersistenceMapper.class
)
public interface ProductPersistenceMapper {

    ProductJpaEntity toJpaEntity(Product product);

    Product toDomain(ProductJpaEntity entity);
}
