# Tech-Debt Summary — Catalog Foundation (Weeks 1–2)

This document consolidates unchecked work from the original Week 1–2 debt list and deliberately deferred Day 12 quality gates. Track each item here before updating related daily plans.

## Status convention

- **Now:** required for the current Catalog hardening work.
- **Next:** starts when its dependency or real product need appears.
- **Deferred:** intentionally postponed; revisit only when its trigger occurs.
- **Verify:** an older checklist may be stale; confirm the real state before creating work.

## Now

- [x] **TD-01 — Feature boundaries:** Category and Product are separated by feature; `CategoryProductUsagePort` removes the reverse repository dependency.
- [x] **TD-02 — Architecture tests:** ArchUnit runs in `mvn verify` and protects Domain isolation, Application/Presentation-to-Infrastructure direction, and the Category Application boundary.
- [x] **TD-03 — OpenAPI and API contract tests:** OpenAPI is versioned as `v1`; an integration contract test protects the Catalog paths in `/v3/api-docs`.
- [x] **TD-04 — Exception hierarchy and HTTP mapping:** domain status-transition exceptions are plain Java; Application translates them to `CatalogErrorCode` business errors, with `PROD_004` protected by controller tests.
- [x] **TD-13 — Reversed dependency direction:** Application and Presentation no longer import Catalog Infrastructure.

| ID    | Work item                      | Trigger/source | Definition of done                                                                                                          |
|-------|--------------------------------|----------------|-----------------------------------------------------------------------------------------------------------------------------|
| TD-03 | OpenAPI and API contract tests | Day 12         | **Completed:** `/v3/api-docs` exposes the `v1` Catalog contract; the integration test verifies the important Catalog paths. |

## Next

- [ ] **TD-05 — Access denied handling and security tests.** Trigger: User/Auth, role matrix, and protected endpoints.
- [ ] **TD-06 — Product deletion with pending/confirmed orders.** Trigger: the Order module defines state and Product relation.
- [ ] **TD-07 — Category filtering (`active`, `name`).** Trigger: a real Category search/list use case.
- [ ] **TD-08 — End-to-end tests.** Trigger: frontend or external integration.
- [ ] **TD-09 — Performance/load tests.** Trigger: SLA, throughput/latency budget, and production-like data.

## Deferred

- [ ] **TD-10 — RFC 7807 Problem Details.** Revisit when a public/interoperable API needs a standard error contract.
- [ ] **TD-11 — Archive table and background hard-delete.** Revisit with retention/compliance requirements or meaningful data volume.
- [ ] **TD-12 — Security tests.** Revisit together with TD-05.
- [x] **TD-14 — Public Domain setters (accepted trade-off recorded).** `Category` and `Product` use direct MapStruct mapping against near one-to-one JPA models. Replacing setters with a public all-fields constructor or builder would not improve invariant protection; a controlled `restore`/rehydration API is deferred until Domain state becomes immutable or materially diverges from persistence state. Production business flows must use named methods such as `rename`, `changePrice`, and `changeStatus` rather than setters.

## Verify / learning backlog

- [ ] Advanced Bean Validation: `@Validated`, nested validation, and validation groups.
- [ ] Bean lifecycle: `@PostConstruct`, `@PreDestroy`, initialization, and destruction.
- [ ] Factory pattern compared with Dependency Injection.
- [ ] Reconcile old DI and `BusinessException` checklists after review to avoid duplicate debt.

## Superseded item

- [x] Catalog Domain and JPA entities are already separated. Day 13 moved persistence entities into Infrastructure and maps at the persistence boundary.

## Update rule

When an item is completed, update this document first, then the source plan and any linked decision/test record. New items require an ID, trigger, owner or milestone when known, and supporting test/decision evidence.
