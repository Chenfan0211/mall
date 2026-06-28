<template>
  <view class="page no-tab message-page">
    <view class="section">
      <view class="message-head">
        <view>
          <text class="title">站内消息</text>
          <text class="subtle">{{ unreadCount }} 条未读</text>
        </view>
        <button class="plain small" @click="readAll">全部已读</button>
      </view>
      <view class="chip-row">
        <text v-for="item in categories" :key="item" class="pill" :class="{ active: activeCategory === item }" @click="activeCategory = item">
          {{ item }}
        </text>
      </view>
    </view>

    <view v-for="item in filteredMessages" :key="item.id" class="section message-row" :class="{ unread: !item.read }" @click="openMessage(item)">
      <view>
        <text>{{ item.title }}</text>
        <text>{{ item.content }}</text>
        <text>{{ item.category }} · {{ item.createTime }}</text>
      </view>
      <text class="pill">{{ item.read ? '已读' : '未读' }}</text>
    </view>

    <EmptyActionCard
      v-if="filteredMessages.length === 0"
      title="暂无消息"
      sub="订单、提货、售后消息会展示在这里。"
      icon="信"
      button-text="返回首页"
      @action="goHome"
    />
    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';

import EmptyActionCard from '@/components/EmptyActionCard.vue';
import UserToast from '@/components/UserToast.vue';
import { markAllMessagesRead, markMessageRead, navigateUser, showUserToast, useUserState, type LocalMessage } from '@/stores/userState';

const state = useUserState();
const activeCategory = ref('全部');
const categories = ['全部', '订单', '提货', '售后'];
const filteredMessages = computed(() =>
    activeCategory.value === '全部'
        ? state.localMessages
        : state.localMessages.filter((item) => item.category === activeCategory.value)
);
const unreadCount = computed(() => filteredMessages.value.filter((item) => !item.read).length);

function openMessage(item: LocalMessage) {
    markMessageRead(item.id);
    if (item.bizUrl) {
        navigateUser(item.bizUrl);
        return;
    }
    showUserToast('消息已标记为已读');
}

function readAll() {
    markAllMessagesRead(activeCategory.value);
    showUserToast('消息已全部标记为已读');
}

function goHome() {
    uni.switchTab({ url: '/pages/home/index' });
}
</script>

<style lang="scss" scoped>
.message-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  margin-bottom: 18rpx;
}

.message-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18rpx;
}

.message-row.unread {
  border-color: #e85d3f;
  background: #fff7f1;
}

.message-row view {
  min-width: 0;
}

.message-row view text {
  display: block;
}

.message-row view text:first-child {
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
}

.message-row view text:not(:first-child) {
  margin-top: 6rpx;
  color: #8c6a58;
  font-size: 23rpx;
  line-height: 1.45;
}
</style>
