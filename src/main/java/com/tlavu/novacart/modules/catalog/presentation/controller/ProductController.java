package com.tlavu.novacart.modules.catalog.presentation.controller;

import com.tlavu.novacart.modules.catalog.application.usecase.*;
import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.infrastructure.validation.SortValidator;
import com.tlavu.novacart.modules.catalog.presentation.dto.request.*;
import com.tlavu.novacart.modules.catalog.presentation.dto.response.ProductResponse;
import com.tlavu.novacart.shared.presentation.dto.response.ApiResponse;
import com.tlavu.novacart.shared.presentation.dto.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final GetProductByIdUseCase getProductByIdUseCase;
    private final ListProductsUseCase listProductsUseCase;
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final UpdateProductStatusUseCase updateProductStatusUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final DeleteProductUseCase deleteProductUseCase;

    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of(
            "name",
            "price",
            "stockQuantity",
            "createdAt",
            "updatedAt"
    );

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable
            Long id
    ) {

        Product product = getProductByIdUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(ProductResponse.from(product)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> listProducts(
            @ModelAttribute ProductFilterRequest filter,
            Pageable pageable
    ) {

        SortValidator.validate(pageable, ALLOWED_SORT_PROPERTIES);

        Page<Product> pages = listProductsUseCase.execute(filter, pageable);

        List<ProductResponse> content = pages.getContent()
                .stream()
                .map(ProductResponse::from)
                .toList();

        PageResponse<ProductResponse> response = new PageResponse<>(
                content,
                pages.getNumber(),
                pages.getSize(),
                pages.getTotalElements(),
                pages.getTotalPages()
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }

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

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable
            Long id,

            @Valid
            @RequestBody
            UpdateProductRequest request
    ) {

        Product product = updateProductUseCase.execute(
                id,
                request.name(),
                request.description(),
                request.price(),
                request.categoryId()
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(ProductResponse.from(product)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProductStatus(
            @PathVariable
            Long id,

            @Valid
            @RequestBody
            UpdateProductStatusRequest request
    ) {

        Product product = updateProductStatusUseCase.execute(
                id,
                request.status()
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(ProductResponse.from(product)));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProductStock(
            @PathVariable
            Long id,

            @Valid
            @RequestBody
            UpdateProductStockRequest request
    ) {

        Product product = updateProductStockUseCase.execute(
                id,
                request.stockQuantity()
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(ProductResponse.from(product)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable
            Long id
    ) {

        deleteProductUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
