package com.tlavu.novacart.modules.catalog.presentation.controller;

import com.tlavu.novacart.modules.catalog.application.usecase.*;
import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.presentation.dto.request.CreateCategoryRequest;
import com.tlavu.novacart.modules.catalog.presentation.dto.request.UpdateCategoryRequest;
import com.tlavu.novacart.modules.catalog.presentation.dto.response.CategoryResponse;
import com.tlavu.novacart.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final GetCategoryByIdUseCase getCategoryByIdUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;
    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @PathVariable Long id
    ) {

        Category category = getCategoryByIdUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(CategoryResponse.from(category)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> listCategories() {

        List<Category> categories = listCategoriesUseCase.execute();

        List<CategoryResponse> response = categories.stream()
                .map(CategoryResponse::from)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid
            @RequestBody
            CreateCategoryRequest request
    ) {

        Category category = createCategoryUseCase.execute(
                request.name(),
                request.description()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(CategoryResponse.from(category)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable
            Long id,

            @Valid
            @RequestBody
            UpdateCategoryRequest request
    ) {

        Category category = updateCategoryUseCase.execute(
                id,
                request.name(),
                request.description(),
                request.active()
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(CategoryResponse.from(category)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable Long id
    ) {

        deleteCategoryUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
