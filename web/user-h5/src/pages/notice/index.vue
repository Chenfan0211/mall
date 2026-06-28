<template>
  <view class="page no-tab">
    <view class="section">
      <text class="title">商品通知</text>
      <text class="subtle">售罄商品到货后，会按当前自提点发送站内消息。</text>
    </view>

    <view v-for="productId in notices" :key="productId" class="section notice-row">
      <view>
        <text>商品 {{ productId }}</text>
        <text class="subtle">{{ state.station.name }} · 到货提醒已开启</text>
      </view>
      <button class="plain small" @click="cancelNotice(productId)">取消提醒</button>
    </view>

    <EmptyActionCard
      v-if="notices.length === 0"
      title="暂无商品通知"
      sub="遇到售罄商品时，可在商品详情页开启到货提醒。"
      icon="铃"
      button-text="去首页"
      @action="goHome"
    />
    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';

import EmptyActionCard from '@/components/EmptyActionCard.vue';
import UserToast from '@/components/UserToast.vue';
import { showUserToast, toggleNotice, useUserState } from '@/stores/userState';

const state = useUserState();
const notices = computed(() => Array.from(state.notices));

function cancelNotice(productId: number) {
    toggleNotice(productId);
    showUserToast('已取消到货提醒');
}

function goHome() {
    uni.switchTab({ url: '/pages/home/index' });
}
</script>

<style lang="scss" scoped>
.notice-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
}

.notice-row view {
  min-width: 0;
}

.notice-row view text:first-child {
  display: block;
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
}
</style>
