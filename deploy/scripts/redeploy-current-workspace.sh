#!/usr/bin/env bash
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "${REPO_ROOT}"

DEPLOY_HOST="${DEPLOY_HOST:-192.168.28.211}"
DEPLOY_USER="${DEPLOY_USER:-root}"
SSH_PORT="${SSH_PORT:-22}"
MALL_DEPLOY_ROOT="${MALL_DEPLOY_ROOT:-/opt/mall-deploy}"
REMOTE_TMP_DIR="${REMOTE_TMP_DIR:-/tmp}"
DEPLOY_RETRIES="${DEPLOY_RETRIES:-5}"
DEPLOY_RETRY_SECONDS="${DEPLOY_RETRY_SECONDS:-20}"
TIMESTAMP="$(date +%Y%m%d-%H%M%S)"
ARCHIVE_NAME="mall-source-${TIMESTAMP}.tar.gz"

SSH_TARGET="${DEPLOY_USER}@${DEPLOY_HOST}"
SSH_COMMON_OPTS=(
  -o ConnectTimeout=20
  -o ServerAliveInterval=30
  -o ServerAliveCountMax=6
  -o StrictHostKeyChecking=accept-new
  -o NumberOfPasswordPrompts=1
)
SSH_OPTS=(-p "${SSH_PORT}" "${SSH_COMMON_OPTS[@]}")
SCP_OPTS=(-P "${SSH_PORT}" "${SSH_COMMON_OPTS[@]}")

for command_name in tar ssh scp; do
  if ! command -v "${command_name}" >/dev/null 2>&1; then
    echo "${command_name} command not found" >&2
    exit 1
  fi
done

tmp_dir="$(mktemp -d)"
archive_path="${tmp_dir}/${ARCHIVE_NAME}"
remote_script_path="${tmp_dir}/mall-remote-deploy-${TIMESTAMP}.sh"
remote_script="${REMOTE_TMP_DIR}/mall-remote-deploy-${TIMESTAMP}.sh"
remote_archive="${REMOTE_TMP_DIR}/${ARCHIVE_NAME}"

cleanup() {
  rm -rf "${tmp_dir}"
}
trap cleanup EXIT

retry() {
  local description="$1"
  shift
  local attempt=1
  while true; do
    echo "${description} (attempt ${attempt}/${DEPLOY_RETRIES})"
    if "$@"; then
      return 0
    fi
    if [ "${attempt}" -ge "${DEPLOY_RETRIES}" ]; then
      echo "${description} failed after ${DEPLOY_RETRIES} attempts" >&2
      return 1
    fi
    echo "${description} failed. Retrying in ${DEPLOY_RETRY_SECONDS}s..."
    sleep "${DEPLOY_RETRY_SECONDS}"
    attempt=$((attempt + 1))
  done
}

echo "Packing current workspace from ${REPO_ROOT}"
tar \
  --warning=no-file-changed \
  --ignore-failed-read \
  --exclude='.git' \
  --exclude='target' \
  --exclude='node_modules' \
  --exclude='dist' \
  --exclude='unpackage' \
  --exclude='deploy/.env' \
  --exclude='deploy/data' \
  --exclude='deploy/logs' \
  --exclude='*.log' \
  -czf "${archive_path}" \
  -C "${REPO_ROOT}" .

cat > "${remote_script_path}" <<'REMOTE_SCRIPT'
#!/usr/bin/env bash
set -euo pipefail

deploy_root="${MALL_DEPLOY_ROOT:-/opt/mall-deploy}"
source_dir="${deploy_root}/source"
backup_root="${deploy_root}/backups"
log_dir="${deploy_root}/logs"
timestamp="$(date +%Y%m%d-%H%M%S)"
backup_dir="${backup_root}/source-${timestamp}"
archive="${REMOTE_ARCHIVE:?REMOTE_ARCHIVE is required}"
env_backup="/tmp/mall-deploy-env-${timestamp}"

mkdir -p "${deploy_root}" "${backup_root}" "${log_dir}"

on_error() {
  local exit_code="$?"
  echo "REMOTE_DEPLOY_FAILED exit=${exit_code}"
  echo "No rollback was performed. Current failed workspace is left at ${source_dir}."
  if [ -d "${source_dir}" ] && [ -f "${source_dir}/deploy/scripts/diagnose-deploy.sh" ]; then
    cd "${source_dir}"
    bash deploy/scripts/diagnose-deploy.sh || true
  fi
  exit "${exit_code}"
}
trap on_error ERR

