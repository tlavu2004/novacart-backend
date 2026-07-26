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
- `[DONE]` DTO constraints are covered through Web-layer tests; separate DTO
  validation unit tests are intentionally unnecessary.

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

- `[DONE]` Create: success, missing category, duplicate name/slug, normalization,
  and slug generation.
- `[DONE]` Get by ID: found and `ProductNotFoundException`.
- `[DONE]` List: page, filters, and pageable/sort behavior.
- `[DONE]` Update: partial updates, null-field preservation, duplicate name/slug,
  blank name, and missing replacement category.
- `[DONE]` Update status: not found, valid transition, invalid transition.
- `[DONE]` Update stock: update behavior and not found are covered here;
  negative stock validation is covered in the Web layer.
- `[DONE]` Delete: soft delete and not found.

### 3. Persistence/data-layer tests

Use `@DataJpaTest` with PostgreSQL Testcontainers. This is a persistence-slice
test and an integration test at the database boundary.

#### Category

- `[DONE]` `existsByNameIgnoreCase`.
- `[DONE]` `existsBySlug`.
- `[DONE]` `...AndIdNot` queries exclude the current entity.
- `[DONE]` `@SQLRestriction` hides soft-deleted categories from lookups.

#### Product and the Category–Product relationship

- `[PARTIAL]` `AbstractIntegrationTest` and PostgreSQL Testcontainers exist, and
  Product persistence tests now use them; runtime execution is blocked because
  Docker is unavailable in the current environment.
- `[PARTIAL]` `ProductJpaRepository` tests are implemented for duplicate
  name/slug, `...AndIdNot`, `existsByCategoryId`, `findById`, and `findAll`;
  runtime verification is blocked until Docker is available.
- `[PARTIAL]` `ProductSpecification` tests are implemented for name, status,
  category ID, min/max price, and combined filters; runtime verification is
  blocked until Docker is available.
- `[PARTIAL]` `Product.category` mapping tests cover `ManyToOne`, category
  persistence, and status enum mapping; runtime verification is blocked until
  Docker is available.
- `[PARTIAL]` Database constraint tests cover the product price check; stock
  constraint coverage and runtime verification remain pending.
- `[PARTIAL]` Product `@SQLRestriction` and soft-delete behavior are covered;
  runtime verification is blocked until Docker is available.
- `[TODO]` Auditing of `createdAt`/`updatedAt`, if required behavior.
- `[TODO]` Flyway migration and database-constraint smoke tests.

### 4. Web-layer tests

Use `@WebMvcTest` and MockMvc with use cases mocked; do not use a database.

#### CategoryController

- `[DONE]` Get by ID: 200 and 404.
- `[DONE]` List: pagination, response mapping, and sorting.
- `[DONE]` Create: 201 and request validation.
- `[DONE]` Update: 200, validation, and business errors.
- `[DONE]` Delete: 204, 404, and conflict when products exist.

#### ProductController

- `[DONE]` Get by ID: 200 and 404.
- `[DONE]` List: filters, pagination, sorting, and rejected sort properties.
- `[DONE]` Create: 201, validation, and missing category.
- `[DONE]` Update: partial updates and business errors.
- `[DONE]` Update status: valid/invalid enum and transition errors.
- `[DONE]` Update stock: validation and not found.
- `[DONE]` Delete: 204 and 404.

#### Shared HTTP behavior

- `[DONE]` `GlobalExceptionHandler`: 400/404/405/409/500, response envelope,
  and error codes.
- `[DONE]` Malformed JSON, invalid enum, invalid path-variable type, and
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

Category and Product application unit tests, Category JPA tests, the Catalog
domain/utility unit tests from section 1, and Testcontainers infrastructure are
present. Web-layer tests now cover both Catalog controllers, request validation,
and shared exception behavior. Product persistence/specification tests are
implemented but not yet runtime-verified because Docker is unavailable in the
current environment. The largest remaining gap is the full Category–Product API
integration workflow.

Manual requests remain separate at
[`../http/catalog-exception-cases.http`](../http/catalog-exception-cases.http). They
are useful smoke checks but do not replace automated tests.
