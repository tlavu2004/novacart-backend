## TUẦN 1 * Foundation

### Day 1

* [x] Completed

#### Học

* [x]  Spring Boot Project Structure
* [x]  PostgreSQL Basics
* [x]  Docker Basics (container, volume, port mapping)

#### Làm

* [x] Docker chạy được
* [x] PostgreSQL container chạy được
* [x] Database ecommerce được tạo
* [x] Spring Boot project chạy được
* [x] Spring Boot kết nối PostgreSQL thành công
* [x] Application startup không lỗi
* [x] Có ít nhất 1 endpoint hoạt động
* [x] Xác nhận DB thực sự được truy cập

#### Kết quả

* [x] Application chạy thành công và kết nối PostgreSQL thông qua Docker.

---

### Day 2

#### Học

* [x] Database Design Basics
* [x] ERD
* [x] JPA Entity
* [x] Primary Key / Foreign Key
* [x] Flyway Migration

#### Làm

* [x] Category table
* [x] Product table
* [x] Relationship Category 1-N Product
* [x] Flyway V1 migration
* [x] JPA Entity mapping

#### Kết quả

* [x] Flyway migration chạy thành công
* [x] Schema database đúng thiết kế
* [x] FK + UNIQUE + NOT NULL hoạt động
* [x] Entity mapping hoạt động với Hibernate validate

---

### Day 3

#### Học

* [x] Repository Pattern (Port vs Adapter)
* [x] Spring Data JPA
* [x] JpaRepository internals
* [x] Query Methods convention
* [x] Optional<T> usage in repository layer
* [x] Lazy vs Eager loading basics

---

#### Làm

##### Persistence Layer

* [x] CategoryJpaRepository
* [x] ProductJpaRepository

##### Application Layer (Ports)

* [x] CategoryRepository (interface)
* [x] ProductRepository (interface)

##### Infrastructure Layer (Adapters)

* [x] CategoryPersistenceAdapter
* [x] ProductPersistenceAdapter

##### Application Layer (Use Case)

* [x] CreateCategoryUseCase
* [x] CreateProductUseCase

##### Data Setup

* [x] Insert sample data (manual / CommandLineRunner)
* [x] Ensure FK Category → Product works

##### Testing

* [x] Test save Category
* [x] Test save Product with Category relation
* [x] Test findById
* [x] Verify relationship loading works correctly

---

#### Kết quả

* [x] Save Category thành công qua full flow (Use Case → Adapter → JPA)
* [x] Save Product thành công có Category mapping
* [x] Query Category/Product hoạt động đúng
* [x] Không leak JpaRepository ra Application Layer
* [x] Có phân tách rõ: Domain / Application / Infrastructure

---

### Day 4

#### Học

##### Spring Core

* Dependency Injection (DI)
* Spring IoC Container
* Constructor Injection
* Bean Lifecycle (basic)

##### Web Layer

* REST API fundamentals
* HTTP methods (GET, POST)
* Request / Response lifecycle
* Controller Layer

##### Data Transfer

* DTO Pattern
* Request DTO
* Response DTO
* Why not expose Entity directly

##### Validation

* Bean Validation
* `@Valid`
* `@NotBlank`
* `@Size`

##### Error Handling

* Exception hierarchy
* Global Exception Handling
* `@ControllerAdvice`
* `@ExceptionHandler`

##### Transaction

* `@Transactional`
* Transaction boundary concept

---

#### Làm

##### Application Layer

* [x] Reuse CreateCategoryUseCase
* [ ] Add business exception structure (optional)

##### Presentation Layer

###### DTO

* [x] CreateCategoryRequest
* [x] CategoryResponse

###### Controller

* [x] CategoryController

---

##### Validation

* [x] Validate category name

---

##### Exception Handling

* [x] Create GlobalExceptionHandler

Handle:

* [x] MethodArgumentNotValidException
* [ ] BusinessException (base exception)
* [x] Unexpected Exception

##### Response Format

---

##### Testing

###### Manual API Testing

