package com.tlavu.novacart.modules.catalog.infrastructure.persistence.mapper.entity;

import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.mapper.config.MapStructPersistenceConfig;
import org.mapstruct.Mapper;

@Mapper(
        config = MapStructPersistenceConfig.class,
        uses = CategoryPersistenceMapper.class
)
public interface ProductPersistenceMapper {

    ProductJpaEntity toJpaEntity(Product product);

    Product toDomain(ProductJpaEntity entity);
}
