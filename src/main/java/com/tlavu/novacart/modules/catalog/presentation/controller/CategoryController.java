package com.tlavu.novacart.modules.catalog.presentation.controller;

import com.tlavu.novacart.modules.catalog.application.usecase.CreateCategoryUseCase;
import com.tlavu.novacart.modules.catalog.application.usecase.GetCategoryByIdUseCase;
import com.tlavu.novacart.modules.catalog.application.usecase.ListCategoriesUseCase;
import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.presentation.dto.request.CreateCategoryRequest;
import com.tlavu.novacart.modules.catalog.presentation.dto.response.CategoryResponse;
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

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long id
    ) {

        Category category = getCategoryByIdUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CategoryResponse.from(category));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> listCategories() {

        List<Category> categories = listCategoriesUseCase.execute();

        List<CategoryResponse> response = categories.stream()
                .map(CategoryResponse::from)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
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