* [x] Create category success
* [x] Empty name
* [x] Name too long

---

#### Kết quả

* [x] POST /api/categories hoạt động
* [x] Controller gọi UseCase thành công
* [x] Dependency Injection hoạt động đúng
* [x] Validation hoạt động
* [x] JSON response chuẩn
* [x] Không expose Entity trực tiếp
* [x] Exception trả đúng HTTP Status
* [x] Có endpoint REST API đầu tiên của hệ thống

---

### DAY 5

#### Học

##### REST API

* GET Resource by Id
* GET Collection
* Path Variables (`@PathVariable`)
* Query Parameters (`@RequestParam`)
* ResponseEntity
* HTTP Status Codes (200, 404)
* Location Header (201 Created)

##### Validation

* Validation Message Customization
* Centralized Validation Messages

##### Clean Architecture

* Query Use Case
* Read Flow (Controller → UseCase → Repository)

---

#### Làm

##### Application Layer

###### Use Cases

* Create GetCategoryByIdUseCase
* Create ListCategoriesUseCase

---

##### Domain Layer

###### Repository

* [x] Add findById()
* [x] Add findAll()

---

##### Infrastructure Layer

###### Persistence

* [x] Implement findById()
* [x] Implement findAll()

---

##### Presentation Layer

###### Controller

* [x] GET /api/categories/{id}
* [x] GET /api/categories

###### Response

* [x] Return CategoryResponse
* [x] Return 404 when category not found

---

##### Validation

* [x] Add max length validation for category name
* [x] Customize validation messages

Example:

* [x] Name is required
* [x] Name must not exceed 255 characters

---

##### Exception Handling

Handle:

* [x] CategoryNotFoundException
* [ ] BusinessException (optional)

---

##### Testing

###### Manual API Testing

- Create Category

* [x] Create category success
* [x] Empty name
* [x] Name too long

- Get Category By Id

* [x] Existing id
* [x] Non-existing id

- List Categories

* [x] Empty list
* [x] Multiple categories

---

#### Kết quả

* [x] POST /api/categories hoạt động
* [x] GET /api/categories/{id} hoạt động
* [x] GET /api/categories hoạt động
* [x] Validation message được customize
* [x] Category not found trả về 404
* [x] Controller chỉ làm nhiệm vụ nhận/trả dữ liệu
* [x] Use Case xử lý business flow
* [x] Không expose Entity trực tiếp
* [x] Hoàn thiện Read API đầu tiên của hệ thống

---

# DAY 6 — Exception Architecture & System Hardening

## HỌC

### Exception Handling Design (Core)
* [ ] BusinessException vs SystemException
* [x] ResourceNotFoundException (404)
* [x] ConflictException (409)
* [x] ValidationException (400)
* [x] Domain Exception Hierarchy Design

### HTTP Mapping Strategy
* [x] Mapping exception → HTTP status
* [ ] RFC 7807 (Problem Details concept * [ ] optional)
* [x] Consistent error response contract

### Clean Architecture (Exception Flow)
* [x] Domain throws exception
* [x] Application layer propagates
* [x] Presentation layer maps to HTTP

### Spring Core (Deepening DI)
* [x] @Qualifier usage (multiple beans)
* [x] @Primary bean selection
* [x] Constructor injection best practices
* [x] Bean ambiguity resolution

---

## LÀM

## 1. Exception Refactor (CORE TASK)

### 1.1 Create Base Exception Hierarchy
* [x] BaseException (abstract or runtime base)
* [ ] BusinessException
* [x] ResourceNotFoundException
* [x] ConflictException
* [x] ValidationException

Mục tiêu:
* [x] Không dùng IllegalArgumentException / RuntimeException bừa nữa
* [x] Domain-driven error classification

---

### 1.2 Replace Generic Exceptions

Refactor toàn bộ:

IllegalArgumentException  
RuntimeException (generic)

→ thay bằng:

* [x] ResourceNotFoundException
* [x] ConflictException
* [x] BusinessException

---

### 1.3 Standardize ErrorCode Usage

