<template>
  <el-container class="admin-layout">
    <el-aside width="280px" class="admin-layout__aside">
      <div class="brand">
        <strong>社区团购商城</strong>
        <span>运营后台 / Admin Console</span>
      </div>
      <div class="nav-title">第一版后台菜单</div>
      <el-menu router :default-active="activePath" class="nav-menu">
        <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
          <small>{{ item.code }}</small>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="admin-layout__header">
        <div>
          <div class="header-title">{{ route.meta.title || '运营后台' }}</div>
          <div class="header-subtitle">按角色与数据范围展示当前可处理的数据</div>
        </div>
        <el-space>
          <el-tag effect="plain">平台管理员</el-tag>
          <el-button @click="logout">退出</el-button>
        </el-space>
      </el-header>
      <el-main class="admin-layout__main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import {
    DataAnalysis,
    Goods,
    List,
    Location,
    Money,
    Setting,
    ShoppingCart,
    Van
} from '@element-plus/icons-vue';
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useSessionStore } from '@/store/session';

const route = useRoute();
const router = useRouter();
const sessionStore = useSessionStore();

const menuItems = [
    { title: '运营驾驶舱', path: '/dashboard', icon: DataAnalysis, code: 'GMV' },
    { title: '商品与团期', path: '/product-sale', icon: Goods, code: 'P0' },
    { title: '交易履约', path: '/fulfillment', icon: ShoppingCart, code: 'Order' },
    { title: '自提点/团长', path: '/station-leader', icon: Location, code: 'Station' },
    { title: '供应商/采购', path: '/supplier-purchase', icon: Van, code: 'PO' },
    { title: '财务风控', path: '/finance-risk', icon: Money, code: 'Risk' },
    { title: '用户与营销', path: '/user-marketing', icon: List, code: 'User' },
    { title: '报表与系统', path: '/report-system', icon: Setting, code: 'Sys' }
];

const activePath = computed(() => route.path);

function logout() {
    sessionStore.clearSession();
    router.push('/login');
}
</script>

<style lang="scss" scoped>
.admin-layout {
  min-height: 100vh;
  background: var(--mall-bg);
}

.admin-layout__aside {
  position: sticky;
  top: 0;
  height: 100vh;
  overflow: auto;
  padding: 18px 14px;
  color: #ffffff;
  background: var(--mall-nav);
}

.brand {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 8px 10px 16px;
  margin-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
}

.brand strong {
  font-size: 20px;
  line-height: 1.35;
}

.brand span {
  color: #cbd5e1;
  font-size: 12px;
}

.nav-title {
  margin: 18px 10px 7px;
  color: #94a3b8;
  font-size: 12px;
}

.nav-menu {
  border-right: 0;
  background: transparent;
}

:deep(.el-menu-item) {
  height: 39px;
  margin-bottom: 4px;
  padding: 0 10px !important;
  color: #e5e7eb;
  border-radius: 6px;
  font-size: 13px;
}

:deep(.el-menu-item small) {
  margin-left: auto;
  color: #cbd5e1;
  font-size: 11px;
}

:deep(.el-menu-item.is-active) {
  color: #ffffff;
  background: var(--mall-primary);
}

:deep(.el-menu-item:hover) {
  color: #ffffff;
  background: var(--mall-primary);
}

.admin-layout__header {
  position: sticky;
  top: 0;
  z-index: 5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  background: #ffffff;
  border-bottom: 1px solid var(--mall-border);
}

.header-title {
  font-size: 17px;
  font-weight: 700;
}

.header-subtitle {
  margin-top: 4px;
  color: var(--mall-muted);
  font-size: 12px;
}

.admin-layout__main {
  max-width: 1480px;
  padding: 22px;
}
</style>
