<template>
  <view class="user-tab-bar shop-bottom">
    <button
      v-for="item in tabs"
      :key="item.key"
      class="tab-button"
      :class="{ active: item.key === active }"
      @click="go(item.key)"
    >
      <view class="tab-icon">
        <svg v-if="item.key === 'home'" viewBox="0 0 24 24" aria-hidden="true">
          <path d="M3 10.8 12 3l9 7.8"></path>
          <path d="M5.5 9.8V21h5.3v-6.2h4.4V21h5.3V9.8"></path>
        </svg>
        <svg v-else-if="item.key === 'category'" viewBox="0 0 24 24" aria-hidden="true">
          <rect x="4" y="4" width="6.4" height="6.4" rx="1.6"></rect>
          <rect x="13.6" y="4" width="6.4" height="6.4" rx="1.6"></rect>
          <rect x="4" y="13.6" width="6.4" height="6.4" rx="1.6"></rect>
          <rect x="13.6" y="13.6" width="6.4" height="6.4" rx="1.6"></rect>
        </svg>
        <svg v-else-if="item.key === 'cart'" viewBox="0 0 24 24" aria-hidden="true">
          <circle cx="9" cy="20" r="1.5"></circle>
          <circle cx="18" cy="20" r="1.5"></circle>
          <path d="M3.5 4.5h2.4L8 15.7h9.7l2.2-7.8H7.2"></path>
          <path d="M8.4 11.4h10.2"></path>
        </svg>
        <svg v-else viewBox="0 0 24 24" aria-hidden="true">
          <circle cx="12" cy="8" r="3.5"></circle>
          <path d="M5.2 20.5a6.8 6.8 0 0 1 13.6 0"></path>
        </svg>
      </view>
      <text class="tab-label">{{ item.label }}</text>
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
