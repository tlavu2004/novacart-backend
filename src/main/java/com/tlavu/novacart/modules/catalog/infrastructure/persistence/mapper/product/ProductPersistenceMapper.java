package com.tlavu.novacart.modules.catalog.infrastructure.persistence.mapper.product;

import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.mapper.category.CategoryPersistenceMapper;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.mapper.config.MapStructPersistenceConfig;
import org.mapstruct.Mapper;

@Mapper(
        config = MapStructPersistenceConfig.class,
        uses = CategoryPersistenceMapper.class
)
public abstract class ProductPersistenceMapper {

    public abstract ProductJpaEntity toJpaEntity(Product product);

    abstract ProductRehydrationState toRehydration(ProductJpaEntity entity);

    public Product toDomain(ProductJpaEntity entity) {

        if (entity == null) {
            return null;
        }

        ProductRehydrationState state = toRehydration(entity);

        return Product.rehydrate(
                state.id(),
                state.name(),
                state.description(),
                state.slug(),
                state.status(),
                state.price(),
                state.stockQuantity(),
                state.category(),
                state.createdAt(),
                state.updatedAt(),
                state.deletedAt()
        );
    }
}
