#!/bin/bash

set -euo pipefail

# Define color code
if [[ -t 1 ]]; then
  RED='\033[0;31m'
  YELLOW='\033[1;33m'
  GREEN='\033[0;32m'
  NC='\033[0m'
else
  # shellcheck disable=SC2034
  RED=''
  # shellcheck disable=SC2034
  YELLOW=''
  # shellcheck disable=SC2034
  GREEN=''
  # shellcheck disable=SC2034
  NC=''
fi

# Get the directory where script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Go up 5 levels from src/main/resources/scripts/postgres to reach project root
SERVICE_DIR="$(cd "$SCRIPT_DIR/../../../../.." && pwd)"

ENV_FILENAME=${1:-".env"}
ENV_FILE="$SERVICE_DIR/$ENV_FILENAME"

# Check for .env file in service directory
if [[ ! -f "$ENV_FILE" ]]; then
  echo -e "${RED}ERROR: Environment file not found at $ENV_FILE${NC}" >&2
  exit 1
fi

# Load variables from service .env (robustly without requiring quotes)
echo "Loading environment from $ENV_FILE..."

while IFS='=' read -r key value || [[ -n "${key-}" ]]; do
  # Skip comments and empty lines
  if [[ $key =~ ^#.* ]] || [[ -z $key ]]; then
    continue
  fi

  # Clean potential carriage returns
  key=${key//$'\r'/}
  value=${value//$'\r'/}

  if [[ -n "$key" ]]; then
    export "$key"="$value"
  fi
done < "$ENV_FILE"

# Validate required variables (aligned with NovaCart .env)
required_vars=(
  DB_HOST
  DB_PORT
  DB_NAME
  DB_USERNAME
  DB_PASSWORD
)

for var in "${required_vars[@]}"; do
  if [[ -z "${!var:-}" ]]; then
    echo -e "${RED}ERROR: Required variable '$var' is missing in $ENV_FILENAME${NC}" >&2
    exit 1
  fi
done

# Check Maven
command -v mvn >/dev/null 2>&1 || {
  echo -e "${RED}ERROR: Maven is not installed or not in PATH${NC}" >&2
  exit 1
}

# Confirm destructive action
echo -e "${YELLOW}--------------------------------------------------------"
echo "WARNING: This will DROP all tables and data in the DB!"
echo "Target URL: jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME"
echo "Env File:   $ENV_FILENAME"
echo -e "--------------------------------------------------------${NC}"

# Reminder: Press Enter to cancel
read -r -p "Are you sure? Type 'yes' to continue (or press Enter to cancel): " confirm

# Force user type correctly the word 'yes', any other values of empty value (Enter) will cancel
if [[ "$confirm" != "yes" ]]; then
  echo -e "${YELLOW}Aborted. No changes were made.${NC}"
  exit 0
fi

# Change to service directory
cd "$SERVICE_DIR" || {
  echo -e "${RED}ERROR: Cannot cd to $SERVICE_DIR${NC}" >&2
  exit 1
}

# Run Flyway clean with explicit parameters from .env
mvn flyway:clean \
  -Dflyway.cleanDisabled=false \
  -Dflyway.url="jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME" \
  -Dflyway.user="$DB_USERNAME" \
  -Dflyway.password="$DB_PASSWORD"

echo -e "${GREEN}PostgreSQL database cleaned successfully!${NC}"
