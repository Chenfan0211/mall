set -e
cd /opt/mall-deploy/source
mkdir -p /opt/mall-deploy/logs
services="mall-auth mall-system mall-operation mall-product mall-sale mall-trade mall-payment mall-aftersale mall-supplier mall-finance mall-wms mall-station mall-user mall-job mall-gateway mall-nginx"
for svc in $services; do
  log="/opt/mall-deploy/logs/build-${svc}-$(date +%Y%m%d-%H%M%S).log"
  echo "== BUILD $svc =="
  set +e
  docker compose -f deploy/docker-compose.yml --env-file deploy/.env build "$svc" > "$log" 2>&1
  rc=$?
  set -e
  echo "BUILD_RC=$rc SERVICE=$svc LOG=$log"
  tail -30 "$log"
  if [ "$rc" -ne 0 ]; then
    exit "$rc"
  fi
done