package com.tlavu.novacart.modules.catalog.category.infrastructure.persistence.mapper.entity;

import com.tlavu.novacart.modules.catalog.category.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.category.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import com.tlavu.novacart.modules.catalog.shared.infrastructure.persistence.mapper.config.MapStructPersistenceConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructPersistenceConfig.class)
public interface CategoryPersistenceMapper {

    CategoryJpaEntity toJpaEntity(Category category);

    Category toDomain(CategoryJpaEntity entity);
}
