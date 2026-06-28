<template>
  <view class="user-tab-bar">
    <button
      v-for="item in tabs"
      :key="item.key"
      class="tab-button"
      :class="{ active: item.key === active }"
      @click="go(item.key)"
    >
      <text class="tab-icon">{{ item.icon }}</text>
      <text>{{ item.label }}</text>
      <text v-if="item.key === 'cart' && cartBadgeCount > 0" class="tab-badge">{{ cartBadgeCount }}</text>
    </button>
  </view>
</template>

<script setup lang="ts">
import { USER_MAIN_TABS, cartBadgeCount, goUserTab } from '@/stores/userState';

defineProps<{
    active: string;
}>();

const tabs = USER_MAIN_TABS;

function go(key: string) {
    goUserTab(key);
}
</script>
