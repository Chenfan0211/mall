<template>
  <view class="page">
    <view class="role-header">
      <view class="status-bar">
        <text>14:49</text>
        <text>采购到仓</text>
      </view>
      <text class="subtle">{{ profile.entity }}</text>
      <text class="title">采购单与到仓凭证</text>
      <text class="subtle">供应商只看自身采购单，不展示采购、仓库、财务内部流转。</text>
    </view>

    <view class="tabs">
      <button :class="{ active: tab === 'all' }" @click="tab = 'all'">全部</button>
      <button :class="{ active: tab === 'pending' }" @click="tab = 'pending'">待提交</button>
      <button :class="{ active: tab === 'rejected' }" @click="tab = 'rejected'">待我补充</button>
    </view>

    <view class="card-list">
      <view v-for="item in rows" :key="item.no" class="record-card" @click="open(item.no)">
        <view class="card-head">
          <view class="card-main">
            <text class="card-title">{{ item.no }}</text>
            <text class="card-desc">{{ item.warehouse }} · {{ item.pickup }}</text>
          </view>
          <text class="status-pill" :class="item.statusClass">{{ item.status }}</text>
        </view>
        <view class="purchase-foot">
          <text>{{ item.totalQty }} 件</text>
          <text class="money">¥{{ item.amount.toFixed(2) }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { currentProfile } from '@/stores/role';

const profile = computed(() => currentProfile.value);
const tab = ref<'all' | 'pending' | 'rejected'>('all');
const rows = computed(() => {
    if (tab.value === 'pending') {
        return profile.value.purchases.filter((item) => /待提交|待确认/.test(item.status));
    }
    if (tab.value === 'rejected') {
        return profile.value.purchases.filter((item) => /补充|拒绝/.test(item.status));
    }
    return profile.value.purchases;
});

function open(no: string) {
    uni.navigateTo({ url: `/pages/store/supplier-purchase-detail?no=${encodeURIComponent(no)}` });
}
</script>

<style lang="scss" scoped>
.purchase-foot {
  display: flex;
  justify-content: space-between;
  margin-top: 18rpx;
  color: #8f6c58;
  font-size: 24rpx;
}
</style>
