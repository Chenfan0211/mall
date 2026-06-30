#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if [ -d "${SCRIPT_DIR}/../../web/warehouse-h5" ] || [ -d "${SCRIPT_DIR}/../../deploy" ]; then
  REPO_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
else
  REPO_ROOT="${SCRIPT_DIR}"
fi
cd "${REPO_ROOT}"

DEPLOY_HOST="${DEPLOY_HOST:-192.168.28.211}"
DEPLOY_USER="${DEPLOY_USER:-root}"
SSH_PORT="${SSH_PORT:-22}"
NGINX_CONTAINER="${NGINX_CONTAINER:-mall-nginx}"
REMOTE_TMP_DIR="${REMOTE_TMP_DIR:-/tmp}"
IMPORT_SQL="${IMPORT_SQL:-0}"
REMOTE_SQL_IMPORT_COMMAND="${REMOTE_SQL_IMPORT_COMMAND:-}"
VERIFY_AFTER_DEPLOY="${VERIFY_AFTER_DEPLOY:-0}"
SKIP_BROWSER_VERIFY="${SKIP_BROWSER_VERIFY:-0}"
VERIFY_DEBUG_PORT="${VERIFY_DEBUG_PORT:-9333}"
TIMESTAMP="$(date +%Y%m%d-%H%M%S)"
SSH_TARGET="${DEPLOY_USER}@${DEPLOY_HOST}"

RELEASE_ZIP="${1:-}"
RELEASE_SOURCE_DIR=""
if [ -z "${RELEASE_ZIP}" ] && [ -f "warehouse/index.html" ]; then
  RELEASE_SOURCE_DIR="${REPO_ROOT}"
elif [ -z "${RELEASE_ZIP}" ]; then
  RELEASE_ZIP="$(ls -t deploy/release/warehouse-h5-release-*.zip 2>/dev/null | head -n 1 || true)"
fi
if [ -z "${RELEASE_SOURCE_DIR}" ] && { [ -z "${RELEASE_ZIP}" ] || [ ! -f "${RELEASE_ZIP}" ]; }; then
  echo "Release zip not found. Pass a zip path or run deploy/scripts/package-warehouse-h5-release.ps1 first." >&2
  exit 1
fi
if [ "${IMPORT_SQL}" = "1" ] && [ -z "${REMOTE_SQL_IMPORT_COMMAND}" ]; then
  echo "IMPORT_SQL=1 requires REMOTE_SQL_IMPORT_COMMAND, for example: docker exec -i mall-mysql mysql -uroot -p\"\$MYSQL_ROOT_PASSWORD\" mall" >&2
  exit 1
fi

SSH_OPTS=(-p "${SSH_PORT}" -o ConnectTimeout=20 -o ServerAliveInterval=30 -o ServerAliveCountMax=6 -o StrictHostKeyChecking=accept-new)
SCP_OPTS=(-P "${SSH_PORT}" -o ConnectTimeout=20 -o ServerAliveInterval=30 -o ServerAliveCountMax=6 -o StrictHostKeyChecking=accept-new)

for command_name in unzip tar ssh scp; do
  if ! command -v "${command_name}" >/dev/null 2>&1; then
    echo "${command_name} command not found" >&2
    exit 1
  fi
done

work_dir="$(mktemp -d)"
trap 'rm -rf "${work_dir}"' EXIT

if [ -n "${RELEASE_SOURCE_DIR}" ]; then
  echo "Using extracted release directory: ${RELEASE_SOURCE_DIR}"
  source_dir="${RELEASE_SOURCE_DIR}"
else
  echo "Using release zip: ${RELEASE_ZIP}"
  unzip -q "${RELEASE_ZIP}" -d "${work_dir}"
  source_dir="${work_dir}"
fi

test -f "${source_dir}/warehouse/index.html"
test -f "${source_dir}/sql/warehouse-h5-test-data-20260629.sql"

SQL_TEXT="$(cat "${source_dir}/sql/warehouse-h5-test-data-20260629.sql")"
printf '%s' "${SQL_TEXT}" | grep -q 'ACC_TEST_WMS_BUYER_H5'
printf '%s' "${SQL_TEXT}" | grep -q 'ACC_TEST_WMS_MANAGER_H5'
printf '%s' "${SQL_TEXT}" | grep -q 'ON DUPLICATE KEY UPDATE'
printf '%s' "${SQL_TEXT}" | grep -q 'module_code'
printf '%s' "${SQL_TEXT}" | grep -q 'risk_level'
if printf '%s' "${SQL_TEXT}" | grep -q 'resource_code'; then
  echo "SQL contains invalid sys_role_button.resource_code" >&2
  exit 1
