<template>
  <view class="page">
    <view class="role-header">
      <view class="status-bar">
        <text>14:49</text>
        <text>到货详情</text>
      </view>
      <text class="subtle">{{ profile.entity }}</text>
      <text class="title">{{ goods.title }}</text>
      <text class="subtle">{{ goods.spec }} · 影响 {{ goods.users.length }} 位用户</text>
    </view>

    <view class="section">
      <view class="summary-box">
        <view>
          <text>{{ goods.expected }}</text>
          <text>应到</text>
        </view>
        <view>
          <text>{{ goods.actual }}</text>
          <text>实到</text>
        </view>
        <view>
          <text>{{ Math.max(0, goods.expected - goods.actual) }}</text>
          <text>缺货</text>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">用户明细</text>
          <text class="subtle">到货通知会影响以下提货用户</text>
        </view>
      </view>
      <view v-for="user in goods.users" :key="user.orderNo" class="detail-row">
        <text>{{ user.name }}</text>
        <text>{{ user.mobile }} · {{ user.qty }} 件 · {{ user.orderNo }}</text>
      </view>
    </view>

    <view class="fixed-bottom">
      <button class="primary" @click="confirmArrival">确认到货</button>
      <button class="plain" @click="goNotify">发送通知</button>
      <button class="danger" @click="goShortage">缺货标记</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { confirmAction, currentProfile, showRoleToast } from '@/stores/role';

const id = ref('');
const profile = computed(() => currentProfile.value);
const emptyDelivery = {
    date: '',
    label: '暂无配送日',
    goods: [
        {
            id: 'empty',
            title: '暂无到货商品',
            spec: '当前身份没有可查看的到货记录',
            qty: 0,
            price: '0.00',
            expected: 0,
            actual: 0,
            users: []
        }
    ],
    returns: []
};
const delivery = computed(() => profile.value.deliveryRecords[0] || emptyDelivery);
const goods = computed(() => delivery.value.goods.find((item) => item.id === id.value) || delivery.value.goods[0]);

onLoad((query) => {
    id.value = String(query?.id || '');
});

async function confirmArrival() {
    const ok = await confirmAction(`确认 ${goods.value.title} 已到货？`, '确认到货');
    if (ok) {
        showRoleToast('到货确认接口暂未接通，已保留交互');
    }
}

function goNotify() {
    uni.navigateTo({ url: '/pages/store/notify-preview' });
}

function goShortage() {
    uni.navigateTo({ url: `/pages/store/shortage?id=${goods.value.id}` });
}
</script>

<style lang="scss" scoped>
.summary-box {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12rpx;
}

.summary-box view {
  padding: 18rpx;
  background: #fff7f1;
  border: 1rpx solid #f0dfd6;
  border-radius: 20rpx;
  text-align: center;
}

.summary-box text:first-child {
  display: block;
  color: #d94b34;
  font-size: 34rpx;
  font-weight: 900;
}

.summary-box text:last-child {
  color: #8f6c58;
  font-size: 22rpx;
}

.fixed-bottom button {
  flex: 1;
  min-height: 70rpx;
  font-size: 23rpx;
}
</style>