Ensure ErrorCode usage consistent

Mapping:
* [x] NOT_FOUND → 404
* [x] CONFLICT → 409
* [x] VALIDATION → 400
* [x] INTERNAL → 500

---

## 2. Global Exception Handler Upgrade

Refactor GlobalExceptionHandler

Handle:

* [x] ResourceNotFoundException → 404
* [x] ConflictException → 409
* [x] BusinessException → 400 or 422
* [x] ValidationException → 400 (field errors)
* [x] fallback → 500

Output chuẩn hóa:
* [x] ErrorResponse unified format
* [x] Không leak stacktrace
* [x] Consistent error structure across APIs

---

## 3. Category Module Refactor

### Update Category Flow
* [x] Replace old exceptions

Ensure:

* [x] GET by id → ResourceNotFoundException
* [x] Create category → ConflictException (duplicate name)

### Improve Use Case Layer
* [x] UseCase chỉ throw domain/application exceptions
* [x] Không chứa HTTP concerns

---

## 4. Dependency Injection Cleanup

Apply:

* [x] @Primary cho default implementations (if needed)
* [x] @Qualifier cho:
  * [x] multiple repositories (future-proofing)
  * [x] multiple service implementations

---

## TESTING

### Manual Testing

Exception cases:

#### GET /categories/{id}
* [x] valid → 200
* [x] invalid → 404 ResourceNotFoundException

#### POST /categories
* [x] duplicate name → 409 ConflictException

Validation:
* [x] name empty → 400
* [x] name > 255 → 400

---

## KẾT QUẢ DAY 6

* [x] Exception hierarchy chuẩn hóa
* [x] Replace toàn bộ generic exceptions
* [x] GlobalExceptionHandler unified
* [x] HTTP mapping rõ ràng (400/404/409/500)
* [x] Category module dùng domain exceptions
* [x] DI annotations đúng chuẩn
* [x] Clean Architecture exception flow hoàn chỉnh

---

## OUTPUT HỆ THỐNG SAU DAY 6

Bạn sẽ có:

* [x] API behavior predictable
* [x] Error response consistent
* [x] Domain logic clean, không phụ thuộc HTTP
* [x] Base đủ chắc để scale Product / Order / Auth sau này

---

# DAY 7 — API Contract Standardization & Product Foundation

## HỌC

### API Contract Design

* [x] ApiResponse<T>
* [x] Generic Response Wrapper
* [x] Success Response Design
* [x] Error Response Design
* [x] Standardized Error Codes
* [x] Consistent API Contract Across Services

### Spring Core

#### Bean Lifecycle

* [x] Bean Creation Lifecycle
* [x] @PostConstruct
* [x] @PreDestroy
* [x] Initialization vs Destruction Phase

### Validation

* [x] Bean Validation Deep Dive
* [x] @Valid
* [ ] @Validated
* [ ] Nested Object Validation
* [ ] Validation Groups (optional)

---

## LÀM

## 1. API Response Standardization (CORE TASK)

### 1.1 Create Generic ApiResponse

Tạo response wrapper chung:

* [x] ApiResponse<T>

Mục tiêu:

* [x] Generic response structure
* [x] Reusable cho toàn bộ API
* [x] Tách biệt success và error responses

---

### 1.2 Standardize Success Responses

Áp dụng cho:

* [x] GET Category By Id
* [x] POST Category
* [x] GET Categories List

Ensure:

* [x] Response format thống nhất
* [x] Không trả Entity trực tiếp
* [x] DTO-based responses

---

### 1.3 Standardize Error Responses

Refactor nếu cần:

* [x] ErrorResponse consistent
* [x] ErrorCode luôn xuất hiện
* [x] Validation errors hỗ trợ field-level details

---

## 2. Product Module Foundation

### 2.1 Create Product DTOs

* [x] CreateProductRequest
* [x] ProductResponse

---

### 2.2 Validation

Apply:

* [x] @NotBlank
* [x] @NotNull
* [x] @Positive
* [x] @Size

