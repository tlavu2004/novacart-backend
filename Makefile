.PHONY: dev-up dev-down dev-run dev-build-run dev-logs dev-clean test-up test-down test-run test-build-run test-logs test-clean config-dev config-test

dev-up:
	docker compose -p novacart-dev --env-file .env -f docker-compose.yml -f docker-compose.dev.yml up -d

dev-down:
	docker compose -p novacart-dev --env-file .env -f docker-compose.yml -f docker-compose.dev.yml down

dev-run: dev-up
	SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run

dev-build-run: dev-up
	SPRING_PROFILES_ACTIVE=dev mvn clean install -DskipTests && SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run

dev-logs:
	docker compose -p novacart-dev --env-file .env -f docker-compose.yml -f docker-compose.dev.yml logs -f postgres

dev-clean:
	docker compose -p novacart-dev --env-file .env -f docker-compose.yml -f docker-compose.dev.yml down -v --remove-orphans

test-up:
	docker compose -p novacart-test --env-file .env.test -f docker-compose.yml -f docker-compose.test.yml up -d

test-down:
	docker compose -p novacart-test --env-file .env.test -f docker-compose.yml -f docker-compose.test.yml down

test-run: test-up
	SPRING_PROFILES_ACTIVE=test mvn spring-boot:run

test-build-run: test-up
	SPRING_PROFILES_ACTIVE=test mvn clean install -DskipTests && SPRING_PROFILES_ACTIVE=test mvn spring-boot:run

test-logs:
	docker compose -p novacart-test --env-file .env.test -f docker-compose.yml -f docker-compose.test.yml logs -f postgres

test-clean:
	docker compose -p novacart-test --env-file .env.test -f docker-compose.yml -f docker-compose.test.yml down -v --remove-orphans

config-dev:
	docker compose -p novacart-dev --env-file .env -f docker-compose.yml -f docker-compose.dev.yml config

config-test:
	docker compose -p novacart-test --env-file .env.test -f docker-compose.yml -f docker-compose.test.yml config
