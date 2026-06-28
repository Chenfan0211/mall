<template>
  <view class="page">
    <view class="role-header">
      <view class="status-bar">
        <text>14:49</text>
        <text>服务工具</text>
      </view>
      <text class="subtle">{{ profile.entity }}</text>
      <text class="title">{{ roleType === 'supplier' ? '供应商服务' : '自提点服务' }}</text>
      <text class="subtle">入口按当前角色展示，不跨身份打开。</text>
    </view>

    <view class="quick-grid">
      <button v-for="item in profile.services" :key="item.title" class="quick-button" @click="open(item.page)">
        <text class="task-title">{{ item.title }}</text>
        <text class="task-desc">{{ item.desc }}</text>
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { currentProfile, currentRole, goPage } from '@/stores/role';

const profile = computed(() => currentProfile.value);
const roleType = computed(() => currentRole.value);

function open(url: string) {
    goPage(url);
}
</script>
