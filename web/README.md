# 商城前端工作区

本目录是商城项目第一版前端骨架，包含 5 个独立应用：

| 应用 | 目录 | 端口 | 技术栈 | 定位 |
| --- | --- | --- | --- | --- |
| 运营后台 | `admin` | `5173` | Vue3 + Vite + TypeScript + Element Plus | 平台运营、采购、财务、系统管理 |
| WMS 后台 | `wms-admin` | `5174` | Vue3 + Vite + TypeScript + Element Plus | 仓库主管、仓管、采购到仓 |
| 用户端 H5 | `user-h5` | `5175` | uni-app + Vue3 + TypeScript | 浏览、下单、支付、订单、售后 |
| 角色工作台 H5 | `role-workbench-h5` | `5176` | uni-app + Vue3 + TypeScript | 自提点、团长、供应商工作台 |
| 仓配移动端 H5 | `warehouse-h5` | `5177` | uni-app + Vue3 + TypeScript | 收货、拣货、装车、配送、主管作业 |

后台类应用和 3 个 uni-app 应用都采用 Vue3/Vite 的 `src/` 源码目录。uni-app 的 `App.vue`、`main.ts`、`pages.json`、`manifest.json`、`uni.scss` 和 `pages/` 放在各自应用的 `src/` 目录下。

## 启动方式

当前机器需要先安装 Node.js 18+，再在 `web/` 目录执行：

```bash
npm install
npm run dev:admin
npm run dev:wms-admin
npm run dev:user-h5
npm run dev:role-workbench-h5
npm run dev:warehouse-h5
```

uni-app H5 的端口在各应用 `vite.config.ts` 中配置，启动脚本保持 DCloud CLI 模板的 `uni` / `uni build` 形式。

## 静态检查

当前机器没有 Node.js 时，可以先执行：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\check-frontend.ps1
```

暂缓入口检查可以执行：

```powershell
rg -n "优惠券|积分|客服中心|客服工单|IM|机器人|自定义 BI|拖拽报表|实时大屏|复杂审批|会签|加签|多级分销|电子签章|MFA|SSO" . --glob "!README.md"
```

## API 地址

前端不写死后端地址，统一读取环境变量：

| 应用 | 变量 |
| --- | --- |
| 后台类应用 | `VITE_API_BASE_URL` |
| uni-app H5 | `VITE_API_BASE_URL` |

默认值为 `/api`，通过网关转发到后端服务。
每个应用目录都提供 `.env.example`，需要直连网关时复制为 `.env.local` 并修改 `VITE_API_BASE_URL`。

## 第一版边界

当前不展示优惠券、积分、客服中心、自定义 BI、复杂审批、多级分销等暂缓功能入口。
