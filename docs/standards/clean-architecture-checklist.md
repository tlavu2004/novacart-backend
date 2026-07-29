# Clean Architecture Checklist

Use this checklist when creating a module or reviewing/refactoring an existing one. Every completed item should have verifiable evidence: a test, package path, pull request/commit, or decision record. Do not mark an item complete merely because the directory structure looks correct.

## How to use it

1. Copy this checklist into the module plan or review and link back to this source.
2. Mark an item complete only after recording its evidence.
3. For an intentional exception, document the reason, owner, and review date instead of silently weakening a rule.

## Domain

- [ ] Does not import Spring, JPA, Hibernate, Spring Data, HTTP, or other framework-specific types.
- [ ] Entities and value objects own business state and behaviour; public setters do not replace business methods.
- [ ] Domain exceptions are plain Java exceptions and do not carry HTTP status, API error codes, or persistence details.
- [ ] Cross-feature domain references exist only for genuine business relationships and do not create circular dependencies.

## Application

- [ ] Use cases coordinate business behaviour and transaction boundaries without knowing controllers, JPA entities, or database details.
- [ ] Dependencies go through ports or repository abstractions owned by the Application or Domain layer.
- [ ] Application translates domain exceptions to business exceptions or API error contracts when required; Domain remains unaware of HTTP.
- [ ] Cross-feature collaboration uses a business-named port rather than directly calling another feature's repository or adapter.

## Infrastructure

- [ ] Adapters implement ports and repositories; JPA entities, Spring Data repositories, Specifications, and SQL stay in Infrastructure.
- [ ] Domain-to-persistence mapping occurs in adapters or mappers, with safeguards against silently missed fields.
- [ ] ORM defaults to `LAZY`; each query has a deliberate fetch plan, with no Open Session in View used to mask lazy-loading issues.
- [ ] Migrations, indexes, and performance decisions are supported by query plans or workload evidence.

## Presentation

- [ ] Controllers accept and return DTOs only; they do not expose Domain/JPA entities or lazy proxies.
- [ ] Request validation, query parameter validation, and sort allowlists are enforced at the boundary.
- [ ] Presentation does not depend on Infrastructure; HTTP status and error responses are mapped consistently by advice/handlers.
- [ ] Important API contracts are protected by controller or integration tests.

## Dependency direction and quality gates

- [ ] Dependencies point inward toward Domain/Application; Infrastructure and Presentation remain outer layers.
- [ ] Test packages mirror the feature/layer structure or deliberately live in an integration scope.
- [ ] Architecture tests run in `mvn verify`/CI and protect at least Domain isolation and the absence of Application/Presentation-to-Infrastructure dependencies.
- [ ] Existing tests, coverage gates, and static analysis pass before merge.
