#!/usr/bin/env bash
set -euo pipefail

COMPOSE_FILE="${COMPOSE_FILE:-deploy/docker-compose.yml}"

docker compose -f "${COMPOSE_FILE}" ps
docker compose -f "${COMPOSE_FILE}" exec -T mall-mysql mysqladmin ping -uroot -p"${MYSQL_ROOT_PASSWORD}"
docker compose -f "${COMPOSE_FILE}" exec -T mall-redis redis-cli -a "${REDIS_PASSWORD}" ping
docker compose -f "${COMPOSE_FILE}" exec -T mall-nginx nginx -t

curl -fsS "http://127.0.0.1/admin/" >/dev/null
curl -fsS "http://127.0.0.1/wms/" >/dev/null
curl -fsS "http://127.0.0.1/user/" >/dev/null
curl -fsS "http://127.0.0.1/workbench/" >/dev/null
curl -fsS "http://127.0.0.1/warehouse/" >/dev/null

echo "Basic deployment checks passed."
