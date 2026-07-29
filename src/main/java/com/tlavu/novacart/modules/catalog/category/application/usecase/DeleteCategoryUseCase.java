package com.tlavu.novacart.modules.catalog.category.application.usecase;

import com.tlavu.novacart.modules.catalog.category.application.exception.CategoryHasActiveProductsException;
import com.tlavu.novacart.modules.catalog.category.application.exception.CategoryNotFoundException;
import com.tlavu.novacart.modules.catalog.category.application.port.out.CategoryProductUsagePort;
import com.tlavu.novacart.modules.catalog.category.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.category.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final CategoryProductUsagePort categoryProductUsagePort;

    public void execute(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (categoryProductUsagePort.hasProductsByCategoryId(id)) {
            throw new CategoryHasActiveProductsException(id);
        }

        Instant now = Instant.now();

        category.softDelete(now);

        categoryRepository.save(category);
    }
}
