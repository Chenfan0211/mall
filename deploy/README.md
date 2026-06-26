# Mall Docker Deployment

This directory contains the single-host Docker Compose deployment for the mall platform.

## Public Entry

Only Nginx is exposed to the public network.

| Entry | URL |
| --- | --- |
| Admin web | `http://<PUBLIC_IP>/admin/` |
| WMS admin | `http://<PUBLIC_IP>/wms/` |
| User H5 | `http://<PUBLIC_IP>/user/` |
| Role workbench H5 | `http://<PUBLIC_IP>/workbench/` |
| Warehouse H5 | `http://<PUBLIC_IP>/warehouse/` |
| API gateway | `http://<PUBLIC_IP>/api/**` |

The middleware ports are internal Compose network ports only. Do not publish MySQL, Redis, Nacos, RocketMQ,
Elasticsearch, MinIO, or XXL-Job Admin to the public network for this first deployment.

## Files

| File | Purpose |
| --- | --- |
| `docker-compose.yml` | Full middleware, backend, frontend, and Nginx orchestration |
| `.env.example` | Environment variable template. Copy to `.env` before deployment |
| `docker/backend.Dockerfile` | Builds one Spring Boot service image per Maven module |
| `docker/nginx.Dockerfile` | Builds all five frontends and serves them with Nginx |
| `nginx/conf.d/mall.conf` | Public route and reverse proxy configuration |
| `docker/mysql/init/00-create-databases.sh` | Creates app, Nacos, and XXL-Job databases and grants |
| `scripts/fetch-middleware-schemas.sh` | Downloads Nacos and XXL-Job initialization SQL |
| `scripts/prepare-host.sh` | Creates host directories and opens 80/443 when firewalld exists |
| `scripts/verify-deploy.sh` | Basic deployment smoke checks |

## First Deployment

Run the commands from the repository root.

```bash
cp deploy/.env.example deploy/.env
```

Edit `deploy/.env` and replace every `change-*` password with strong values. Keep this file out of git.

Generate middleware SQL files:

```bash
bash deploy/scripts/fetch-middleware-schemas.sh
```

Run a local deployment preflight:

```bash
bash deploy/scripts/preflight.sh
```

Prepare the target host directories and system parameter:

```bash
sudo MALL_DEPLOY_ROOT=/opt/mall-deploy bash deploy/scripts/prepare-host.sh
```

Copy the repository or release package to `/opt/mall-deploy/source`, then run Compose from the repository root:

```bash
cd /opt/mall-deploy/source
docker compose --env-file deploy/.env -f deploy/docker-compose.yml build
docker compose --env-file deploy/.env -f deploy/docker-compose.yml up -d
```

If Docker Hub is slow or blocked, edit `deploy/.env` and replace Docker Hub images with a reachable mirror.

## Verification

Load the same environment variables used by Compose and run:

```bash
set -a
. deploy/.env
set +a
bash deploy/scripts/verify-deploy.sh
```

Manual checks:

```bash
docker compose --env-file deploy/.env -f deploy/docker-compose.yml ps
curl -I http://127.0.0.1/admin/
curl -I http://127.0.0.1/wms/
curl -I http://127.0.0.1/user/
curl -I http://127.0.0.1/workbench/
curl -I http://127.0.0.1/warehouse/
```

Then open the public entries from a browser:

```text
http://<PUBLIC_IP>/admin/
http://<PUBLIC_IP>/wms/
http://<PUBLIC_IP>/user/
http://<PUBLIC_IP>/workbench/
http://<PUBLIC_IP>/warehouse/
```

## Production Notes

- `192.168.0.254` is an internal IP. Public access requires a real public IP or router/cloud security-group forwarding
  from public `80/443` to the host.
- Add HTTPS later by extending the Nginx service with certificate volumes and a `443 ssl` server block.
- Keep `deploy/.env`, `deploy/data/`, and `deploy/logs/` out of git.
- On SELinux hosts, add `:Z` to bind mounts or set correct container labels if Podman is used instead of Docker.
- Elasticsearch requires `vm.max_map_count=262144`; `prepare-host.sh` sets it for the current boot.
- RocketMQ bind-mounted directories must be writable by UID `3000`; `prepare-host.sh` applies that ownership.