Validate:

* [x] Product name
* [x] Price
* [x] Stock quantity
* [x] Category id

---

### 2.3 Product Use Case Preparation

Review:

* [x] CreateProductUseCase
* [x] ProductRepository contract
* [x] Category existence validation

Ensure:

* [x] Không chứa HTTP concerns
* [x] Chỉ throw domain/application exceptions
* [x] Ready cho Product Controller

---

## 3. Bean Lifecycle Demo

Tạo ví dụ thực tế:

### Lifecycle Service

* [ ] @PostConstruct log startup
* [ ] @PreDestroy log shutdown

Hiểu:

* [ ] Bean initialization
* [ ] Bean destruction
* [ ] Spring container lifecycle

---

## TESTING

### Category APIs

#### GET /categories/{id}

* [x] valid → 200
* [x] invalid → 404

#### POST /categories

* [x] valid → 201
* [x] duplicate → 409
* [x] validation error → 400

---

### API Contract

Kiểm tra:

* [x] Success response format giống nhau
* [x] Error response format giống nhau
* [x] Timestamp luôn xuất hiện
* [x] ErrorCode luôn xuất hiện

---

## KẾT QUẢ DAY 7

* [x] ApiResponse<T> hoàn chỉnh
* [x] API contract được chuẩn hóa
* [x] Success/Error responses thống nhất
* [x] Validation được củng cố
* [x] Product module sẵn sàng mở rộng
* [ ] Hiểu Bean Lifecycle trong Spring

---

## OUTPUT HỆ THỐNG SAU DAY 7

Bạn sẽ có:

* [x] API responses nhất quán toàn hệ thống
* [x] Client dễ tích hợp hơn
* [x] Product API có nền tảng vững chắc
* [x] Validation và Exception hoạt động đồng bộ
* [x] Kiến trúc sẵn sàng cho Pagination, Sorting và Filtering ở các ngày tiếp theo

---

## OPTIONAL

### Design Patterns

* [ ] Static Factory Method
* [ ] Factory Pattern (basic awareness)
* [ ] Khi nào nên dùng Factory trong Spring Boot
* [ ] Factory vs Dependency Injection


---

# DAY 7.1. — Product API Implementation

## HỌC

### REST API Design

* Resource-Oriented Design
* HTTP Status Codes Review
* POST vs PUT vs PATCH
* Idempotency Concept
* API Endpoint Naming Conventions

### Spring Core

#### Bean Scope

* Singleton Scope
* Prototype Scope
* Request Scope (awareness)
* Bean Scope Lifecycle

### DTO Design

* Request DTO vs Response DTO
* Entity → DTO Mapping
* Khi nào không nên expose Entity

---

## LÀM

## 1. Product Creation API

### 1.1 Create Product Endpoint

Implement:

* [x] POST /products

Flow:

* [x] Validate request
* [x] Verify category exists
* [x] Create product
* [x] Return standardized ApiResponse

Response:

* [x] 201 Created
* [x] ProductResponse

---

### 1.2 Product Mapping

Create:

* [x] ProductMapper

Responsibilities:

* [x] Product → ProductResponse
* [x] Request → Use Case Input

Ensure:

* [x] Controller không chứa mapping logic
* [x] Mapping tập trung một nơi

---

### 1.3 Service / Use Case Integration

Review:

* [x] CreateProductUseCase

Ensure:

* [x] Business validation nằm trong use case
* [x] Controller chỉ điều phối request
* [x] Không chứa business logic ở web layer

---

## 2. Validation Hardening

### Product Validation

Verify:

* [x] Name required
* [x] Name length constraint
* [x] Price > 0
* [x] Stock quantity >= 0
* [x] Category id required

Check:

* [x] Validation messages rõ ràng
* [x] Field errors xuất hiện đúng format

---

## 3. API Contract Verification

Confirm:

* [x] Success response chuẩn ApiResponse<T>
* [x] Error response chuẩn ErrorResponse
* [x] ErrorCode luôn xuất hiện
* [x] Timestamp luôn xuất hiện
* [x] Path luôn chính xác

