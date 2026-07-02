package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.application.exception.ResourceNotFoundException;
import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
import com.tlavu.novacart.shared.application.exception.code.ErrorCode;
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
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CATEGORY_NOT_FOUND,
                        "Category with id=%d not found".formatted(id)
                ));
    }
}
