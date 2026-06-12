package com.tlavu.novacart.modules.catalog.presentation.controller;

import com.tlavu.novacart.modules.catalog.application.usecase.CreateCategoryUseCase;
import com.tlavu.novacart.modules.catalog.application.usecase.GetCategoryByIdUseCase;
import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.presentation.dto.request.CreateCategoryRequest;
import com.tlavu.novacart.modules.catalog.presentation.dto.response.CategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final GetCategoryByIdUseCase getCategoryByIdUseCase;
    private final CreateCategoryUseCase createCategoryUseCase;

    @GetMapping()
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long id
    ) {

        Category category = getCategoryByIdUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CategoryResponse.from(category));
    }

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

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoryResponse.from(category));
    }
}
