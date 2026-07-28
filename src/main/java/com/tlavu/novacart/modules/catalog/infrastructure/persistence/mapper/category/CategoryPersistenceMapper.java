package com.tlavu.novacart.modules.catalog.infrastructure.persistence.mapper.category;

import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import com.tlavu.novacart.modules.catalog.infrastructure.persistence.mapper.config.MapStructPersistenceConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructPersistenceConfig.class)
public interface CategoryPersistenceMapper {

    CategoryJpaEntity toJpaEntity(Category category);

    Category toDomain(CategoryJpaEntity entity);
}
