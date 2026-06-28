set -e
cd /opt/mall-deploy/source
mkdir -p /opt/mall-deploy/logs
log="/opt/mall-deploy/logs/build-all-$(date +%Y%m%d-%H%M%S).log"
services="mall-system mall-operation mall-product mall-sale mall-trade mall-payment mall-aftersale mall-supplier mall-finance mall-wms mall-station mall-user mall-job mall-gateway mall-nginx"
set +e
docker compose -f deploy/docker-compose.yml --env-file deploy/.env build $services > "$log" 2>&1
rc=$?
set -e
echo "BUILD_RC=$rc LOG=$log"
tail -120 "$log"
exit $rc