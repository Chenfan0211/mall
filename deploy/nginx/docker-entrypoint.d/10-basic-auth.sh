#!/bin/sh
set -eu

: "${MALL_BASIC_AUTH_USER:=mallops}"
: "${MALL_BASIC_AUTH_PASSWORD:?MALL_BASIC_AUTH_PASSWORD is required}"

htpasswd -bc /etc/nginx/.htpasswd "${MALL_BASIC_AUTH_USER}" "${MALL_BASIC_AUTH_PASSWORD}" >/dev/null
chmod 600 /etc/nginx/.htpasswd
