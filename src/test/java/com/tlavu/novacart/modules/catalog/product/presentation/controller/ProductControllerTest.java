package com.tlavu.novacart.modules.catalog.product.presentation.controller;

import com.tlavu.novacart.modules.catalog.category.application.exception.CategoryNotFoundException;
import com.tlavu.novacart.modules.catalog.product.application.exception.ProductNotFoundException;
import com.tlavu.novacart.modules.catalog.product.application.usecase.CreateProductUseCase;
import com.tlavu.novacart.modules.catalog.product.application.usecase.DeleteProductUseCase;
import com.tlavu.novacart.modules.catalog.product.application.usecase.GetProductByIdUseCase;
import com.tlavu.novacart.modules.catalog.product.application.usecase.ListProductsUseCase;
import com.tlavu.novacart.modules.catalog.product.application.usecase.UpdateProductStatusUseCase;
import com.tlavu.novacart.modules.catalog.product.application.usecase.UpdateProductStockUseCase;
import com.tlavu.novacart.modules.catalog.product.application.usecase.UpdateProductUseCase;
import com.tlavu.novacart.modules.catalog.category.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.product.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.product.domain.enums.ProductStatus;
import com.tlavu.novacart.modules.catalog.product.application.exception.InvalidProductStatusTransitionException;
import com.tlavu.novacart.modules.catalog.shared.domain.query.PageResult;
import com.tlavu.novacart.modules.catalog.product.domain.repository.query.ProductFilter;
import com.tlavu.novacart.modules.catalog.shared.domain.query.SortDirection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetProductByIdUseCase getProductByIdUseCase;

    @MockitoBean
    private ListProductsUseCase listProductsUseCase;

    @MockitoBean
    private CreateProductUseCase createProductUseCase;

    @MockitoBean
    private UpdateProductUseCase updateProductUseCase;

    @MockitoBean
    private UpdateProductStatusUseCase updateProductStatusUseCase;

    @MockitoBean
    private UpdateProductStockUseCase updateProductStockUseCase;

    @MockitoBean
    private DeleteProductUseCase deleteProductUseCase;

    @Test
    void getProductById_whenProductExists_returns200AndResponse() throws Exception {
        when(getProductByIdUseCase.execute(1L)).thenReturn(product());

        mockMvc.perform(get("/api/v1/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Wireless Mouse"))
                .andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andExpect(jsonPath("$.data.categoryId").value(7));
    }

    @Test
    void getProductById_whenProductDoesNotExist_returns404() throws Exception {
        when(getProductByIdUseCase.execute(99L)).thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(get("/api/v1/products/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.status").value(404))
                .andExpect(jsonPath("$.error.code").value("PROD_003"));
    }

    @Test
    void listProducts_withFiltersPaginationAndSort_returnsMappedPage() throws Exception {
        when(listProductsUseCase.execute(any(ProductFilter.class), any()))
                .thenReturn(new PageResult<>(List.of(product()), 0, 5, 1, 1));

        mockMvc.perform(get("/api/v1/products")
                        .queryParam("name", "mouse")
                        .queryParam("status", "DRAFT")
                        .queryParam("categoryId", "7")
                        .queryParam("minPrice", "10.00")
                        .queryParam("maxPrice", "30.00")
                        .queryParam("page", "0")
                        .queryParam("size", "5")
                        .queryParam("sort", "price,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].name").value("Wireless Mouse"))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(5))
                .andExpect(jsonPath("$.data.totalElements").value(1));

        verify(listProductsUseCase).execute(
                argThat(filter -> "mouse".equals(filter.name())
                        && filter.status() == ProductStatus.DRAFT
                        && Long.valueOf(7L).equals(filter.categoryId())
                        && new BigDecimal("10.00").compareTo(filter.minPrice()) == 0
                        && new BigDecimal("30.00").compareTo(filter.maxPrice()) == 0),
                argThat(page -> page.sortOrders().stream().anyMatch(order ->
                        order.property().equals("price") && order.direction() == SortDirection.DESC
                ))
        );
    }

    @Test
    void listProducts_withRejectedSortProperty_returns400WithoutCallingUseCase() throws Exception {
        mockMvc.perform(get("/api/v1/products").queryParam("sort", "unknown,asc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VAL_003"));

        verifyNoInteractions(listProductsUseCase);
    }

    @Test
    void listProducts_withoutPageParameters_usesDefaultPageSize() throws Exception {
        when(listProductsUseCase.execute(any(ProductFilter.class), any()))
                .thenReturn(new PageResult<>(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pageSize").value(20));

        verify(listProductsUseCase).execute(any(ProductFilter.class), argThat(page ->
                page.page() == 0 && page.size() == 20
        ));
    }

    @Test
    void listProducts_withPageSizeAboveMaximum_capsPageSize() throws Exception {
        when(listProductsUseCase.execute(any(ProductFilter.class), any()))
                .thenReturn(new PageResult<>(List.of(), 0, 100, 0, 0));

        mockMvc.perform(get("/api/v1/products").queryParam("size", "101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pageSize").value(100));

        verify(listProductsUseCase).execute(any(ProductFilter.class), argThat(page -> page.size() == 100));
    }

    @Test
    void createProduct_whenRequestIsValid_returns201() throws Exception {
        when(createProductUseCase.execute(
                eq("Wireless Mouse"), eq("Description"), eq(new BigDecimal("19.90")), eq(4), eq(7L)
        )).thenReturn(product());

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Wireless Mouse",
                                  "description": "Description",
                                  "price": 19.90,
                                  "stockQuantity": 4,
                                  "categoryId": 7
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void createProduct_whenRequiredAndNumericFieldsAreInvalid_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": " ",
                                  "price": 0,
                                  "stockQuantity": -1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VAL_001"))
                .andExpect(jsonPath("$.error.fieldErrors[*].field", hasItem("name")))
                .andExpect(jsonPath("$.error.fieldErrors[*].field", hasItem("price")))
                .andExpect(jsonPath("$.error.fieldErrors[*].field", hasItem("stockQuantity")))
                .andExpect(jsonPath("$.error.fieldErrors[*].field", hasItem("categoryId")));

        verifyNoInteractions(createProductUseCase);
    }

    @Test
    void createProduct_whenCategoryDoesNotExist_returns404() throws Exception {
        when(createProductUseCase.execute(
                eq("Wireless Mouse"), isNull(), eq(new BigDecimal("19.90")), eq(4), eq(99L)
        )).thenThrow(new CategoryNotFoundException(99L));

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Wireless Mouse",
                                  "price": 19.90,
                                  "stockQuantity": 4,
                                  "categoryId": 99
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("CAT_003"));
    }

    @Test
    void updateProduct_whenRequestIsValid_returns200() throws Exception {
        Product updated = product();
        updated.setName("Keyboard");
        updated.setSlug("keyboard");
        updated.setPrice(new BigDecimal("49.90"));
        when(updateProductUseCase.execute(
                eq(1L), eq("Keyboard"), isNull(), eq(new BigDecimal("49.90")), eq(7L)
        )).thenReturn(updated);

        mockMvc.perform(patch("/api/v1/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Keyboard",
                                  "price": 49.90,
                                  "categoryId": 7
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Keyboard"))
                .andExpect(jsonPath("$.data.price").value(49.90));
    }

    @Test
    void updateProduct_whenPriceIsInvalid_returns400WithoutCallingUseCase() throws Exception {
        mockMvc.perform(patch("/api/v1/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"price": 0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VAL_001"))
                .andExpect(jsonPath("$.error.fieldErrors[*].field", hasItem("price")));

        verifyNoInteractions(updateProductUseCase);
    }

    @Test
    void updateProduct_whenProductDoesNotExist_returns404() throws Exception {
        when(updateProductUseCase.execute(99L, "Keyboard", null, null, null))
                .thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(patch("/api/v1/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Keyboard"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("PROD_003"));
    }

    @Test
    void updateProductStatus_whenRequestIsValid_returns200() throws Exception {
        Product active = product();
        active.changeStatus(ProductStatus.ACTIVE);
        when(updateProductStatusUseCase.execute(1L, ProductStatus.ACTIVE)).thenReturn(active);

        mockMvc.perform(patch("/api/v1/products/{id}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status": "ACTIVE"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    void updateProductStatus_whenEnumIsInvalid_returns400() throws Exception {
        mockMvc.perform(patch("/api/v1/products/{id}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status": "UNKNOWN"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VAL_001"))
                .andExpect(jsonPath("$.error.message").value(allOf(
                        containsString("UNKNOWN"),
                        containsString("ProductStatus")
                )));

        verifyNoInteractions(updateProductStatusUseCase);
    }

    @Test
    void updateProductStatus_whenTransitionIsInvalid_returns400BusinessError() throws Exception {
        when(updateProductStatusUseCase.execute(1L, ProductStatus.INACTIVE))
                .thenThrow(new InvalidProductStatusTransitionException(
                        ProductStatus.DRAFT, ProductStatus.INACTIVE
                ));

        mockMvc.perform(patch("/api/v1/products/{id}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status": "INACTIVE"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("PROD_004"));
    }

    @Test
    void updateProductStock_whenStockIsValid_returns200() throws Exception {
        Product product = product();
        product.setStockQuantity(0);
        when(updateProductStockUseCase.execute(1L, 0)).thenReturn(product);

        mockMvc.perform(patch("/api/v1/products/{id}/stock", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"stockQuantity": 0}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stockQuantity").value(0));
    }

    @Test
    void updateProductStock_whenStockIsNegative_returns400WithoutCallingUseCase() throws Exception {
        mockMvc.perform(patch("/api/v1/products/{id}/stock", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"stockQuantity": -1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VAL_001"))
                .andExpect(jsonPath("$.error.fieldErrors[*].field", hasItem("stockQuantity")));

        verifyNoInteractions(updateProductStockUseCase);
    }

    @Test
    void updateProductStock_whenProductDoesNotExist_returns404() throws Exception {
        when(updateProductStockUseCase.execute(99L, 3))
                .thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(patch("/api/v1/products/{id}/stock", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"stockQuantity": 3}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("PROD_003"));
    }

    @Test
    void deleteProduct_whenDeletionSucceeds_returns204() throws Exception {
        mockMvc.perform(delete("/api/v1/products/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(deleteProductUseCase).execute(1L);
    }

    @Test
    void deleteProduct_whenProductDoesNotExist_returns404() throws Exception {
        doThrow(new ProductNotFoundException(99L))
                .when(deleteProductUseCase).execute(99L);

        mockMvc.perform(delete("/api/v1/products/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("PROD_003"));
    }

    @Test
    void sharedHandler_forTypeMismatchMalformedJsonMethodRouteAndUnexpectedError_returnsExpectedCodes()
            throws Exception {
        mockMvc.perform(get("/api/v1/products/not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VAL_004"));

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VAL_001"));

        mockMvc.perform(put("/api/v1/products/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.error.code").value("SYS_002"));

        mockMvc.perform(get("/api/v1/not-a-real-route"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("SYS_001"));

        when(getProductByIdUseCase.execute(500L)).thenThrow(new RuntimeException("Unexpected"));
        mockMvc.perform(get("/api/v1/products/{id}", 500L))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.code").value("SYS_003"));
    }

    private Product product() {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        Product product = new Product();
        product.setId(1L);
        product.setName("Wireless Mouse");
        product.setDescription("Description");
        product.setSlug("wireless-mouse");
        product.setPrice(new BigDecimal("19.90"));
        product.setStockQuantity(4);
        product.setCategory(Category.builder().id(7L).name("Electronics").build());
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        return product;
    }
}
