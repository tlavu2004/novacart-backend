package com.tlavu.novacart.modules.catalog.presentation.controller;

import com.tlavu.novacart.modules.catalog.application.usecase.CreateCategoryUseCase;
import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.presentation.dto.request.CreateCategoryRequest;
import com.tlavu.novacart.modules.catalog.presentation.dto.response.CategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CreateCategoryUseCase createCategoryUseCase;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid
            @RequestBody
            CreateCategoryRequest request
    ) {

        Category category = createCategoryUseCase.execute(
                request.name(),
                request.description()
        );

        CategoryResponse categoryResponse = new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryResponse);
    }
}