---

## 4. Bean Scope Demo

Create example:

### Singleton Bean

* [x] Verify single instance

### Prototype Bean

* [x] Verify new instance per request

Understand:

* [x] Scope khác nhau thế nào
* [x] Khi nào dùng Prototype
* [x] Tại sao đa số Spring Bean là Singleton

---

## TESTING

### POST /products

#### Success

* [x] valid request → 201

#### Validation

* [x] blank name → 400
* [x] null name → 400
* [x] negative price → 400
* [x] negative stock → 400
* [x] null category id → 400

#### Business Rules

* [x] category not found → 404

#### API Contract

* [x] success response đúng format
* [x] validation response đúng format
* [x] business exception đúng format

---

## KẾT QUẢ DAY 8

* [x] Product API hoạt động hoàn chỉnh
* [x] Product được tạo qua REST API
* [x] Validation hoạt động đầy đủ
* [x] Controller tuân thủ Clean Architecture
* [x] Mapping được tách biệt rõ ràng
* [x] Hiểu Bean Scope trong Spring

---

## OUTPUT HỆ THỐNG SAU DAY 8

Bạn sẽ có:

* [x] Module Product có API tạo dữ liệu hoàn chỉnh
* [x] Validation + Exception + API Contract hoạt động đồng bộ
* [x] Kiến trúc sẵn sàng cho GET Product By Id
* [x] Sẵn sàng triển khai Pagination ở Day 9

---

# DAY 8

## Học

### REST API

* GET Resource by Id
* GET Collection
* Resource Retrieval Patterns

### Spring Core

* Bean Scope

  * Singleton
  * Prototype

---

## Làm

## 1. Product Query API

### 1.1 Get Product By Id

* [x] GET /products/{id}

Ensure:

* [x] Return ProductResponse
* [x] Return 404 when product not found
* [x] Response uses ApiResponse<T>

---

### 1.2 List Products

* [x] GET /products

Ensure:

* [x] Return list of ProductResponse
* [x] Consistent response format
* [x] No Entity exposure

---

## 2. Bean Scope Demo

### Singleton Scope

* [x] Create Singleton Bean
* [x] Verify single instance behavior

### Prototype Scope

* [x] Create Prototype Bean
* [x] Verify new instance creation

---

## TESTING

### GET /products/{id}

* [x] existing id → 200
* [x] missing id → 404

### GET /products

* [x] empty list → 200
* [x] populated list → 200

---

## KẾT QUẢ DAY 8

* [x] Product Query APIs hoàn chỉnh
* [x] Resource Retrieval Patterns được áp dụng
* [x] Hiểu Bean Scope trong Spring

---

## OUTPUT HỆ THỐNG SAU DAY 8

Bạn sẽ có:

* [x] Product Creation API
* [x] Product Query APIs
* [x] Module Product hoàn chỉnh cho CRUD nền tảng
* [x] Sẵn sàng triển khai Pagination ở ngày tiếp theo

---

# DAY 9

## Học

### REST API - PUT vs PATCH
* PATCH phù hợp hơn PUT cho e-commerce: bandwidth thấp, intent rõ ràng
* PATCH idempotent khi gửi giá trị tuyệt đối, không phải delta
* PUT chỉ có giá trị cho upsert hoặc system sync

### Business Operations vs Field Updates
* `status` và `stock` tách endpoint riêng vì là business operation độc lập, không phải edit field
* State machine: transition rules nằm trong enum, không trong UseCase
* Soft delete: set `deleted_at`, không xóa hẳn — chặn khi có ràng buộc business

### Schema & Validation
* Expression index: `CREATE UNIQUE INDEX ON (LOWER(name))` thay vì `UNIQUE (name)`
* `slug` server tự generate từ name, không nhận từ client
* `@SQLRestriction("deleted_at IS NULL")` tự động filter soft delete mọi query

