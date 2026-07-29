package com.tlavu.novacart.modules.catalog.category.application.usecase;

import com.tlavu.novacart.modules.catalog.category.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.category.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.shared.domain.repository.query.PageRequest;
import com.tlavu.novacart.modules.catalog.shared.domain.repository.query.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    public PageResult<Category> execute(PageRequest pageRequest) {

        return categoryRepository.findAll(pageRequest);
    }
}
