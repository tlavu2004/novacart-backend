package com.tlavu.novacart.modules.catalog.product.presentation.controller;

import com.tlavu.novacart.modules.catalog.product.application.usecase.CreateProductUseCase;
import com.tlavu.novacart.modules.catalog.product.application.usecase.DeleteProductUseCase;
import com.tlavu.novacart.modules.catalog.product.application.usecase.GetProductByIdUseCase;
import com.tlavu.novacart.modules.catalog.product.application.usecase.ListProductsUseCase;
import com.tlavu.novacart.modules.catalog.product.application.usecase.UpdateProductStatusUseCase;
import com.tlavu.novacart.modules.catalog.product.application.usecase.UpdateProductStockUseCase;
import com.tlavu.novacart.modules.catalog.product.application.usecase.UpdateProductUseCase;
import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.shared.domain.query.PageResult;
import com.tlavu.novacart.modules.catalog.product.domain.repository.query.ProductFilter;
import com.tlavu.novacart.modules.catalog.shared.presentation.validation.SortValidator;
import com.tlavu.novacart.modules.catalog.shared.presentation.mapper.PageRequestMapper;
import com.tlavu.novacart.modules.catalog.product.presentation.dto.request.CreateProductRequest;
import com.tlavu.novacart.modules.catalog.product.presentation.dto.request.ProductFilterRequest;
import com.tlavu.novacart.modules.catalog.product.presentation.dto.request.UpdateProductRequest;
import com.tlavu.novacart.modules.catalog.product.presentation.dto.request.UpdateProductStatusRequest;
import com.tlavu.novacart.modules.catalog.product.presentation.dto.request.UpdateProductStockRequest;
import com.tlavu.novacart.modules.catalog.product.presentation.dto.response.ProductResponse;
import com.tlavu.novacart.shared.presentation.dto.response.ApiResponse;
import com.tlavu.novacart.shared.presentation.dto.response.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(
        name = "Products",
        description = "Manage catalog products, including details, lifecycle status, stock, and category assignment."
)
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
            @PathVariable Long id
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

        PageResult<Product> pages = listProductsUseCase.execute(
                new ProductFilter(
                        filter.name(),
                        filter.status(),
                        filter.categoryId(),
                        filter.minPrice(),
                        filter.maxPrice()
                ),
                PageRequestMapper.toDomain(pageable)
        );

        List<ProductResponse> content = pages.content()
                .stream()
                .map(ProductResponse::from)
                .toList();

        PageResponse<ProductResponse> response = new PageResponse<>(
                content,
                pages.page(),
                pages.size(),
                pages.totalElements(),
                pages.totalPages()
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request
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
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request
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
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductStatusRequest request
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
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductStockRequest request
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
            @PathVariable Long id
    ) {

        deleteProductUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
