<template>
  <view class="service-page shop-page" data-m-page="messages">
    <view class="service-head dark">
      <view class="order-nav">
        <button class="order-back" @click="goMine">‹</button>
        <b>站内消息</b>
        <span>{{ unreadCount }}条未读</span>
      </view>
    </view>

    <view class="message-list">
      <view class="message-category-strip">
        <button
          v-for="item in categories"
          :key="item"
          :class="{ active: activeCategory === item }"
          @click="activeCategory = item"
        >
          {{ item }}
        </button>
      </view>

      <button class="message-read-all" @click="readAll">全部已读</button>

      <view
        v-for="item in filteredMessages"
        :key="item.id"
        class="message-item"
        :class="{ unread: !item.read }"
        @click="openMessage(item)"
      >
        <view class="message-row-top">
          <em>{{ item.category }}</em>
          <span>{{ item.createTime }}</span>
        </view>
        <b>{{ item.title }}</b>
        <span>{{ item.content }}</span>
        <view class="message-actions-row">
          <span>{{ item.bizUrl ? '查看详情' : '仅记录通知' }}</span>
          <button @click.stop="markRead(item)">{{ item.read ? '已读' : '标记已读' }}</button>
        </view>
      </view>

      <EmptyActionCard
        v-if="filteredMessages.length === 0"
        title="暂无该类消息"
        sub="切换其他分类查看订单、售后或系统消息。"
        icon="信"
        button-text="去首页逛逛"
        @action="goHome"
      />
    </view>
    <UserToast />
    <UserTabBar active="mine" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';

import EmptyActionCard from '@/components/EmptyActionCard.vue';
import UserTabBar from '@/components/UserTabBar.vue';
import UserToast from '@/components/UserToast.vue';
import { markAllMessagesRead, markMessageRead, navigateUser, showUserToast, useUserState, type LocalMessage } from '@/stores/userState';

const state = useUserState();
const activeCategory = ref('全部');
const categories = ['全部', '订单', '售后', '系统'];
const filteredMessages = computed(() =>
    activeCategory.value === '全部'
        ? state.localMessages
        : state.localMessages.filter((item) => displayCategory(item) === activeCategory.value)
);
const unreadCount = computed(() => filteredMessages.value.filter((item) => !item.read).length);

function displayCategory(item: LocalMessage) {
    return item.category === '提货' ? '订单' : item.category;
}

function openMessage(item: LocalMessage) {
    markMessageRead(item.id);
    if (item.bizUrl) {
        navigateUser(item.bizUrl);
        return;
    }
    showUserToast('消息已标记为已读');
}

function markRead(item: LocalMessage) {
    markMessageRead(item.id);
    showUserToast(item.read ? '消息已读' : '消息已标记为已读');
}

function readAll() {
    if (activeCategory.value === '订单') {
        state.localMessages.forEach((item) => {
            if (displayCategory(item) === '订单') {
                item.read = true;
            }
        });
    } else {
        markAllMessagesRead(activeCategory.value);
    }
    showUserToast('消息已全部标记为已读');
}

function goMine() {
    navigateUser('/pages/mine/index', true);
}

function goHome() {
    navigateUser('/pages/home/index', true);
}
</script>
