#!/bin/bash
set -e

APP_DB="${MYSQL_APP_DATABASE:-mall}"
NACOS_DB="${NACOS_DATABASE:-nacos_config}"
XXL_DB="${XXL_JOB_DATABASE:-xxl_job}"
APP_USER="${MYSQL_APP_USER:-mall}"
APP_PASSWORD="${MYSQL_APP_PASSWORD:?MYSQL_APP_PASSWORD is required}"

mysql --default-character-set=utf8mb4 -uroot -p"${MYSQL_ROOT_PASSWORD}" <<EOSQL
CREATE DATABASE IF NOT EXISTS \`${APP_DB}\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS \`${NACOS_DB}\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS \`${XXL_DB}\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS '${APP_USER}'@'%' IDENTIFIED BY '${APP_PASSWORD}';
GRANT ALL PRIVILEGES ON \`${APP_DB}\`.* TO '${APP_USER}'@'%';
GRANT ALL PRIVILEGES ON \`${NACOS_DB}\`.* TO '${APP_USER}'@'%';
GRANT ALL PRIVILEGES ON \`${XXL_DB}\`.* TO '${APP_USER}'@'%';
FLUSH PRIVILEGES;
EOSQL

if [ -f /app-sql/mall.sql ]; then
  mysql --default-character-set=utf8mb4 -uroot -p"${MYSQL_ROOT_PASSWORD}" "${APP_DB}" < /app-sql/mall.sql
fi
