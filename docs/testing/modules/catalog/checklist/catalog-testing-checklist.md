# Catalog module testing checklist

This checklist covers the `Category` and `Product` entities in the Catalog
module. The order moves from isolated logic to full application behavior, so
each layer is verified before it becomes a dependency of the next one.

## Status legend

- `[DONE]` — the repository already contains the corresponding automated test
  or implementation.
- `[PARTIAL]` — test infrastructure, manual coverage, or only part of the
  automated coverage exists.
- `[TODO]` — no corresponding test exists yet.

## Recommended execution order

### 1. Domain and utility unit tests

These tests must not start Spring, a database, or an HTTP server.

- `[DONE]` `Category` has no standalone domain behavior; do not spend coverage
  on generated getters, setters, or builders.
- `[DONE]` `ProductStatus.canTransitionTo()`: all valid and invalid transitions
  among `DRAFT`, `ACTIVE`, and `INACTIVE`.
- `[DONE]` `Product.changeStatus()`: successful changes and
  `InvalidProductStatusTransitionException`.
- `[DONE]` `SlugUtils.generate()`: whitespace, case, punctuation, Unicode, and
  boundary inputs.
- `[DONE]` `SortValidator`: allowed properties, rejected properties, and
  multiple sort orders.
- `[TODO]` Optional DTO validation unit tests; HTTP validation is still required
  in the Web layer.

### 2. Application/use-case unit tests

Use JUnit and Mockito with repositories mocked. Test `Category` first because
`Product` depends on a category.

#### Category

- `[DONE]` Create: success, duplicate name, duplicate slug.
- `[DONE]` Get by ID: found and not found.
- `[DONE]` List: returns a page.
- `[DONE]` Update: full/partial updates, blank name, duplicate name/slug,
  preserving null fields, and name/slug normalization.
- `[DONE]` Delete: soft delete, not found, and rejection when active products
  exist.

#### Product

- `[TODO]` Create: success, missing category, duplicate name/slug, normalization,
  and slug generation.
- `[TODO]` Get by ID: found and `ProductNotFoundException`.
- `[TODO]` List: page, filters, and pageable/sort behavior.
- `[TODO]` Update: partial updates, null-field preservation, duplicate name/slug,
  blank name, and missing replacement category.
- `[TODO]` Update status: not found, valid transition, invalid transition.
- `[TODO]` Update stock: update behavior, not found, and valid/invalid boundary
  values.
- `[TODO]` Delete: soft delete and not found.

### 3. Persistence/data-layer tests

Use `@DataJpaTest` with PostgreSQL Testcontainers. This is a persistence-slice
test and an integration test at the database boundary.

#### Category

- `[DONE]` `existsByNameIgnoreCase`.
- `[DONE]` `existsBySlug`.
- `[DONE]` `...AndIdNot` queries exclude the current entity.
- `[DONE]` `@SQLRestriction` hides soft-deleted categories from lookups.

#### Product and the Category–Product relationship

- `[PARTIAL]` `AbstractIntegrationTest` and PostgreSQL Testcontainers exist, but
  are currently used only by the Category repository test.
- `[TODO]` `ProductJpaRepository`: duplicate name/slug, `...AndIdNot`,
  `existsByCategoryId`, `findById`, and `findAll`.
- `[TODO]` `ProductSpecification`: name, status, category ID, min/max price, and
  combined filters.
- `[TODO]` `Product.category` mapping: `ManyToOne`, lazy loading, and non-null FK.
- `[TODO]` `ProductStatus` enum mapping and `price`/`stockQuantity` columns.
- `[TODO]` Product `@SQLRestriction` and soft-delete behavior.
- `[TODO]` Auditing of `createdAt`/`updatedAt`, if required behavior.
- `[TODO]` Flyway migration and database-constraint smoke tests.

### 4. Web-layer tests

Use `@WebMvcTest` and MockMvc with use cases mocked; do not use a database.

#### CategoryController

- `[TODO]` Get by ID: 200 and 404.
- `[TODO]` List: pagination, response mapping, and sorting.
- `[TODO]` Create: 201 and request validation.
- `[TODO]` Update: 200, validation, and business errors.
- `[TODO]` Delete: 204, 404, and conflict when products exist.

#### ProductController

- `[TODO]` Get by ID: 200 and 404.
- `[TODO]` List: filters, pagination, sorting, and rejected sort properties.
- `[TODO]` Create: 201, validation, and missing category.
- `[TODO]` Update: partial updates and business errors.
- `[TODO]` Update status: valid/invalid enum and transition errors.
- `[TODO]` Update stock: validation and not found.
- `[TODO]` Delete: 204 and 404.

#### Shared HTTP behavior

- `[TODO]` `GlobalExceptionHandler`: 400/404/405/409/500, response envelope,
  and error codes.
- `[TODO]` Malformed JSON, invalid enum, invalid path-variable type, and
  unknown routes.

### 5. Full application integration tests

Use `@SpringBootTest` with MockMvc or RestAssured and PostgreSQL Testcontainers.
Do not mock the application or repository layer.

- `[PARTIAL]` `NovaCartApplicationTests.contextLoads()` checks only that the
  Spring context starts; it is not API integration coverage.
- `[TODO]` Create Category, then create a Product belonging to it.
- `[TODO]` Read, filter, sort, and paginate Products against the real database.
- `[TODO]` Update a Product and move it to another category.
- `[TODO]` Reject Category deletion while an active Product exists.
- `[TODO]` Delete the Product, then the Category; verify soft-deleted records
  disappear from API/query results.
- `[TODO]` Verify Flyway runs on the test database and Hibernate validation
  matches the schema.

### 6. Additional testing when justified

- `[TODO]` Architecture tests (for example, ArchUnit).
- `[TODO]` API contract tests when other clients depend on the API contract.
- `[TODO]` Security tests when authentication/authorization is introduced.
- `[TODO]` Performance/load tests when an SLA or traffic target exists.
- `[TODO]` End-to-end tests when a frontend or external integration exists.

## Current coverage summary

Category application unit tests, Category JPA tests, the Catalog domain/utility
unit tests from section 1, and Testcontainers infrastructure are present. The
largest gaps are the Product application tests, Web-layer tests, and full
Category–Product API integration workflows.

Manual requests remain separate at
[`../http/catalog-exception-cases.http`](../http/catalog-exception-cases.http). They
are useful smoke checks but do not replace automated tests.
