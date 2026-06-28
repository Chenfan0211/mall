set -e
cd /opt/mall-deploy/source
mkdir -p /opt/mall-deploy/logs
log="/opt/mall-deploy/logs/build-mall-auth-$(date +%Y%m%d-%H%M%S).log"
set +e
docker compose -f deploy/docker-compose.yml --env-file deploy/.env build mall-auth > "$log" 2>&1
rc=$?
set -e
echo "BUILD_RC=$rc LOG=$log"
tail -80 "$log"
exit $rc