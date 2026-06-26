#!/usr/bin/env bash
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "${REPO_ROOT}"

if ! command -v docker >/dev/null 2>&1; then
  echo "docker command not found"
  exit 1
fi

if ! docker compose version >/dev/null 2>&1; then
  echo "docker compose plugin not found"
  exit 1
fi

if [ ! -f deploy/.env ]; then
  echo "deploy/.env not found. Copy deploy/.env.example to deploy/.env first."
  exit 1
fi

for name in MYSQL_ROOT_PASSWORD MYSQL_APP_PASSWORD REDIS_PASSWORD MINIO_ROOT_USER MINIO_ROOT_PASSWORD; do
  if ! grep -Eq "^${name}=.+" deploy/.env; then
    echo "${name} is missing in deploy/.env"
    exit 1
  fi
done

if [ ! -f deploy/docker/mysql/init/10-nacos.sql ] || [ ! -f deploy/docker/mysql/init/11-xxl-job.sql ]; then
  echo "Middleware schemas are missing. Run: bash deploy/scripts/fetch-middleware-schemas.sh"
  exit 1
fi

docker compose --env-file deploy/.env -f deploy/docker-compose.yml config >/tmp/mall-compose-config.yml
echo "Preflight passed."
