# NovaCart Backend

Spring Boot backend for the NovaCart project.

> **MVP note:** This README documents the current Local/Test workflow. Sections marked with `[TODO]` are reserved for future documentation as the MVP grows.

## Prerequisites

- Java 21
- Maven
- Docker and Docker Compose
- GNU Make (Git Bash, Linux, or macOS)
- VS Code REST Client extension (for manual API checks)

## Project structure

```text
 docker-compose.yml                         # shared Compose configuration
 docker-compose.local.yml                   # Local database override
 docker-compose.test.yml                    # Test database override
 src/main/resources/application.yaml        # shared Spring configuration
 src/main/resources/application-local.yaml  # Local profile environment import
 src/main/resources/application-test.yaml   # Test profile overrides
 .env.local.example                         # Local environment template
.env.test.example                           # Test environment template
docs/testing/                               # manual REST Client test cases
Makefile                                    # common local commands
```

## Environment files

Create local environment files from the templates and fill in real values:

```bash
cp .env.local.example .env.local
cp .env.test.example .env.test
```

`.env.local` and `.env.test` are local-only files and must not be committed. The Local and Test profiles explicitly import their matching environment file.

Required database variables include:

```text
DB_HOST DB_PORT DB_NAME DB_USERNAME DB_PASSWORD
```

Passwords are required by Compose and have no production-safe defaults.

## Local environments

| Environment | Spring profile | PostgreSQL port | Application port | Compose project | Volume |
|---|---|---:|---:|---|---|
| Local | `local` | `5432` | `8080` | `novacart-local` | `postgres_local_data` |
| Test | `test` | `5433` | `8081` | `novacart-test` | `postgres_test_data` |

Local and Test use separate Compose project names, networks, containers, and volumes, so they can run in parallel.

## Standard startup workflow

### Local

Build the application and start Local PostgreSQL and Spring Boot:

```bash
make local-build-run
```

If the database is already running and only the backend needs to restart:

```bash
make local-run
```

The Local API is available at `http://localhost:8080`.

### Test

Start the isolated Test database, build the application, and run the Test profile:

```bash
make test-build-run
```

The Test API is available at `http://localhost:8081`.

When `Ctrl+C` is used to stop Spring Boot, only the backend process stops. The PostgreSQL container remains running. The Makefile handles this intentional interrupt without reporting a make error.

## Make targets

### Start and run

```bash
make local-up          # start Local PostgreSQL only
make local-start       # start existing, stopped Local containers
make local-stop        # stop Local containers without removing them
make local-run         # start Local PostgreSQL and run the backend without rebuilding
make local-build-run   # start Local PostgreSQL, build without tests, then run the backend

make test-up         # start Test PostgreSQL only
make test-start      # start existing, stopped Test containers
make test-stop       # stop Test containers without removing them
make test-run        # start Test PostgreSQL and run the backend without rebuilding
make test-build-run  # start Test PostgreSQL, build without tests, then run the backend
```

### Stop and clean

```bash
make local-down      # stop Local containers and networks; keep the database volume
make test-down       # stop Test containers and networks; keep the database volume

make local-clean     # stop Local and delete its volume and data
make test-clean      # stop Test and delete its volume and data
```

`*-clean` commands are destructive for the selected environment.

Use `*-start` and `*-stop` for a temporary pause when the Compose containers
already exist. Use `*-up` when containers may not exist yet; use `*-down` when
you want to remove containers and networks while keeping the database volume.

### Logs and configuration

```bash
make local-logs      # follow Local PostgreSQL logs
make test-logs       # follow Test PostgreSQL logs
make config-local    # print the resolved Local Compose configuration
make config-test     # print the resolved Test Compose configuration
```

## Manual API testing

Manual exception and validation cases are in:

```text
docs/testing/catalog-exception-cases.http
```

The file uses VS Code REST Client syntax. Run the setup requests first so the named request response references can provide temporary entity IDs. Then run the Business, Framework/Routing, and Validation groups.

Use port `8080` for Local and port `8081` for Test. Do not run Test requests against the Local database.

## Database and migrations

Flyway manages database migrations. Hibernate uses schema validation in the isolated Test profile.

> [TODO] Document migration commands, rollback policy, backup/restore procedure, and production migration ownership.

## Production deployment (planned)

The planned deployment target is Render:

1. Deploy the Spring Boot application as a Docker-based Web Service.
2. Use a managed Render PostgreSQL database instead of Docker Compose in production.
3. Set `SPRING_PROFILES_ACTIVE=prod`.
4. Store production secrets in Render Environment Variables or Environment Groups; do not commit or upload `.env.prod` as a source-controlled file.
5. Run Flyway migrations as a pre-deploy step when the production workflow is finalized.
6. Configure the service to listen on Render's `PORT` value (normally `10000`).

> [TODO] Add the production Dockerfile, Render service settings, health check endpoint, environment variable mapping, and `render.yaml` Blueprint when the MVP is ready.

## Troubleshooting

- If Compose reports a missing password, check `.env.local` or `.env.test`.
- If Local and Test conflict, verify that `novacart-local` and `novacart-test` are used as project names.
- If the API starts before PostgreSQL is ready, inspect `make local-logs` or `make test-logs` and retry after the database health check passes.
- If a REST Client ID variable is unresolved, run the named setup request before the dependent request.

> [TODO] Add common startup errors, database reset guidance, and CI troubleshooting after the MVP test suite is in place.

## Testing roadmap

The `.http` file is useful for manual development checks. As the project grows, convert these cases into automated integration tests using `@SpringBootTest` with MockMvc or RestAssured, and run them in CI/CD.

> [TODO] Document unit-test conventions, integration-test profile, Testcontainers, coverage goals, and CI checks.
