package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    public Page<Category> execute(Pageable pageable) {

        return categoryRepository.findAll(pageable);
    }
}
