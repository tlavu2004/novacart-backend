package com.tlavu.novacart.modules.catalog.category.application.usecase;

import com.tlavu.novacart.modules.catalog.category.application.exception.specific.CategoryNotFoundException;
import com.tlavu.novacart.modules.catalog.category.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.category.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCategoryByIdUseCase {

    private final CategoryRepository categoryRepository;

    public Category execute(Long id) {

        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }
}
