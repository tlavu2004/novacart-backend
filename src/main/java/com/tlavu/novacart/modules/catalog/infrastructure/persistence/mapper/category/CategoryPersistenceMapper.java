package com.tlavu.novacart.modules.catalog.infrastructure.persistence.mapper.category;

import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.mapper.config.MapStructPersistenceConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructPersistenceConfig.class)
public abstract class CategoryPersistenceMapper {

    public abstract CategoryJpaEntity toJpaEntity(Category category);

    abstract CategoryRehydrationState toRehydration(CategoryJpaEntity entity);

    public Category toDomain(CategoryJpaEntity entity) {

        if (entity == null) {
            return null;
        }

        CategoryRehydrationState state = toRehydration(entity);

        return Category.rehydrate(
                state.id(),
                state.name(),
                state.description(),
                state.slug(),
                state.active(),
                state.createdAt(),
                state.updatedAt(),
                state.deletedAt()
        );
    }
}
