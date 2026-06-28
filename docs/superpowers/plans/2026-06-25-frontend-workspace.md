# Frontend Workspace Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Create the first runnable frontend workspace for the mall project, covering operation admin, WMS admin, user H5, role workbench H5, and warehouse H5.

**Architecture:** Use one `web/` workspace with five independent applications. Admin applications use Vue 3 + Vite + TypeScript + Element Plus. Mobile applications use uni-app + Vue 3 + TypeScript with CLI-style `src/pages.json`, `src/manifest.json`, `src/App.vue`, `src/main.ts`, `src/uni.scss`, `src/pages/`, request wrappers, and mock-ready data.

**Tech Stack:** Vue 3, Vite, TypeScript, Element Plus, Pinia, Vue Router, uni-app, SCSS.

---

### Task 1: Workspace Shell

**Files:**
- Create: `web/README.md`
- Create: `web/package.json`
- Create: `web/tsconfig.base.json`
- Create: `web/.gitignore`

- [x] Add one workspace-level package manifest with scripts for five apps.
- [x] Add a shared TypeScript base config.
- [x] Add README startup notes and port mapping.
- [x] Add frontend-only ignored build artifacts.

### Task 2: Admin Applications

**Files:**
- Create: `web/admin/**`
- Create: `web/wms-admin/**`

- [x] Build Vite entry files for both admin applications.
- [x] Add login page, dashboard shell, router, store, request wrapper, and typed API modules.
- [x] Add operation admin eight first-level menus exactly as documented.
- [x] Add WMS admin menus for base, inbound, inventory, outbound, delivery, return, stock flow, logs, and supervisor work.

### Task 3: Mobile uni-app Applications

**Files:**
- Create: `web/user-h5/**`
- Create: `web/role-workbench-h5/**`
- Create: `web/warehouse-h5/**`

- [x] Build `manifest.json`, `pages.json`, `App.vue`, `main.ts`, `uni.scss`, and app pages for each uni-app application.
- [x] Add login or first-entry pages where required.
- [x] Add tab pages and status lists matching the documented page coverage.
- [x] Add request wrappers with token handling and consistent error messages.

### Task 4: Verification

**Files:**
- Inspect: all new `web/**` source files

- [ ] Run package installation and build after Node.js is available on PATH.
- [x] Verify required frontend entry files exist for all five applications.
- [x] Verify no coupon, points, customer-service, or complex BI entries are exposed.
