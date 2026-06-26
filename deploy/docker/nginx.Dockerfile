FROM node:20-bookworm-slim AS web-build

WORKDIR /workspace/web
COPY web/package.json web/tsconfig.base.json ./
COPY web/admin ./admin
COPY web/wms-admin ./wms-admin
COPY web/user-h5 ./user-h5
COPY web/role-workbench-h5 ./role-workbench-h5
COPY web/warehouse-h5 ./warehouse-h5

RUN npm install
RUN VITE_PUBLIC_BASE=/admin/ npm run build:admin
RUN VITE_PUBLIC_BASE=/wms/ npm run build:wms-admin
RUN VITE_PUBLIC_BASE=/user/ npm run build:user-h5
RUN VITE_PUBLIC_BASE=/workbench/ npm run build:role-workbench-h5
RUN VITE_PUBLIC_BASE=/warehouse/ npm run build:warehouse-h5

FROM nginx:1.27-alpine

COPY deploy/nginx/conf.d/mall.conf /etc/nginx/conf.d/default.conf
COPY --from=web-build /workspace/web/admin/dist /usr/share/nginx/html/admin
COPY --from=web-build /workspace/web/wms-admin/dist /usr/share/nginx/html/wms
COPY --from=web-build /workspace/web/user-h5/dist/build/h5 /usr/share/nginx/html/user
COPY --from=web-build /workspace/web/role-workbench-h5/dist/build/h5 /usr/share/nginx/html/workbench
COPY --from=web-build /workspace/web/warehouse-h5/dist/build/h5 /usr/share/nginx/html/warehouse
