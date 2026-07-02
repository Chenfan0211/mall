#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
INIT_DIR="${REPO_ROOT}/deploy/docker/mysql/init"

NACOS_VERSION="${NACOS_VERSION:-2.4.3}"
XXL_JOB_VERSION="${XXL_JOB_VERSION:-2.4.1}"
NACOS_DATABASE="${NACOS_DATABASE:-nacos_config}"
XXL_JOB_DATABASE="${XXL_JOB_DATABASE:-xxl_job}"

NACOS_SCHEMA_URL="${NACOS_SCHEMA_URL:-https://raw.githubusercontent.com/alibaba/nacos/${NACOS_VERSION}/distribution/conf/mysql-schema.sql}"
XXL_JOB_SCHEMA_URL="${XXL_JOB_SCHEMA_URL:-https://raw.githubusercontent.com/xuxueli/xxl-job/${XXL_JOB_VERSION}/doc/db/tables_xxl_job.sql}"
CURL_CONNECT_TIMEOUT="${CURL_CONNECT_TIMEOUT:-15}"
CURL_MAX_TIME="${CURL_MAX_TIME:-120}"

mkdir -p "${INIT_DIR}"

tmp_nacos="$(mktemp)"
tmp_xxl="$(mktemp)"
trap 'rm -f "${tmp_nacos}" "${tmp_xxl}"' EXIT

curl -fsSL --connect-timeout "${CURL_CONNECT_TIMEOUT}" --max-time "${CURL_MAX_TIME}" "${NACOS_SCHEMA_URL}" -o "${tmp_nacos}"
{
    printf 'USE `%s`;\n' "${NACOS_DATABASE}"
    cat "${tmp_nacos}"
} > "${INIT_DIR}/10-nacos.sql"

curl -fsSL --connect-timeout "${CURL_CONNECT_TIMEOUT}" --max-time "${CURL_MAX_TIME}" "${XXL_JOB_SCHEMA_URL}" -o "${tmp_xxl}"
{
    printf 'USE `%s`;\n' "${XXL_JOB_DATABASE}"
    sed -E '/^[[:space:]]*CREATE[[:space:]]+database[[:space:]]/Id; /^[[:space:]]*use[[:space:]]+/Id' "${tmp_xxl}"
} > "${INIT_DIR}/11-xxl-job.sql"

echo "Generated:"
echo "  ${INIT_DIR}/10-nacos.sql"
echo "  ${INIT_DIR}/11-xxl-job.sql"
