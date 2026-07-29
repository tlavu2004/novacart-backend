package com.tlavu.novacart.modules.catalog.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tlavu.novacart.support.AbstractIntegrationTest;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CatalogApiIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void catalogWorkflow_persistsFiltersMovesProtectsAndSoftDeletesResources() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String sourceName = "Integration Source " + suffix;
        String targetName = "Integration Target " + suffix;
        String productName = "Integration Mouse " + suffix;
        String updatedProductName = "Integration Keyboard " + suffix;

        long sourceCategoryId = createCategory(sourceName);
        long targetCategoryId = createCategory(targetName);
        long productId = createProduct(productName, sourceCategoryId);

        mockMvc.perform(get("/api/v1/products")
                        .queryParam("name", productName)
                        .queryParam("status", "DRAFT")
                        .queryParam("categoryId", String.valueOf(sourceCategoryId))
                        .queryParam("minPrice", "10.00")
                        .queryParam("maxPrice", "30.00")
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                        .queryParam("sort", "price,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].id").value(productId))
                .andExpect(jsonPath("$.data.totalElements").value(1));

        mockMvc.perform(patch("/api/v1/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "%s",
                                  "price": 49.90,
                                  "categoryId": %d
                                }
                                """.formatted(updatedProductName, targetCategoryId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(updatedProductName))
                .andExpect(jsonPath("$.data.categoryId").value(targetCategoryId))
                .andExpect(jsonPath("$.data.price").value(49.90));

        mockMvc.perform(patch("/api/v1/products/{id}/status", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status": "ACTIVE"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        mockMvc.perform(delete("/api/v1/categories/{id}", targetCategoryId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("CAT_004"));

        mockMvc.perform(delete("/api/v1/products/{id}", productId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/products/{id}", productId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("PROD_003"));

        mockMvc.perform(get("/api/v1/products")
                        .queryParam("name", updatedProductName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(0))
                .andExpect(jsonPath("$.data.totalElements").value(0));

        mockMvc.perform(delete("/api/v1/categories/{id}", targetCategoryId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/categories/{id}", targetCategoryId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("CAT_003"));

        mockMvc.perform(delete("/api/v1/categories/{id}", sourceCategoryId))
                .andExpect(status().isNoContent());
    }

    @Test
    void applicationStartup_appliesFlywayMigrationAndValidatesSchema() {
        Integer successfulMigrationCount = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM flyway_schema_history
                        WHERE version = '1' AND success = true
                        """,
                Integer.class
        );
        Integer requiredTableCount = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM information_schema.tables
                        WHERE table_schema = 'public'
                          AND table_name IN ('categories', 'products')
                        """,
                Integer.class
        );

        assertThat(successfulMigrationCount).isEqualTo(1);
        assertThat(requiredTableCount).isEqualTo(2);
    }

    @Test
    void productReadEndpoints_serializeResponsesWithoutLazyLoadingOrNPlusOne() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        long categoryId = createCategory("Read category " + suffix);
        long productId = createProduct("Read product " + suffix + " primary", categoryId);
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);

        sessionFactory.getStatistics().clear();

        mockMvc.perform(get("/api/v1/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(productId))
                .andExpect(jsonPath("$.data.categoryId").value(categoryId));

        assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(1);

        createProduct("Read product " + suffix + " secondary", categoryId);
        sessionFactory.getStatistics().clear();

        mockMvc.perform(get("/api/v1/products")
                        .queryParam("name", "Read product " + suffix)
                        .queryParam("page", "0")
                        .queryParam("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.content[0].categoryId").value(categoryId));

        assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(2);
    }

    private long createCategory(String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "%s",
                                  "description": "Integration test category"
                                }
                                """.formatted(name)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.data.updatedAt").isNotEmpty())
                .andReturn();

        return responseId(result);
    }

    private long createProduct(String name, long categoryId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "%s",
                                  "description": "Integration test product",
                                  "price": 19.90,
                                  "stockQuantity": 4,
                                  "categoryId": %d
                                }
                                """.formatted(name, categoryId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andExpect(jsonPath("$.data.categoryId").value(categoryId))
                .andExpect(jsonPath("$.data.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.data.updatedAt").isNotEmpty())
                .andReturn();

        return responseId(result);
    }

    private long responseId(MvcResult result) throws Exception {
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsByteArray());
        return response.path("data").path("id").asLong();
    }
}
