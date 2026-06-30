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
| `scripts/redeploy-current-workspace.sh` | Packages the current local workspace and redeploys it to the target host |
| `scripts/verify-deploy.sh` | Full deployment smoke checks for middleware, backend services, Nginx, and public pages |
| `scripts/diagnose-deploy.sh` | Collects failed container state, logs, ports, disk, memory, and Nacos config checks |

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

## Redeploy Current Workspace

Use this flow when the server should receive the exact local workspace, including uncommitted and untracked files.
The script excludes `.git`, `target`, `node_modules`, `dist`, local logs, local data, and local `deploy/.env`.

Run from the repository root:

```bash
bash deploy/scripts/redeploy-current-workspace.sh
```

Defaults:

| Variable | Default |
| --- | --- |
| `DEPLOY_HOST` | `192.168.28.211` |
| `DEPLOY_USER` | `root` |
| `SSH_PORT` | `22` |
| `MALL_DEPLOY_ROOT` | `/opt/mall-deploy` |

Example override:

```bash
DEPLOY_HOST=192.168.28.211 DEPLOY_USER=root bash deploy/scripts/redeploy-current-workspace.sh
```

The script preserves the existing server-side `/opt/mall-deploy/source/deploy/.env`. It does not store or pass
passwords in the repository. If the server has no `deploy/.env`, the script creates it from `.env.example`, stops,
and requires replacing every `change-*` value before rerunning.

Deployment failure does not roll back automatically. The failed source tree is left at `/opt/mall-deploy/source`, and
the previous source tree is kept under `/opt/mall-deploy/backups/source-YYYYMMDD-HHMMSS`. Inspect the diagnostic report
before deciding whether to fix forward or restore a backup.

## Verification

Load the same environment variables used by Compose and run:

```bash
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

If verification fails, collect a diagnostic report without restarting or deleting anything:

```bash
bash deploy/scripts/diagnose-deploy.sh
```

Reports are written to `/opt/mall-deploy/logs/deploy-diagnose-YYYYMMDD-HHMMSS.log`.

## Warehouse H5 Hot Deploy

Use this flow when only `web/warehouse-h5` changed and the server already has the Docker Compose stack running.

```bash
DEPLOY_HOST=192.168.28.211 DEPLOY_USER=root bash deploy/scripts/deploy-warehouse-h5.sh
```

The script builds the H5 bundle with `VITE_PUBLIC_BASE=/warehouse/`, uploads it, replaces
`/usr/share/nginx/html/warehouse/` inside the `mall-nginx` container, runs `nginx -t`, then checks that the public
bundle contains `/auth/password-login`, `接口数据`, and `作业统计`.

After deploying, run the Windows smoke check from the repository root:

```powershell
powershell -ExecutionPolicy Bypass -File deploy/scripts/verify-warehouse-h5.ps1
```

For local visual and interaction validation while `npm run dev:h5` is serving `http://localhost:5177/`, run:

```powershell
powershell -ExecutionPolicy Bypass -File deploy/scripts/verify-warehouse-h5-local.ps1 -Url http://localhost:5177/
```

The local check opens Chrome at `375 x 812`, verifies the login role selector, all six role navigations, data-source
labels, horizontal overflow, and console/runtime errors.

The smoke check verifies:

- `http://192.168.28.211/warehouse/` serves the latest warehouse H5 bundle.
- unauthenticated `/api/wms/*` requests return `401/403`.
- `POST /api/auth/password-login` succeeds for `test_wms_receiver / Test@123456`.
- all six Warehouse H5 role accounts can log in with `Test@123456`:
  `test_wms_receiver`, `test_wms_buyer`, `test_wms_picker`, `test_wms_loader`, `test_wms_driver`, `test_wms_manager`.
- the five WMS read-only lists return `code=0` and `total > 0`.

If the bundle check passes but `test_wms_buyer` or `test_wms_manager` fails, import the 2026-06-29 warehouse H5
test-data block appended in `Sql/mall.sql`, then rerun the smoke check. To temporarily verify only the deployed bundle
and receiver API path before SQL import, run:

```powershell
powershell -ExecutionPolicy Bypass -File deploy/scripts/verify-warehouse-h5.ps1 -SkipRoleAccounts
```

To export only the Warehouse H5 test-data block from the large SQL file:

```powershell
powershell -ExecutionPolicy Bypass -File deploy/scripts/export-warehouse-h5-test-sql.ps1
```

This writes `Sql/warehouse-h5-test-data-20260629.sql`, which can be imported by the DBA or ops account. It does not
connect to MySQL by itself.

After importing the SQL but before redeploying the frontend, you can verify only the six role accounts:

```powershell
powershell -ExecutionPolicy Bypass -File deploy/scripts/verify-warehouse-h5.ps1 -OnlyRoleAccounts
```

## Production Notes

- `192.168.0.254` is an internal IP. Public access requires a real public IP or router/cloud security-group forwarding
  from public `80/443` to the host.
- Add HTTPS later by extending the Nginx service with certificate volumes and a `443 ssl` server block.
- Keep `deploy/.env`, `deploy/data/`, and `deploy/logs/` out of git.
- On SELinux hosts, add `:Z` to bind mounts or set correct container labels if Podman is used instead of Docker.
- Elasticsearch requires `vm.max_map_count=262144`; `prepare-host.sh` sets it for the current boot.
- RocketMQ bind-mounted directories must be writable by UID `3000`; `prepare-host.sh` applies that ownership.