### Spring
* Spring Data Auditing: `@CreatedDate`, `@LastModifiedDate`, `@EnableJpaAuditing` thay `@PrePersist`/`@PreUpdate`
* `@Transactional` cho write UseCase, `@Transactional(readOnly = true)` cho read UseCase

---

## Làm

### Category
* [x] PATCH /categories/{id} — update name, description, active, re-generate slug nếu name đổi
* [x] DELETE /categories/{id} — soft delete, chặn nếu còn product bất kỳ

### Product
* [x] PATCH /products/{id} — update name, description, price, categoryId
* [x] PATCH /products/{id}/status — state machine DRAFT→ACTIVE, ACTIVE↔INACTIVE
* [x] PATCH /products/{id}/stock — set tuyệt đối
* [x] DELETE /products/{id} — soft delete, set status=INACTIVE (check order là TODO)

---

## Kết quả mong đợi

### PATCH /categories/{id}
* [x] Kiểm tra category tồn tại → 404
* [x] Re-generate slug nếu name thay đổi
* [x] Kiểm tra trùng name/slug → 409
* [x] Trả về category đã cập nhật → 200

### DELETE /categories/{id}
* [x] Kiểm tra category tồn tại → 404
* [x] Chặn nếu còn product bất kỳ → 409
* [x] Soft delete: set deleted_at = now(), active = false
* [x] Trả về 204 No Content

### PATCH /products/{id}
* [x] Kiểm tra product tồn tại → 404
* [x] Kiểm tra category tồn tại nếu categoryId thay đổi → 404
* [x] Re-generate slug nếu name thay đổi
* [x] Kiểm tra trùng name/slug → 409
* [x] Trả về product đã cập nhật → 200

### PATCH /products/{id}/status
* [x] DRAFT→ACTIVE, ACTIVE↔INACTIVE hợp lệ
* [x] Transition không hợp lệ → 400
* [x] Trả về product đã cập nhật → 200

### PATCH /products/{id}/stock
* [x] stockQuantity >= 0
* [x] Set tuyệt đối (idempotent)
* [x] Trả về product đã cập nhật → 200

### DELETE /products/{id}
* [x] Kiểm tra product tồn tại → 404
* [ ] TODO: Chặn nếu có order PENDING/CONFIRMED → 409 (sau khi có Order module)
* [x] Soft delete: set deleted_at = now(), status = INACTIVE
* [x] Trả về 204 No Content

---

## Hoàn thành khi
* [x] Tất cả PATCH hoạt động và test thành công
* [x] DELETE soft delete đúng, chặn đúng trường hợp
* [x] State machine ProductStatus hoạt động đúng
* [x] Exception handling đúng HTTP status code
* [x] CRUD Category và Product hoàn chỉnh

---

# DAY 10

## Học

### Pagination
* `Pageable`, `PageRequest`, `Page<T>` trong Spring Data JPA
* Cách truyền pagination params qua query string (`?page=0&size=10`)
* Cấu trúc response pagination: content, totalElements, totalPages, currentPage

### Filtering
* Filter theo single field: `status`, `categoryId`
* Filter theo range: `price` (minPrice, maxPrice)
* Kết hợp nhiều filter cùng lúc

### Sorting
* Sort theo field: `price`, `createdAt`, `name`
* Sort direction: ASC/DESC
* Kết hợp sort với pagination

---

## Làm

### Category API
* [x] GET /categories — pagination + sorting

### Product API
* [x] GET /products — pagination + filtering (status, categoryId, minPrice, maxPrice) + sorting

---

## Kết quả mong đợi

### GET /categories
* [x] Pagination hoạt động đúng (`?page=0&size=10`)
* [x] Sort theo name, createdAt
* [x] Trả về metadata: totalElements, totalPages, currentPage

### GET /products
* [x] Pagination hoạt động đúng
* [x] Filter theo status → chỉ trả product đúng status
* [x] Filter theo categoryId → chỉ trả product thuộc category đó
* [x] Filter theo minPrice/maxPrice → chỉ trả product trong range
* [x] Filter theo name → chỉ trả name product tương ứng
* [x] Kết hợp nhiều filter cùng lúc
* [x] Sort theo price, createdAt, name
* [x] Soft deleted product không xuất hiện

