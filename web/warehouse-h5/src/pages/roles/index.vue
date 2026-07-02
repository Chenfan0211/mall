<template>
  <view class="page phone-shell roles-page">
    <view class="warehouse-work-head roles-hero">
      <view class="status-row">
        <button class="back-btn" @click="goBack">‹</button>
        <text>切换岗位</text>
      </view>
      <view class="warehouse-work-title">
        <view>
          <text class="eyebrow">WMS MOBILE</text>
          <text class="title">选择岗位</text>
        </view>
        <text class="role-badge">{{ activeRole.name }}</text>
      </view>
    </view>

    <view class="roles-current section">
      <view class="role-icon-lg">
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <template v-if="activeRole.key === 'receiver'">
            <path d="M21 8.5 12 3 3 8.5l9 5.5 9-5.5Z" />
            <path d="M3 8.5v7L12 21l9-5.5v-7" />
            <path d="M8 12.5V17" />
          </template>
          <template v-else-if="activeRole.key === 'buyer'">
            <path d="M6 4h12v16H6z" />
            <path d="M9 8h6" />
            <path d="M9 12h6" />
            <path d="M9 16h3" />
            <path d="m16 15 1.5 1.5L21 13" />
          </template>
          <template v-else-if="activeRole.key === 'picker'">
            <path d="M9 11V6a3 3 0 0 1 6 0v5" />
            <path d="M5 10h14l-1.2 10H6.2L5 10Z" />
            <path d="M9 15h6" />
          </template>
          <template v-else-if="activeRole.key === 'recheck'">
            <path d="M5 7h10v10H5z" />
            <path d="M15 10h3l2 3v4h-5" />
            <path d="M7 19a2 2 0 1 0 0-4 2 2 0 0 0 0 4Z" />
            <path d="M17 19a2 2 0 1 0 0-4 2 2 0 0 0 0 4Z" />
            <path d="m7.5 11.5 1.2 1.2 2.3-2.5" />
          </template>
          <template v-else-if="activeRole.key === 'driver'">
            <path d="M5 16h14l-1.3-5.2A3 3 0 0 0 14.8 8H9.2a3 3 0 0 0-2.9 2.8L5 16Z" />
            <path d="M7 16v2" />
            <path d="M17 16v2" />
            <path d="M8 12h8" />
            <circle cx="8" cy="18" r="1.8" />
            <circle cx="16" cy="18" r="1.8" />
          </template>
          <template v-else>
            <path d="M4 6h16" />
            <path d="M6 6v14h12V6" />
            <path d="M9 10h6" />
            <path d="M9 14h6" />
            <path d="M10 3h4l1 3H9l1-3Z" />
          </template>
        </svg>
      </view>
      <view>
        <view class="role-home-row">
          <text class="section-title">{{ activeRole.name }}</text>
          <text class="status-pill green">当前岗位</text>
        </view>
        <text class="section-sub">{{ activeRole.entity }}</text>
        <text class="section-sub">账号：{{ activeRole.account }} · 仓库：{{ activeRole.warehouse }}</text>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="section-title">选择岗位</text>
        </view>
      </view>
      <view class="role-matrix-list">
        <button
          v-for="role in roles"
          :key="role.key"
          class="role-matrix-card"
          :class="{ active: currentRole === role.key }"
          @click="chooseRole(role.key)"
        >
          <view class="role-matrix-head">
            <view class="role-icon-sm">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <template v-if="role.key === 'receiver'">
                  <path d="M21 8.5 12 3 3 8.5l9 5.5 9-5.5Z" />
                  <path d="M3 8.5v7L12 21l9-5.5v-7" />
                  <path d="M8 12.5V17" />
                </template>
                <template v-else-if="role.key === 'buyer'">
                  <path d="M6 4h12v16H6z" />
                  <path d="M9 8h6" />
                  <path d="M9 12h6" />
                  <path d="M9 16h3" />
                  <path d="m16 15 1.5 1.5L21 13" />
                </template>
                <template v-else-if="role.key === 'picker'">
                  <path d="M9 11V6a3 3 0 0 1 6 0v5" />
                  <path d="M5 10h14l-1.2 10H6.2L5 10Z" />
                  <path d="M9 15h6" />
                </template>
                <template v-else-if="role.key === 'recheck'">
                  <path d="M5 7h10v10H5z" />
                  <path d="M15 10h3l2 3v4h-5" />
                  <path d="M7 19a2 2 0 1 0 0-4 2 2 0 0 0 0 4Z" />
                  <path d="M17 19a2 2 0 1 0 0-4 2 2 0 0 0 0 4Z" />
                  <path d="m7.5 11.5 1.2 1.2 2.3-2.5" />
                </template>
                <template v-else-if="role.key === 'driver'">
                  <path d="M5 16h14l-1.3-5.2A3 3 0 0 0 14.8 8H9.2a3 3 0 0 0-2.9 2.8L5 16Z" />
                  <path d="M7 16v2" />
                  <path d="M17 16v2" />
                  <path d="M8 12h8" />
                  <circle cx="8" cy="18" r="1.8" />
                  <circle cx="16" cy="18" r="1.8" />
                </template>
                <template v-else>
                  <path d="M4 6h16" />
                  <path d="M6 6v14h12V6" />
                  <path d="M9 10h6" />
                  <path d="M9 14h6" />
                  <path d="M10 3h4l1 3H9l1-3Z" />
                </template>
              </svg>
            </view>
            <view>
              <text class="role-title">{{ role.name }}</text>
              <text class="role-tip">{{ role.entity }}</text>
            </view>
            <text class="status-pill" :class="currentRole === role.key ? 'green' : 'gray'">{{ currentRole === role.key ? '当前' : '切换' }}</text>
          </view>
        </button>
      </view>
    </view>

    <WarehouseBottomNav active="home" :role="currentRole" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import WarehouseBottomNav from '@/components/WarehouseBottomNav.vue';
import { getCurrentRole, getRoleProfile, roleProfiles, setCurrentRole, type WarehouseRole } from '@/stores/warehouse';

const roles = roleProfiles;
const currentRole = ref<WarehouseRole>(getCurrentRole());
const activeRole = computed(() => getRoleProfile(currentRole.value));

function chooseRole(role: WarehouseRole) {
    setCurrentRole(role);
    currentRole.value = role;
    uni.showToast({ title: `已切换为${getRoleProfile(role).name}`, icon: 'none' });
    uni.switchTab({ url: '/pages/workbench/index' });
}

function goBack() {
    const pages = getCurrentPages();
    if (pages.length > 1) {
        uni.navigateBack();
        return;
    }
    uni.switchTab({ url: '/pages/workbench/index' });
}

onShow(() => {
    currentRole.value = getCurrentRole();
});
</script>
