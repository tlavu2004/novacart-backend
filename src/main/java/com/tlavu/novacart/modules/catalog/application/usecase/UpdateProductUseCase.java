package com.tlavu.novacart.modules.catalog.application.usecase;

import com.tlavu.novacart.modules.catalog.application.exception.specific.CategoryNotFoundException;
import com.tlavu.novacart.modules.catalog.application.exception.specific.DuplicateProductNameException;
import com.tlavu.novacart.modules.catalog.application.exception.specific.DuplicateProductSlugException;
import com.tlavu.novacart.modules.catalog.application.exception.specific.ProductNotFoundException;
import com.tlavu.novacart.shared.application.exception.code.global.GlobalErrorCode;
import com.tlavu.novacart.shared.application.exception.common.InvalidInputException;

import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.domain.repository.CategoryRepository;
import com.tlavu.novacart.modules.catalog.domain.repository.ProductRepository;
import com.tlavu.novacart.modules.catalog.infrastructure.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateProductUseCase {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public Product execute(
            Long id,
            String name,
            String description,
            BigDecimal price,
            Long categoryId
    ) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (name != null) {

            String normalizedName = name.trim();

            if (normalizedName.isEmpty()) {
                throw new InvalidInputException(
                        GlobalErrorCode.INVALID_INPUT,
                        "Product name must not be blank"
                );
            }

            if (productRepository.existsByNameIgnoreCaseAndIdNot(normalizedName, id)) {
                throw new DuplicateProductNameException(normalizedName);
            }

            String slug = SlugUtils.generate(normalizedName);

            if (productRepository.existsBySlugAndIdNot(slug, id)) {
                throw new DuplicateProductSlugException(slug);
            }

            product.rename(normalizedName, slug);
        }

        if (description != null) {

            product.changeDescription(description);
        }

        if (price != null) {

            product.changePrice(price);
        }

        if (categoryId != null) {

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException(categoryId));

            product.moveToCategory(category);
        }

        return productRepository.save(product);
    }
}
