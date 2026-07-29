# REST API Design Checklist

Use this checklist when designing a new endpoint or changing an API contract. Each completed item should appear in OpenAPI or equivalent contract documentation and be protected by an appropriate test.

## Resource and HTTP semantics

- [ ] Resource paths use nouns and follow the system's pluralisation and versioning conventions.
- [ ] HTTP methods and status codes accurately represent create, read, update, delete, or state-transition behaviour.
- [ ] State-transition endpoints describe their side effects and idempotency; `PATCH` is not used as an ambiguous general update.
- [ ] The delete policy (hard delete, soft delete, archive, or rejection) is explicit.

## Requests, responses, and naming

- [ ] Requests and responses use DTOs only; they do not expose Domain or persistence models.
- [ ] Field names, enums, timestamps, decimals, and nullability are consistent across endpoints.
- [ ] Validation constraints, default values, and conditional fields are documented.
- [ ] List responses use a stable content and pagination-metadata format.

## List, filter, sort, and search

- [ ] `page` is zero-based, and `size` has documented default and maximum values.
- [ ] Sorting is restricted to an allowlist; default sorting and invalid directions have defined behaviour.
- [ ] Filter/search semantics document case sensitivity, whitespace, and wildcard or special-character behaviour.
- [ ] Combined filtering, sorting, and pagination preserve correct content and total counts.

## Error contract

- [ ] Validation, business-rule, not-found, conflict, and system errors share a consistent response format.
- [ ] Error codes are stable whenever clients need to make decisions from them; messages do not expose implementation details.
- [ ] Domain exceptions do not contain HTTP concerns; Application/Presentation handles translation and HTTP mapping.
- [ ] Controller or integration tests protect important error contracts.

## Documentation and compatibility

- [ ] OpenAPI describes endpoints, schemas, validation, error responses, and important examples.
- [ ] Breaking changes are versioned or communicated according to the API policy.
- [ ] Contract changes are checked in CI through controller, integration, or contract tests.
