.PHONY: local-up local-start local-stop local-down local-run local-build-run local-logs local-clean test-up test-start test-stop test-down test-run test-build-run test-logs test-clean config-local config-test

local-up:
	docker compose -p novacart-local --env-file .env.local -f docker-compose.yml -f docker-compose.local.yml up -d

local-start:
	docker compose -p novacart-local --env-file .env.local -f docker-compose.yml -f docker-compose.local.yml start

local-stop:
	docker compose -p novacart-local --env-file .env.local -f docker-compose.yml -f docker-compose.local.yml stop

local-down:
	docker compose -p novacart-local --env-file .env.local -f docker-compose.yml -f docker-compose.local.yml down

local-run: local-up
	SPRING_PROFILES_ACTIVE=local mvn spring-boot:run; status=$$?; if [ $$status -eq 130 ] || [ $$status -eq 143 ]; then exit 0; fi; exit $$status

local-build-run: local-up
	mvn clean install -DskipTests && (SPRING_PROFILES_ACTIVE=local mvn spring-boot:run; status=$$?; if [ $$status -eq 130 ] || [ $$status -eq 143 ]; then exit 0; fi; exit $$status)

local-logs:
	docker compose -p novacart-local --env-file .env.local -f docker-compose.yml -f docker-compose.local.yml logs -f postgres

local-clean:
	docker compose -p novacart-local --env-file .env.local -f docker-compose.yml -f docker-compose.local.yml down -v --remove-orphans

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

config-local:
	docker compose -p novacart-local --env-file .env.local -f docker-compose.yml -f docker-compose.local.yml config

config-test:
	docker compose -p novacart-test --env-file .env.test -f docker-compose.yml -f docker-compose.test.yml config