---

## Kiến thức cần nắm

### Paginationw
* [x] `Pageable` là interface, `PageRequest` là implementation
* [x] Page index bắt đầu từ 0
* [x] `Page<T>` chứa content và metadata

### Filtering
* [x] Spring Data JPA: derived query vs `@Query` vs `Specification`
* [x] `Specification` phù hợp khi filter động, nhiều điều kiện kết hợp

### Sorting
* [x] `Sort` object trong Spring Data
* [x] Validate sort field để tránh injection

---

Bổ sung — đã học ngoài plan

* [x] @FunctionalInterface và lambda map vào toPredicate()
* [x] CriteriaBuilder methods tương ứng SQL
* [x] Anonymous class vs lambda
* [x] @Transactional(readOnly = true) cho read operations
* [x] alwaysTrue() fix NPE trong Specification chain
* [x] PageResponse<T> generic wrapper design

---

## Hoàn thành khi
* [x] Pagination hoạt động đúng cho cả Category và Product
* [x] Filter Product theo status, category, price range
* [x] Sort hoạt động đúng chiều ASC/DESC
* [x] Soft deleted records không xuất hiện trong kết quả
* [x] Response trả đúng metadata pagination

---

# DAY 11

## Học

### Logging
* Logging filter: log request/response tự động cho mọi API
* Structured logging: log có context (method, path, status, duration)
* Log levels: TRACE, DEBUG, INFO, WARN, ERROR — khi nào dùng cái nào
* Slf4j vs Logback vs Log4j2 — mối quan hệ giữa các thư viện

### Exception Design
* `BusinessException` — interface hay abstract class?
* Exception hierarchy: base exception, module exception, specific exception
* Khi nào throw exception ở domain, khi nào ở application

---

## Làm

### Logging
* [x] Tạo `RequestLoggingFilter` — log method, path, status, duration (`shared/infrastructure/logging/`)
* [x] Cấu hình log level — **THAY ĐỔI CÁCH LÀM:** dùng `logback-spring.xml` thay vì `application.yml` (tránh 2 nguồn cấu hình xung đột, linh hoạt hơn cho việc thêm file appender sau này)
* [x] Setup MDC — gắn `requestId` (UUID hoặc lấy từ header `X-Request-Id`) xuyên suốt request, trả lại qua response header

### Exception
* [x] Quyết định `BaseException` là **abstract class** (không phải interface) — cần state chung (`errorCode`, `status`) và constructor dùng chung
* [x] Xây `BaseException` hierarchy: `BaseException` → `ResourceNotFoundException`/`ConflictException`/`InvalidInputException`/`InvalidSortFieldException` (shared) → 8 exception cụ thể theo module (`catalog`)
* [x] Hoàn thiện `GlobalExceptionHandler` — cover cả framework-level (`NoResourceFoundException`, `MethodArgumentTypeMismatchException`, `HttpRequestMethodNotSupportedException`, `PropertyReferenceException`) lẫn business-level (qua 1 handler chung `BaseException.class`)
* [x] Đảm bảo không có exception nào trả 500 không mong muốn — phát hiện và fix 4 bug thực tế trong quá trình test (xem phần "Bug phát hiện" bên dưới)

### Việc phát sinh ngoài kế hoạch ban đầu (không có trong checklist gốc, nhưng cần thiết)
* [x] Fix bug: `CategoryController` thiếu `SortValidator.validate()`, gây `PropertyReferenceException` → 500
* [x] Fix bug: `CreateProductUseCase`/`DeleteProductUseCase` gọi `changeStatus()` sai cách, gây `InvalidProductStatusTransitionException` không mong muốn khi tạo/xóa sản phẩm
* [x] Chuyển logic validate status transition từ `UpdateProductStatusUseCase` (Application) vào `Product.changeStatus()` (Domain) — đúng nguyên tắc Bước 10
* [x] Rà soát và tinh giản `ALLOWED_SORT_PROPERTIES` cho `Category`/`Product` — loại field trùng lặp mục đích (`id`, `slug`) hoặc không phù hợp cho sort (`active`, `status`)
* [x] Setup môi trường test riêng: `application-test.yml`, `docker-compose.yml` (service `postgres-test` port 5433), file `.http` test suite (tương thích VSCode REST Client)

