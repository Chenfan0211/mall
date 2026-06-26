#!/usr/bin/env bash
set -euo pipefail

DEPLOY_DIR="${MALL_DEPLOY_ROOT:-${DEPLOY_DIR:-/opt/mall-deploy}}"

mkdir -p \
  "${DEPLOY_DIR}/data/mysql" \
  "${DEPLOY_DIR}/data/redis" \
  "${DEPLOY_DIR}/data/nacos/logs" \
  "${DEPLOY_DIR}/data/nacos/data" \
  "${DEPLOY_DIR}/data/rocketmq/namesrv/logs" \
  "${DEPLOY_DIR}/data/rocketmq/broker/logs" \
  "${DEPLOY_DIR}/data/rocketmq/broker/store" \
  "${DEPLOY_DIR}/data/elasticsearch" \
  "${DEPLOY_DIR}/data/minio" \
  "${DEPLOY_DIR}/logs"

sysctl -w vm.max_map_count=262144

chown -R 3000:3000 "${DEPLOY_DIR}/data/rocketmq" || true
chown -R 1000:0 "${DEPLOY_DIR}/data/elasticsearch" || true

if command -v firewall-cmd >/dev/null 2>&1; then
  firewall-cmd --permanent --add-service=http
  firewall-cmd --permanent --add-service=https
  firewall-cmd --reload
fi

echo "Host prepared at ${DEPLOY_DIR}"
