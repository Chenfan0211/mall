#!/usr/bin/env bash
set -uo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "${REPO_ROOT}"

ENV_FILE="${ENV_FILE:-deploy/.env}"
COMPOSE_FILE="${COMPOSE_FILE:-deploy/docker-compose.yml}"
VERIFY_TIMEOUT_SECONDS="${VERIFY_TIMEOUT_SECONDS:-300}"
VERIFY_INTERVAL_SECONDS="${VERIFY_INTERVAL_SECONDS:-5}"

if [ ! -f "${ENV_FILE}" ]; then
  echo "VERIFY_FAILED: ${ENV_FILE} not found" >&2
  exit 1
fi

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

MYSQL_ROOT_PASSWORD="$(get_env MYSQL_ROOT_PASSWORD)"
REDIS_PASSWORD="$(get_env REDIS_PASSWORD)"

COMPOSE=(docker compose --env-file "${ENV_FILE}" -f "${COMPOSE_FILE}")
FAILURES=()
FAILED_SERVICES=()

all_services=(
  mall-mysql
  mall-redis
  mall-nacos
  mall-rocketmq-namesrv
  mall-rocketmq-broker
  mall-elasticsearch
  mall-minio
  mall-xxl-job
  mall-auth
  mall-system
  mall-operation
  mall-product
  mall-sale
  mall-trade
  mall-payment
  mall-aftersale
  mall-supplier
  mall-finance
  mall-wms
  mall-station
  mall-user
  mall-job
  mall-gateway
  mall-nginx
)

backend_services=(
  mall-auth
  mall-system
  mall-operation
  mall-product
  mall-sale
  mall-trade
  mall-payment
  mall-aftersale
  mall-supplier
  mall-finance
  mall-wms
  mall-station
  mall-user
  mall-job
  mall-gateway
)

fail_check() {
  local message="$1"
  local service="${2:-}"
  echo "VERIFY_FAIL: ${message}" >&2
  FAILURES+=("${message}")
  if [ -n "${service}" ]; then
    FAILED_SERVICES+=("${service}")
  fi
}

run_check() {
  local name="$1"
  shift
  echo "CHECK: ${name}"
  if "$@"; then
    echo "PASS: ${name}"
  else
    fail_check "${name}"
  fi
}

service_state() {
  local service="$1"
  local cid
  cid="$("${COMPOSE[@]}" ps -q "${service}" 2>/dev/null || true)"
  if [ -z "${cid}" ]; then
    echo "missing none"
    return
  fi
  local status
  local health
  status="$(docker inspect --format '{{.State.Status}}' "${cid}" 2>/dev/null || echo unknown)"
  health="$(docker inspect --format '{{if .State.Health}}{{.State.Health.Status}}{{else}}none{{end}}' "${cid}" 2>/dev/null || echo unknown)"
  echo "${status} ${health}"
}

wait_for_services() {
  local deadline
  deadline=$((SECONDS + VERIFY_TIMEOUT_SECONDS))
  while [ "${SECONDS}" -lt "${deadline}" ]; do
    local pending=0
    for service in "${all_services[@]}"; do
      read -r status health < <(service_state "${service}")
      if [ "${status}" != "running" ]; then
        pending=$((pending + 1))
        continue
      fi
      if [ "${health}" = "starting" ] || [ "${health}" = "unhealthy" ] || [ "${health}" = "unknown" ]; then
        pending=$((pending + 1))
      fi
    done
    if [ "${pending}" -eq 0 ]; then
      return 0
    fi
    sleep "${VERIFY_INTERVAL_SECONDS}"
  done
  return 1
}

print_failure_context() {
  echo
  echo "===== VERIFY FAILURE CONTEXT ====="
  "${COMPOSE[@]}" ps || true

  echo
  echo "===== FAILED SERVICE LOGS ====="
  if [ "${#FAILED_SERVICES[@]}" -eq 0 ]; then
    for service in "${all_services[@]}"; do
      read -r status health < <(service_state "${service}")
      if [ "${status}" != "running" ] || { [ "${health}" != "healthy" ] && [ "${health}" != "none" ]; }; then
        FAILED_SERVICES+=("${service}")
      fi
    done
  fi
  for service in "${FAILED_SERVICES[@]}"; do
    cid="$("${COMPOSE[@]}" ps -q "${service}" 2>/dev/null || true)"
    [ -n "${cid}" ] || continue
    echo "--- ${service} last 200 lines ---"
    docker logs --tail=200 "${cid}" 2>&1 || true
  done

  echo
  echo "===== HOST PORTS ====="
  if command -v ss >/dev/null 2>&1; then
    ss -lntp || true
  elif command -v netstat >/dev/null 2>&1; then
    netstat -lntp || true
  fi

  echo
  echo "===== DISK AND MEMORY ====="
  df -h || true
  free -h || true

  if [ -x deploy/scripts/diagnose-deploy.sh ] || [ -f deploy/scripts/diagnose-deploy.sh ]; then
    echo
    echo "===== DIAGNOSE REPORT ====="
    bash deploy/scripts/diagnose-deploy.sh || true
  fi
}

echo "Waiting for containers to become running/healthy..."
if ! wait_for_services; then
  fail_check "containers did not become running/healthy within ${VERIFY_TIMEOUT_SECONDS}s"
fi

"${COMPOSE[@]}" ps

for service in "${all_services[@]}"; do
  read -r status health < <(service_state "${service}")
  if [ "${status}" != "running" ]; then
    fail_check "${service} is ${status}" "${service}"
  elif [ "${health}" != "healthy" ] && [ "${health}" != "none" ]; then
    fail_check "${service} health is ${health}" "${service}"
  else
    echo "PASS: ${service} status=${status} health=${health}"
  fi
done

for service in "${backend_services[@]}"; do
  read -r status health < <(service_state "${service}")
  if [ "${status}" != "running" ]; then
    fail_check "backend ${service} is not running" "${service}"
  fi
done

run_check "mysql ping" "${COMPOSE[@]}" exec -T mall-mysql mysqladmin ping -uroot -p"${MYSQL_ROOT_PASSWORD}"
run_check "redis ping" "${COMPOSE[@]}" exec -T mall-redis redis-cli -a "${REDIS_PASSWORD}" ping
run_check "nacos console" curl -fsS "http://127.0.0.1:8848/nacos/"
run_check "elasticsearch http" curl -fsS "http://127.0.0.1:9200/"
run_check "minio live health" curl -fsS "http://127.0.0.1:9000/minio/health/live"
run_check "xxl-job admin" curl -fsS "http://127.0.0.1:8080/xxl-job-admin/"
run_check "nginx config" "${COMPOSE[@]}" exec -T mall-nginx nginx -t

for path in /admin/ /wms/ /user/ /workbench/ /warehouse/; do
  run_check "public page ${path}" curl -fsS "http://127.0.0.1${path}"
done

if [ "${#FAILURES[@]}" -gt 0 ]; then
  print_failure_context
  echo
  echo "Deployment verification failed. No rollback was performed; inspect the failure context above."
  exit 1
fi

echo "All deployment checks passed."