---

## Kết quả mong đợi

### Logging
* [x] Mỗi request tự động log: method, path, status code, duration
* [x] Log level phù hợp — `INFO` cho request log, `DEBUG` cho SQL, `WARN` cho lỗi client (4xx), `ERROR` cho lỗi hệ thống (5xx)
* [x] Không log sensitive data — đã rà soát, `RequestLoggingFilter` chỉ log method/path/status/duration, không log body/header nhạy cảm

### Exception
* [x] Exception hierarchy rõ ràng, nhất quán (3 tầng: Base → nhóm chung → cụ thể module)
* [x] Mọi exception có error code (`GlobalErrorCode`/`CatalogErrorCode`), message rõ ràng
* [x] `GlobalExceptionHandler` cover hết: validation, business, system — đã test 17 case qua file `.http`
* [x] Không có case nào trả 500 không mong muốn — xác nhận qua test thủ công lặp lại nhiều vòng

---

## Kiến thức cần nắm

### Logging
* [x] `Filter` vs `Interceptor` — Filter bọc ngoài cùng (servlet-level), bắt được cả 404; Interceptor ở trong (Spring MVC-level), biết `handler` cụ thể
* [x] `@Slf4j` và cách dùng `log.info()`, `log.warn()`, `log.error()`
* [x] MDC — thread-local map, cần `MDC.remove()` trong `finally` để tránh leak giữa các request (do thread pool tái sử dụng thread)

### Exception
* [x] Abstract class phù hợp hơn interface cho `BaseException` — cần chia sẻ state/constructor cụ thể
* [x] Checked vs unchecked — chọn unchecked (`RuntimeException`) để tránh phá vỡ signature UseCase và đảm bảo transaction rollback tự động
* [x] `@ExceptionHandler` hoạt động theo đa hình — 1 handler bắt được mọi exception con, dù kế thừa qua nhiều tầng

---

## Hoãn / Chưa làm (có chủ đích, không phải thiếu sót)

* [ ] **`AccessDeniedException` handler (403)** — hoãn vì `catalog` module chưa có Spring Security/authentication. Sẽ bổ sung khi triển khai module `user`/auth.
* [ ] **`CategoryFilterRequest`** (filter theo `active`, `name` cho Category) — hoãn vì chưa có nhu cầu nghiệp vụ thực tế xác nhận. Đã ghi `TODO` trong `CategoryController`.
* [ ] **Tách bảng archive + background job hard-delete sau 30 ngày** — chỉ là ý tưởng kiến trúc dài hạn, chưa có bằng chứng cần thiết (dữ liệu hiện tại còn nhỏ). Ghi chú riêng trong tài liệu kiến trúc, không phải `TODO` trong code.
* [ ] **Tách Domain Entity riêng khỏi JPA Entity** (`Product` hiện vừa là JPA Entity vừa là Domain Entity) — chấp nhận được ở quy mô hiện tại, chỉ cần tách khi thực sự cần Domain độc lập hoàn toàn khỏi JPA (đổi ORM, test không cần Spring context).

---

## Hoàn thành khi
* [x] Mọi request được log tự động với đủ context
* [x] Exception hierarchy nhất quán, dễ extend (đã chứng minh qua việc thêm 8 exception mới không cần sửa `GlobalExceptionHandler`)
* [x] `GlobalExceptionHandler` cover hết các case thực tế phát sinh trong quá trình test
* [x] Không leak stack trace ra response (kể cả message nhạy cảm — đã rà soát riêng)

---

# DAY 12

- Đã có file riêng

---

# DAY 13

- Đã có file riêng

---

# DAY 14

- Đã có file riêng
