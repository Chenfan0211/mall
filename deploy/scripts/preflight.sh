#!/usr/bin/env bash
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "${REPO_ROOT}"

ENV_FILE="${ENV_FILE:-deploy/.env}"
COMPOSE_FILE="${COMPOSE_FILE:-deploy/docker-compose.yml}"
MIN_FREE_KB="${MIN_FREE_KB:-5242880}"

fail() {
  echo "PRECHECK_FAILED: $*" >&2
  exit 1
}

warn() {
  echo "PRECHECK_WARN: $*" >&2
}

get_env() {
  local key="$1"
  local default="${2:-}"
  local value
  value="$(grep -E "^${key}=" "${ENV_FILE}" 2>/dev/null | tail -1 | sed 's/^[^=]*=//' | tr -d '\r' || true)"
  if [ -n "${value}" ]; then
    printf '%s' "${value}"
  else
    printf '%s' "${default}"
  fi
}

if ! command -v docker >/dev/null 2>&1; then
  fail "docker command not found"
fi

if ! docker compose version >/dev/null 2>&1; then
  fail "docker compose plugin not found"
fi

if [ ! -f "${ENV_FILE}" ]; then
  fail "${ENV_FILE} not found. Copy deploy/.env.example to deploy/.env first."
fi

required_names=(
  MYSQL_ROOT_PASSWORD
  MYSQL_APP_PASSWORD
  REDIS_PASSWORD
  MALL_TOKEN_SECRET
  MINIO_ROOT_USER
  MINIO_ROOT_PASSWORD
  MALL_BASIC_AUTH_PASSWORD
)

for name in "${required_names[@]}"; do
  value="$(get_env "${name}")"
  if [ -z "${value}" ]; then
    fail "${name} is missing in ${ENV_FILE}"
  fi
  if [[ "${value}" == change-* ]]; then
    fail "${name} still uses placeholder value in ${ENV_FILE}"
  fi
done

token_secret="$(get_env MALL_TOKEN_SECRET)"
if [ "${#token_secret}" -lt 32 ]; then
  fail "MALL_TOKEN_SECRET must be at least 32 characters"
fi

available_kb="$(df -Pk . | awk 'NR==2 {print $4}')"
if [ -n "${available_kb}" ] && [ "${available_kb}" -lt "${MIN_FREE_KB}" ]; then
  fail "free disk is ${available_kb} KB, lower than required ${MIN_FREE_KB} KB"
fi

if command -v ss >/dev/null 2>&1; then
  if ss -lnt "( sport = :80 )" 2>/dev/null | tail -n +2 | grep -q .; then
    warn "port 80 is already listening; this is expected only when the existing mall-nginx is running"
  fi
elif command -v netstat >/dev/null 2>&1; then
  if netstat -lnt 2>/dev/null | awk '{print $4}' | grep -Eq '(^|:)80$'; then
    warn "port 80 is already listening; this is expected only when the existing mall-nginx is running"
  fi
fi

if [ ! -f deploy/docker/mysql/init/10-nacos.sql ] || [ ! -f deploy/docker/mysql/init/11-xxl-job.sql ]; then
  if command -v curl >/dev/null 2>&1; then
    echo "Middleware schemas are missing. Generating them now..."
    bash deploy/scripts/fetch-middleware-schemas.sh
  else
    fail "middleware schemas are missing and curl is not available. Run: bash deploy/scripts/fetch-middleware-schemas.sh"
  fi
fi

docker compose --env-file "${ENV_FILE}" -f "${COMPOSE_FILE}" config >/tmp/mall-compose-config.yml
echo "Preflight passed."
