.PHONY: dev-up dev-start dev-stop dev-down dev-run dev-build-run dev-logs dev-clean test-up test-start test-stop test-down test-run test-build-run test-logs test-clean config-dev config-test

dev-up:
	docker compose -p novacart-dev --env-file .env -f docker-compose.yml -f docker-compose.dev.yml up -d

dev-start:
	docker compose -p novacart-dev --env-file .env -f docker-compose.yml -f docker-compose.dev.yml start

dev-stop:
	docker compose -p novacart-dev --env-file .env -f docker-compose.yml -f docker-compose.dev.yml stop

dev-down:
	docker compose -p novacart-dev --env-file .env -f docker-compose.yml -f docker-compose.dev.yml down

dev-run: dev-up
	SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run; status=$$?; if [ $$status -eq 130 ] || [ $$status -eq 143 ]; then exit 0; fi; exit $$status

dev-build-run: dev-up
	SPRING_PROFILES_ACTIVE=dev mvn clean install -DskipTests && (SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run; status=$$?; if [ $$status -eq 130 ] || [ $$status -eq 143 ]; then exit 0; fi; exit $$status)

dev-logs:
	docker compose -p novacart-dev --env-file .env -f docker-compose.yml -f docker-compose.dev.yml logs -f postgres

dev-clean:
	docker compose -p novacart-dev --env-file .env -f docker-compose.yml -f docker-compose.dev.yml down -v --remove-orphans

test-up:
	docker compose -p novacart-test --env-file .env.test -f docker-compose.yml -f docker-compose.test.yml up -d

test-start:
	docker compose -p novacart-test --env-file .env.test -f docker-compose.yml -f docker-compose.test.yml start

test-stop:
	docker compose -p novacart-test --env-file .env.test -f docker-compose.yml -f docker-compose.test.yml stop

test-down:
	docker compose -p novacart-test --env-file .env.test -f docker-compose.yml -f docker-compose.test.yml down

test-run: test-up
	SPRING_PROFILES_ACTIVE=test mvn spring-boot:run; status=$$?; if [ $$status -eq 130 ] || [ $$status -eq 143 ]; then exit 0; fi; exit $$status

test-build-run: test-up
	SPRING_PROFILES_ACTIVE=test mvn clean install -DskipTests && (SPRING_PROFILES_ACTIVE=test mvn spring-boot:run; status=$$?; if [ $$status -eq 130 ] || [ $$status -eq 143 ]; then exit 0; fi; exit $$status)

test-logs:
	docker compose -p novacart-test --env-file .env.test -f docker-compose.yml -f docker-compose.test.yml logs -f postgres

test-clean:
	docker compose -p novacart-test --env-file .env.test -f docker-compose.yml -f docker-compose.test.yml down -v --remove-orphans

config-dev:
	docker compose -p novacart-dev --env-file .env -f docker-compose.yml -f docker-compose.dev.yml config

config-test:
	docker compose -p novacart-test --env-file .env.test -f docker-compose.yml -f docker-compose.test.yml config
