#!/usr/bin/env bash
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "${REPO_ROOT}"

ENV_FILE="${ENV_FILE:-deploy/.env}"
COMPOSE_FILE="${COMPOSE_FILE:-deploy/docker-compose.yml}"

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

DEPLOY_ROOT="${MALL_DEPLOY_ROOT:-$(get_env MALL_DEPLOY_ROOT /opt/mall-deploy)}"
LOG_DIR="${DEPLOY_ROOT}/logs"
if ! mkdir -p "${LOG_DIR}" 2>/dev/null; then
  LOG_DIR="/tmp"
fi

REPORT_FILE="${LOG_DIR}/deploy-diagnose-$(date +%Y%m%d-%H%M%S).log"
COMPOSE=(docker compose --env-file "${ENV_FILE}" -f "${COMPOSE_FILE}")

section() {
  printf '\n===== %s =====\n' "$1"
}

run_or_note() {
  "$@" || echo "COMMAND_FAILED: $*"
}

{
  section "summary"
  echo "time=$(date -Is)"
  echo "repo=${REPO_ROOT}"
  echo "env_file=${ENV_FILE}"
  echo "compose_file=${COMPOSE_FILE}"
  echo "deploy_root=${DEPLOY_ROOT}"
  echo "rollback=disabled"

  section "docker version"
  run_or_note docker --version
  run_or_note docker compose version

  section "compose ps"
  run_or_note "${COMPOSE[@]}" ps

  section "container state"
  mapfile -t services < <("${COMPOSE[@]}" config --services 2>/dev/null || true)
  for service in "${services[@]}"; do
    cid="$("${COMPOSE[@]}" ps -q "${service}" 2>/dev/null || true)"
    if [ -z "${cid}" ]; then
      echo "${service}: no container"
      continue
    fi
    docker inspect \
      --format "${service}: status={{.State.Status}} health={{if .State.Health}}{{.State.Health.Status}}{{else}}none{{end}} restart={{.RestartCount}} exit={{.State.ExitCode}}" \
      "${cid}" 2>/dev/null || echo "${service}: inspect failed"
  done

  section "non-running or unhealthy logs"
  for service in "${services[@]}"; do
    cid="$("${COMPOSE[@]}" ps -q "${service}" 2>/dev/null || true)"
    [ -n "${cid}" ] || continue
    status="$(docker inspect --format '{{.State.Status}}' "${cid}" 2>/dev/null || true)"
    health="$(docker inspect --format '{{if .State.Health}}{{.State.Health.Status}}{{else}}none{{end}}' "${cid}" 2>/dev/null || true)"
    if [ "${status}" != "running" ] || { [ "${health}" != "healthy" ] && [ "${health}" != "none" ]; }; then
      echo "--- ${service} (${status}/${health}) last 200 lines ---"
      docker logs --tail=200 "${cid}" 2>&1 || true
    fi
  done

  section "nginx config"
  run_or_note "${COMPOSE[@]}" exec -T mall-nginx nginx -t

  section "nacos config check"
  NACOS_NAMESPACE="${NACOS_NAMESPACE:-$(get_env NACOS_NAMESPACE mall-test)}"
  NACOS_GROUP="${NACOS_GROUP:-$(get_env NACOS_GROUP MALL_GROUP)}"
  tmp_nacos="/tmp/mall-nacos-check.$$"
  if curl -fsS "http://127.0.0.1:8848/nacos/v1/cs/configs?tenant=${NACOS_NAMESPACE}&group=${NACOS_GROUP}&dataId=mall-common.yaml" -o "${tmp_nacos}"; then
    if grep -q 'jdbc:mysql://mall-mysql:3306' "${tmp_nacos}"; then
      echo "nacos mall-common.yaml reachable and contains mysql service url"
    else
      echo "nacos mall-common.yaml reachable but expected mysql service url was not found"
    fi
  else
    echo "nacos mall-common.yaml is not reachable"
  fi
  rm -f "${tmp_nacos}"

  section "host ports"
  if command -v ss >/dev/null 2>&1; then
    run_or_note ss -lntp
  elif command -v netstat >/dev/null 2>&1; then
    run_or_note netstat -lntp
  else
    echo "no ss/netstat command found"
  fi

  section "disk and memory"
  run_or_note df -h
  run_or_note free -h
} | tee "${REPORT_FILE}"

echo "DIAGNOSE_REPORT=${REPORT_FILE}"
