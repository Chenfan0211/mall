#!/usr/bin/env bash
set -euo pipefail

cd /opt/mall-deploy/source

get_env() {
  local key="$1"
  local default="$2"
  local val
  val="$(grep -E "^${key}=" deploy/.env | tail -1 | sed 's/^[^=]*=//' || true)"
  if [ -n "${val}" ]; then
    printf '%s' "${val}"
  else
    printf '%s' "${default}"
  fi
}

NACOS_ADDR="${NACOS_ADDR:-http://127.0.0.1:8848}"
NAMESPACE_ID="$(get_env NACOS_NAMESPACE mall-test)"
GROUP="$(get_env NACOS_GROUP MALL_GROUP)"
MYSQL_DB="$(get_env MYSQL_APP_DATABASE mall)"
MYSQL_USER="$(get_env MYSQL_APP_USER mall)"
MYSQL_PASS="$(get_env MYSQL_APP_PASSWORD '')"
REDIS_PASS="$(get_env REDIS_PASSWORD '')"
TOKEN_SECRET="$(get_env MALL_TOKEN_SECRET '')"
MINIO_USER="$(get_env MINIO_ROOT_USER '')"
MINIO_PASS="$(get_env MINIO_ROOT_PASSWORD '')"

until curl -fsS "${NACOS_ADDR}/nacos/" >/dev/null; do
  sleep 2
done

curl -fsS -X POST "${NACOS_ADDR}/nacos/v1/console/namespaces" \
  --data-urlencode "customNamespaceId=${NAMESPACE_ID}" \
  --data-urlencode "namespaceName=${NAMESPACE_ID}" \
  --data-urlencode "namespaceDesc=mall-test-namespace" >/tmp/nacos_namespace.out || true

publish_config() {
  local data_id="$1"
  local content_file="$2"
  curl -fsS -X POST "${NACOS_ADDR}/nacos/v1/cs/configs" \
    --data-urlencode "tenant=${NAMESPACE_ID}" \
    --data-urlencode "dataId=${data_id}" \
    --data-urlencode "group=${GROUP}" \
    --data-urlencode "type=yaml" \
    --data-urlencode "content@${content_file}" >/dev/null
  echo "PUBLISHED ${data_id}"
}

tmpdir="$(mktemp -d)"
trap 'rm -rf "${tmpdir}"' EXIT

cat > "${tmpdir}/mall-common.yaml" <<EOF
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://mall-mysql:3306/${MYSQL_DB}?useUnicode=true&connectionCollation=utf8mb4_unicode_ci&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: ${MYSQL_USER}
    password: ${MYSQL_PASS}
  data:
    redis:
      host: mall-redis
      port: 6379
      password: ${REDIS_PASS}
      database: 0

mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      logic-delete-field: is_deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

rocketmq:
  name-server: mall-rocketmq-namesrv:9876

mall:
  security:
    token-secret: ${TOKEN_SECRET}
  minio:
    endpoint: http://mall-minio:9000
    access-key: ${MINIO_USER}
    secret-key: ${MINIO_PASS}
  elasticsearch:
    uris: http://mall-elasticsearch:9200
  xxl-job:
    admin-addresses: http://mall-xxl-job:8080/xxl-job-admin
EOF
publish_config mall-common.yaml "${tmpdir}/mall-common.yaml"

write_service_config() {
  local module="$1"
  local port="$2"
  cat > "${tmpdir}/${module}.yaml" <<EOF
server:
  port: ${port}

spring:
  application:
    name: ${module}
EOF
  publish_config "${module}.yaml" "${tmpdir}/${module}.yaml"
}

write_service_config mall-auth 18080
write_service_config mall-system 18081
write_service_config mall-operation 18082
write_service_config mall-product 18083
write_service_config mall-sale 18084
write_service_config mall-trade 18085
write_service_config mall-payment 18086
write_service_config mall-aftersale 18087
write_service_config mall-supplier 18088
write_service_config mall-finance 18089
write_service_config mall-wms 18090
write_service_config mall-station 18091
write_service_config mall-user 18092
write_service_config mall-job 18093

cat > "${tmpdir}/mall-gateway.yaml" <<'EOF'
server:
  port: 18000

spring:
  application:
    name: mall-gateway
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowed-origin-patterns: "*"
            allowed-methods:
              - GET
              - POST
              - PUT
              - DELETE
              - OPTIONS
            allowed-headers: "*"
            allow-credentials: true
      routes:
        - id: mall-auth
          uri: http://mall-auth:18080
          predicates:
            - Path=/api/auth/**
        - id: mall-system
          uri: http://mall-system:18081
          predicates:
            - Path=/api/system/**
        - id: mall-operation
          uri: http://mall-operation:18082
          predicates:
            - Path=/api/operation/**
        - id: mall-product
          uri: http://mall-product:18083
          predicates:
            - Path=/api/product/**
        - id: mall-sale
          uri: http://mall-sale:18084
          predicates:
            - Path=/api/sale/**
        - id: mall-trade
          uri: http://mall-trade:18085
          predicates:
            - Path=/api/trade/**
        - id: mall-payment
          uri: http://mall-payment:18086
          predicates:
            - Path=/api/payment/**
        - id: mall-aftersale
          uri: http://mall-aftersale:18087
          predicates:
            - Path=/api/aftersale/**
        - id: mall-supplier
          uri: http://mall-supplier:18088
          predicates:
            - Path=/api/supplier/**
        - id: mall-finance
          uri: http://mall-finance:18089
          predicates:
            - Path=/api/finance/**
        - id: mall-wms
          uri: http://mall-wms:18090
          predicates:
            - Path=/api/wms/**
        - id: mall-station
          uri: http://mall-station:18091
          predicates:
            - Path=/api/station/**
        - id: mall-user
          uri: http://mall-user:18092
          predicates:
            - Path=/api/user/**
EOF
publish_config mall-gateway.yaml "${tmpdir}/mall-gateway.yaml"

curl -fsS "${NACOS_ADDR}/nacos/v1/cs/configs?tenant=${NAMESPACE_ID}&group=${GROUP}&dataId=mall-common.yaml" \
  >/tmp/nacos_common_check.yaml

if grep -q 'jdbc:mysql://mall-mysql:3306' /tmp/nacos_common_check.yaml; then
  echo "NACOS_CONFIG_DONE namespace=${NAMESPACE_ID} group=${GROUP}"
else
  echo "NACOS_CONFIG_CHECK_FAILED" >&2
  exit 1
fi