fi

INDEX_HTML="$(cat "${source_dir}/warehouse/index.html")"
ASSET_PATH="$(printf '%s' "${INDEX_HTML}" | grep -oE '/warehouse/assets/[^"]+\.js' | head -n 1 || true)"
if [ -z "${ASSET_PATH}" ]; then
  echo "No /warehouse/ JS asset found in release index.html" >&2
  exit 1
fi

BUNDLE_TEXT="$(cat "${source_dir}/${ASSET_PATH#/}")"
while IFS= read -r chunk; do
  chunk_file="${source_dir}/warehouse/${chunk}"
  if [ -f "${chunk_file}" ]; then
    BUNDLE_TEXT="${BUNDLE_TEXT}$(cat "${chunk_file}")"
  fi
done < <(printf '%s' "${BUNDLE_TEXT}" | grep -oE 'assets/[A-Za-z0-9_.-]+\.js' | sort -u || true)

printf '%s' "${BUNDLE_TEXT}" | grep -q '/auth/password-login'
printf '%s' "${BUNDLE_TEXT}" | grep -q 'mall_warehouse_h5_session'
printf '%s' "${BUNDLE_TEXT}" | grep -q 'mall_warehouse_h5_driver_tab'
printf '%s' "${BUNDLE_TEXT}" | grep -q 'warehouse-bottom'

archive_name="warehouse-h5-release-${TIMESTAMP}.tar.gz"
local_archive="${work_dir}/${archive_name}"
remote_archive="${REMOTE_TMP_DIR}/${archive_name}"
tar -czf "${local_archive}" -C "${source_dir}/warehouse" .

sql_remote_path="${REMOTE_TMP_DIR}/warehouse-h5-test-data-${TIMESTAMP}.sql"

echo "Uploading static bundle to ${SSH_TARGET}:${remote_archive}"
scp "${SCP_OPTS[@]}" "${local_archive}" "${SSH_TARGET}:${remote_archive}"

if [ "${IMPORT_SQL}" = "1" ]; then
  echo "Uploading SQL test data to ${SSH_TARGET}:${sql_remote_path}"
  scp "${SCP_OPTS[@]}" "${source_dir}/sql/warehouse-h5-test-data-20260629.sql" "${SSH_TARGET}:${sql_remote_path}"
fi

echo "Replacing /warehouse/ assets inside ${NGINX_CONTAINER}"
ssh "${SSH_OPTS[@]}" "${SSH_TARGET}" \
  "REMOTE_ARCHIVE='${remote_archive}' NGINX_CONTAINER='${NGINX_CONTAINER}' TIMESTAMP='${TIMESTAMP}' bash -s" <<'REMOTE_SCRIPT'
set -euo pipefail

work_dir="/tmp/warehouse-h5-release-${TIMESTAMP}"
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

if [ "${IMPORT_SQL}" = "1" ]; then
  echo "Importing SQL with REMOTE_SQL_IMPORT_COMMAND"
  ssh "${SSH_OPTS[@]}" "${SSH_TARGET}" \
    "SQL_REMOTE_PATH='${sql_remote_path}' REMOTE_SQL_IMPORT_COMMAND='${REMOTE_SQL_IMPORT_COMMAND}' bash -s" <<'REMOTE_SQL'
set -euo pipefail
sh -c "${REMOTE_SQL_IMPORT_COMMAND}" < "${SQL_REMOTE_PATH}"
rm -f "${SQL_REMOTE_PATH}"
echo "WAREHOUSE_H5_SQL_IMPORTED"
REMOTE_SQL
fi

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

if [ "${VERIFY_AFTER_DEPLOY}" = "1" ]; then
  if command -v pwsh >/dev/null 2>&1; then
    powershell_cmd="pwsh"
  elif command -v powershell >/dev/null 2>&1; then
    powershell_cmd="powershell"
  else
    echo "VERIFY_AFTER_DEPLOY=1 requires pwsh or powershell on the release machine" >&2
    exit 1
  fi
  verify_args=(-NoProfile -ExecutionPolicy Bypass -File deploy/scripts/verify-warehouse-h5-full.ps1 -HostUrl "http://${DEPLOY_HOST}" -DebugPort "${VERIFY_DEBUG_PORT}")
  if [ "${SKIP_BROWSER_VERIFY}" = "1" ]; then
    verify_args+=(-SkipBrowser)
  fi
  "${powershell_cmd}" "${verify_args[@]}"
fi