if [ -f "${source_dir}/deploy/.env" ]; then
  cp "${source_dir}/deploy/.env" "${env_backup}"
fi

if [ -d "${source_dir}" ]; then
  mv "${source_dir}" "${backup_dir}"
  echo "Backed up previous source to ${backup_dir}"
fi

mkdir -p "${source_dir}"
tar -xzf "${archive}" -C "${source_dir}"

find "${source_dir}/deploy/scripts" -type f -name '*.sh' -exec sed -i 's/\r$//' {} + 2>/dev/null || true
find "${source_dir}/deploy/nginx/docker-entrypoint.d" -type f -exec sed -i 's/\r$//' {} + 2>/dev/null || true

if [ -f "${env_backup}" ]; then
  mkdir -p "${source_dir}/deploy"
  cp "${env_backup}" "${source_dir}/deploy/.env"
else
  if [ ! -f "${source_dir}/deploy/.env" ] && [ -f "${source_dir}/deploy/.env.example" ]; then
    cp "${source_dir}/deploy/.env.example" "${source_dir}/deploy/.env"
    echo "deploy/.env was missing on the server. A template was created at ${source_dir}/deploy/.env."
    echo "Replace all change-* values and rerun deployment."
    exit 2
  fi
fi

rm -f "${env_backup}" || true
chmod +x "${source_dir}"/deploy/scripts/*.sh 2>/dev/null || true
chmod +x "${source_dir}"/deploy/nginx/docker-entrypoint.d/*.sh 2>/dev/null || true

cd "${source_dir}"

bash -n deploy/scripts/*.sh
bash deploy/scripts/preflight.sh
MALL_DEPLOY_ROOT="${deploy_root}" bash deploy/scripts/prepare-host.sh

if [ ! -f deploy/docker/mysql/init/10-nacos.sql ] || [ ! -f deploy/docker/mysql/init/11-xxl-job.sql ]; then
  bash deploy/scripts/fetch-middleware-schemas.sh
fi

docker compose --env-file deploy/.env -f deploy/docker-compose.yml build

docker compose --env-file deploy/.env -f deploy/docker-compose.yml up -d \
  mall-mysql \
  mall-redis \
  mall-nacos \
  mall-rocketmq-namesrv \
  mall-rocketmq-broker \
  mall-elasticsearch \
  mall-minio \
  mall-xxl-job

MALL_DEPLOY_ROOT="${deploy_root}" bash deploy/scripts/publish-nacos-configs.sh
docker compose --env-file deploy/.env -f deploy/docker-compose.yml up -d
bash deploy/scripts/verify-deploy.sh

rm -f "${archive}" "${REMOTE_SCRIPT_PATH:-}"
echo "REMOTE_DEPLOY_DONE source=${source_dir}"
REMOTE_SCRIPT

echo "Uploading ${archive_path} to ${SSH_TARGET}:${remote_archive}"
retry "Upload archive" scp "${SCP_OPTS[@]}" "${archive_path}" "${SSH_TARGET}:${remote_archive}"
echo "Uploading remote deploy runner to ${SSH_TARGET}:${remote_script}"
retry "Upload remote deploy runner" scp "${SCP_OPTS[@]}" "${remote_script_path}" "${SSH_TARGET}:${remote_script}"

echo "Running remote deployment on ${SSH_TARGET}"
retry "Run remote deployment" ssh "${SSH_OPTS[@]}" "${SSH_TARGET}" \
  "REMOTE_ARCHIVE='${remote_archive}' REMOTE_SCRIPT_PATH='${remote_script}' MALL_DEPLOY_ROOT='${MALL_DEPLOY_ROOT}' bash '${remote_script}'"

echo "Redeploy finished. Verify public pages from your browser:"
echo "  http://${DEPLOY_HOST}/admin/"
echo "  http://${DEPLOY_HOST}/wms/"
echo "  http://${DEPLOY_HOST}/user/"
echo "  http://${DEPLOY_HOST}/workbench/"
echo "  http://${DEPLOY_HOST}/warehouse/"
