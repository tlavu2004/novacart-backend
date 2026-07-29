package com.tlavu.novacart.modules.catalog.presentation.controller;

import com.tlavu.novacart.modules.catalog.application.exception.specific.CategoryHasActiveProductsException;
import com.tlavu.novacart.modules.catalog.application.exception.specific.CategoryNotFoundException;
import com.tlavu.novacart.modules.catalog.application.usecase.*;
import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.repository.query.PageResult;
import com.tlavu.novacart.modules.catalog.domain.repository.query.SortDirection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetCategoryByIdUseCase getCategoryByIdUseCase;

    @MockitoBean
    private ListCategoriesUseCase listCategoriesUseCase;

    @MockitoBean
    private CreateCategoryUseCase createCategoryUseCase;

    @MockitoBean
    private UpdateCategoryUseCase updateCategoryUseCase;

    @MockitoBean
    private DeleteCategoryUseCase deleteCategoryUseCase;

    @Test
    void getCategoryById_whenCategoryExists_returns200AndResponse() throws Exception {
        when(getCategoryByIdUseCase.execute(1L)).thenReturn(category());

        mockMvc.perform(get("/api/v1/categories/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Electronics"))
                .andExpect(jsonPath("$.data.slug").value("electronics"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    void getCategoryById_whenCategoryDoesNotExist_returns404ErrorEnvelope() throws Exception {
        when(getCategoryByIdUseCase.execute(99L)).thenThrow(new CategoryNotFoundException(99L));

        mockMvc.perform(get("/api/v1/categories/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.status").value(404))
                .andExpect(jsonPath("$.error.code").value("CAT_003"))
                .andExpect(jsonPath("$.error.path").value("/api/v1/categories/99"));
    }

    @Test
    void listCategories_withPaginationAndSort_returnsMappedPage() throws Exception {
        when(listCategoriesUseCase.execute(any()))
                .thenReturn(new PageResult<>(List.of(category()), 0, 10, 1, 1));

        mockMvc.perform(get("/api/v1/categories")
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                        .queryParam("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].name").value("Electronics"))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1));

        verify(listCategoriesUseCase).execute(argThat(page ->
                page.page() == 0
                        && page.size() == 10
                        && page.sortOrders().stream().anyMatch(order ->
                                order.property().equals("name") && order.direction() == SortDirection.ASC
                        )
        ));
    }

    @Test
    void listCategories_withRejectedSortProperty_returns400WithoutCallingUseCase() throws Exception {
        mockMvc.perform(get("/api/v1/categories").queryParam("sort", "unknown,asc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VAL_003"));

        verifyNoInteractions(listCategoriesUseCase);
    }

    @Test
    void createCategory_whenRequestIsValid_returns201() throws Exception {
        when(createCategoryUseCase.execute("Electronics", "Devices")).thenReturn(category());

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Electronics",
                                  "description": "Devices"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(createCategoryUseCase).execute("Electronics", "Devices");
    }

    @Test
    void createCategory_whenRequestIsInvalid_returns400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "   "
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VAL_001"))
                .andExpect(jsonPath("$.error.fieldErrors[*].field", hasItem("name")));

        verifyNoInteractions(createCategoryUseCase);
    }

    @Test
    void updateCategory_whenRequestIsValid_returns200() throws Exception {
        Category updated = category();
        updated.setDescription("Updated");
        updated.setActive(false);
        when(updateCategoryUseCase.execute(1L, null, "Updated", false)).thenReturn(updated);

        mockMvc.perform(patch("/api/v1/categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Updated",
                                  "active": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.description").value("Updated"))
                .andExpect(jsonPath("$.data.active").value(false));
    }

    @Test
    void updateCategory_whenRequestIsInvalid_returns400WithoutCallingUseCase() throws Exception {
        mockMvc.perform(patch("/api/v1/categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "%s"
                                }
                                """.formatted("a".repeat(256))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VAL_001"))
                .andExpect(jsonPath("$.error.fieldErrors[*].field", hasItem("name")));

        verifyNoInteractions(updateCategoryUseCase);
    }

    @Test
    void deleteCategory_whenDeletionSucceeds_returns204() throws Exception {
        mockMvc.perform(delete("/api/v1/categories/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(deleteCategoryUseCase).execute(1L);
    }

    @Test
    void deleteCategory_whenActiveProductsExist_returns409() throws Exception {
        doThrow(new CategoryHasActiveProductsException(1L))
                .when(deleteCategoryUseCase).execute(1L);

        mockMvc.perform(delete("/api/v1/categories/{id}", 1L))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("CAT_004"));
    }

    @Test
    void deleteCategory_whenCategoryDoesNotExist_returns404() throws Exception {
        doThrow(new CategoryNotFoundException(99L))
                .when(deleteCategoryUseCase).execute(99L);

        mockMvc.perform(delete("/api/v1/categories/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("CAT_003"));
    }

    private Category category() {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        return Category.builder()
                .id(1L)
                .name("Electronics")
                .slug("electronics")
                .description("Devices")
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
