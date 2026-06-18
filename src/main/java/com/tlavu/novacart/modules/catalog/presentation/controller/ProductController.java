package com.tlavu.novacart.modules.catalog.presentation.controller;

import com.tlavu.novacart.modules.catalog.application.usecase.CreateProductUseCase;
import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.presentation.dto.request.CreateProductRequest;
import com.tlavu.novacart.modules.catalog.presentation.dto.response.ProductResponse;
import com.tlavu.novacart.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final CreateProductUseCase createProductUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid
            @RequestBody
            CreateProductRequest request
    ) {

        Product product = createProductUseCase.execute(
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity(),
                request.categoryId()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(ProductResponse.from(product)));
    }
}
