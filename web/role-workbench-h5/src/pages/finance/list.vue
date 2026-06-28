<template>
  <view class="page">
    <view class="role-header">
      <view class="status-bar">
        <text>14:49</text>
        <text>财务明细</text>
      </view>
      <text class="subtle">{{ profile.entity }}</text>
      <text class="title">{{ roleType === 'supplier' ? '货款结算' : '佣金结算' }}</text>
      <text class="subtle">待结算、已结算、售后扣款、提现记录和流水明细。</text>
    </view>

    <view class="tabs">
      <button v-for="item in tabs" :key="item.value" :class="{ active: tab === item.value }" @click="tab = item.value">
        {{ item.label }}
      </button>
    </view>

    <view class="card-list">
      <view v-for="item in rows" :key="item.no" class="finance-card">
        <view class="card-head">
          <view class="card-main">
            <text class="card-title">{{ item.no }}</text>
            <text class="card-desc">{{ item.desc }}</text>
          </view>
          <text class="status-pill" :class="item.statusClass">{{ item.status }}</text>
        </view>
        <text class="money amount-line">{{ item.amount }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { currentProfile, currentRole, statusClass } from '@/stores/role';

const profile = computed(() => currentProfile.value);
const roleType = computed(() => currentRole.value);
const tab = ref('pending');
const tabs = [
    { value: 'pending', label: '待结算' },
    { value: 'paid', label: '已结算' },
    { value: 'afterSale', label: '售后扣款' },
    { value: 'withdraw', label: '提现' },
    { value: 'detail', label: '流水' }
];

onLoad((query) => {
    tab.value = String(query?.tab || 'pending');
});

const rows = computed(() => {
    if (tab.value === 'withdraw') {
        return profile.value.withdrawRequests.map((item) => ({
            no: item.no,
            desc: `${item.applyTime} · ${item.account}`,
            status: item.status,
            statusClass: item.statusClass,
            amount: item.amount
        }));
    }
    const label = tabs.find((item) => item.value === tab.value)?.label || '流水';
    return profile.value.orders.map((order, index) => ({
        no: `${roleType.value === 'supplier' ? 'SET' : 'COM'}-${order.no}`,
        desc: `${order.time} · ${order.station}`,
        status: label,
        statusClass: statusClass(label),
        amount: `¥${(Number(order.amount.replace(/,/g, '')) * (tab.value === 'afterSale' ? 0.03 : 0.1)).toFixed(2)}`
    })).slice(0, 4);
});
</script>

<style lang="scss" scoped>
.tabs {
  overflow-x: auto;
}

.tabs button {
  min-width: 132rpx;
}

.amount-line {
  display: block;
  margin-top: 16rpx;
  font-size: 30rpx;
}
</style>
