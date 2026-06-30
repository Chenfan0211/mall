#!/usr/bin/env bash
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "${REPO_ROOT}"

DEPLOY_HOST="${DEPLOY_HOST:-192.168.28.211}"
DEPLOY_USER="${DEPLOY_USER:-root}"
SSH_PORT="${SSH_PORT:-22}"
NGINX_CONTAINER="${NGINX_CONTAINER:-mall-nginx}"
REMOTE_TMP_DIR="${REMOTE_TMP_DIR:-/tmp}"
TIMESTAMP="$(date +%Y%m%d-%H%M%S)"
ARCHIVE_NAME="warehouse-h5-${TIMESTAMP}.tar.gz"
LOCAL_DIST="${REPO_ROOT}/web/warehouse-h5/dist/build/h5"
LOCAL_ARCHIVE="${REPO_ROOT}/web/warehouse-h5/dist/${ARCHIVE_NAME}"
REMOTE_ARCHIVE="${REMOTE_TMP_DIR}/${ARCHIVE_NAME}"
SSH_TARGET="${DEPLOY_USER}@${DEPLOY_HOST}"

SSH_OPTS=(-p "${SSH_PORT}" -o ConnectTimeout=20 -o ServerAliveInterval=30 -o ServerAliveCountMax=6 -o StrictHostKeyChecking=accept-new)
SCP_OPTS=(-P "${SSH_PORT}" -o ConnectTimeout=20 -o ServerAliveInterval=30 -o ServerAliveCountMax=6 -o StrictHostKeyChecking=accept-new)

for command_name in npm tar ssh scp; do
  if ! command -v "${command_name}" >/dev/null 2>&1; then
    echo "${command_name} command not found" >&2
    exit 1
  fi
done

echo "Building warehouse-h5 for /warehouse/"
(
  cd web/warehouse-h5
  VITE_PUBLIC_BASE=/warehouse/ npm run build:h5
)

test -f "${LOCAL_DIST}/index.html"
tar -czf "${LOCAL_ARCHIVE}" -C "${LOCAL_DIST}" .

echo "Uploading ${LOCAL_ARCHIVE} to ${SSH_TARGET}:${REMOTE_ARCHIVE}"
scp "${SCP_OPTS[@]}" "${LOCAL_ARCHIVE}" "${SSH_TARGET}:${REMOTE_ARCHIVE}"

echo "Replacing /warehouse/ assets inside ${NGINX_CONTAINER}"
ssh "${SSH_OPTS[@]}" "${SSH_TARGET}" \
  "REMOTE_ARCHIVE='${REMOTE_ARCHIVE}' NGINX_CONTAINER='${NGINX_CONTAINER}' TIMESTAMP='${TIMESTAMP}' bash -s" <<'REMOTE_SCRIPT'
set -euo pipefail

work_dir="/tmp/warehouse-h5-${TIMESTAMP}"
backup_dir="/tmp/warehouse-h5-backup-${TIMESTAMP}"
mkdir -p "${work_dir}"
tar -xzf "${REMOTE_ARCHIVE}" -C "${work_dir}"

docker exec "${NGINX_CONTAINER}" sh -c "rm -rf '${backup_dir}' && cp -a /usr/share/nginx/html/warehouse '${backup_dir}'"
docker exec "${NGINX_CONTAINER}" sh -c "rm -rf /usr/share/nginx/html/warehouse/*"
docker cp "${work_dir}/." "${NGINX_CONTAINER}:/usr/share/nginx/html/warehouse/"
docker exec "${NGINX_CONTAINER}" nginx -t
rm -rf "${work_dir}" "${REMOTE_ARCHIVE}"

echo "WAREHOUSE_H5_DEPLOYED backup=${backup_dir}"
REMOTE_SCRIPT

echo "Checking public warehouse entry"
curl -fsSI "http://${DEPLOY_HOST}/warehouse/" >/dev/null
INDEX_HTML="$(curl -fsSL "http://${DEPLOY_HOST}/warehouse/")"
ASSET_PATH="$(printf '%s' "${INDEX_HTML}" | grep -oE '/warehouse/assets/[^"]+\.js' | head -n 1 || true)"
if [ -z "${ASSET_PATH}" ]; then
  echo "No warehouse asset found after deployment" >&2
  exit 1
fi
BUNDLE_TEXT="$(curl -fsSL "http://${DEPLOY_HOST}${ASSET_PATH}")"
for CHUNK_PATH in $(printf '%s' "${BUNDLE_TEXT}" | grep -oE 'assets/[A-Za-z0-9_.-]+\.js' | sort -u); do
  CHUNK_TEXT="$(curl -fsSL "http://${DEPLOY_HOST}/warehouse/${CHUNK_PATH}" || true)"
  BUNDLE_TEXT="${BUNDLE_TEXT}${CHUNK_TEXT}"
done
printf '%s' "${BUNDLE_TEXT}" | grep -q '/auth/password-login'
printf '%s' "${BUNDLE_TEXT}" | grep -q 'mall_warehouse_h5_session'
printf '%s' "${BUNDLE_TEXT}" | grep -q 'mall_warehouse_h5_driver_tab'
printf '%s' "${BUNDLE_TEXT}" | grep -q 'warehouse-bottom'
echo "Warehouse H5 deployed: http://${DEPLOY_HOST}/warehouse/"
