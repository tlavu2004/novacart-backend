# NovaCart Backend

## Chạy môi trường Dev

Môi trường Dev dùng PostgreSQL ở port `5432` và volume `postgres_data`.

```bash
docker compose -p novacart-dev --env-file .env -f docker-compose.yml -f docker-compose.dev.yml up -d
```

Chạy ứng dụng với profile `dev`:

```bash
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

API mặc định: `http://localhost:8080`

Dừng môi trường Dev:

```bash
docker compose -p novacart-dev --env-file .env -f docker-compose.yml -f docker-compose.dev.yml down
```

## Chạy môi trường Test

Môi trường Test dùng database `novacart_test`, PostgreSQL port `5433`, app port `8081` và volume `postgres_test_data`.

Tạo file biến môi trường local từ file mẫu nếu cần tuỳ chỉnh:

```bash
cp .env.test.example .env.test
```

Khởi động PostgreSQL Test:

```bash
docker compose -p novacart-test --env-file .env.test -f docker-compose.yml -f docker-compose.test.yml up -d
```

Chạy ứng dụng với profile `test`:

```bash
SPRING_PROFILES_ACTIVE=test \
TEST_DB_HOST=localhost \
TEST_DB_PORT=5433 \
TEST_DB_NAME=novacart_test \
TEST_DB_USERNAME=novacart_test \
TEST_DB_PASSWORD=novacart_test \
TEST_SERVER_PORT=8081 \
mvn spring-boot:run
```

API Test: `http://localhost:8081`

Dừng môi trường Test:

```bash
docker compose -p novacart-test --env-file .env.test -f docker-compose.yml -f docker-compose.test.yml down
```

## Compose project names

Always use separate project names when running environments in parallel:

- Dev: `novacart-dev`
- Test: `novacart-test`

Do not omit `-p`, otherwise both environments can share the same Compose
project namespace. `DB_PASSWORD` and `TEST_DB_PASSWORD` are required; do not
use default passwords in production.

## Make targets

```bash
make dev-up        # start Dev PostgreSQL
make dev-run       # start Dev PostgreSQL and run the app
make dev-build-run # build without tests, then run Dev app
make dev-logs       # follow Dev PostgreSQL logs
make test-logs      # follow Test PostgreSQL logs
make test-up       # start Test PostgreSQL
make test-run      # start Test PostgreSQL and run the app
make test-build-run # build without tests, then run Test app
make dev-clean      # stop Dev and remove its volume
make test-clean     # stop Test and remove its volume
```

Thông thường, chỉ cần chạy một trong hai lệnh:

```bash
make dev-build-run
make test-build-run
```

Các target `*-run` tự đọc file môi trường tương ứng và đặt Spring profile:
`.env` + `dev`, hoặc `.env.test` + `test`.

`dev-clean` and `test-clean` are destructive for the selected environment:
they remove its PostgreSQL volume and all data stored there.

## REST Client

Các request kiểm thử exception nằm trong:

`docs/testing/catalog-exception-cases.http`

File này dùng cú pháp tương thích với VSCode REST Client. Chạy Group 0 trước để tạo dữ liệu test riêng, sau đó chạy các group kiểm tra exception.

## Lưu ý dữ liệu

Dev và Test sử dụng database, port và named volume riêng. Không chạy request test trên port `8080`; hãy dùng port `8081` để tránh tác động dữ liệu Dev.
