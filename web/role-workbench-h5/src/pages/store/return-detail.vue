<template>
  <view class="page">
    <view class="role-header">
      <view class="status-bar">
        <text>14:49</text>
        <text>退货交接</text>
      </view>
      <text class="subtle">{{ profile.entity }}</text>
      <text class="title">{{ item.title }}</text>
      <text class="subtle">{{ item.spec }} · {{ item.status }}</text>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">交接商品</text>
          <text class="subtle">司机取回后继续跟踪返仓结果</text>
        </view>
        <text class="status-pill orange">{{ item.status }}</text>
      </view>
      <view class="detail-row">
        <text>退货数量</text>
        <text>{{ item.qty }} 件</text>
      </view>
      <view class="detail-row">
        <text>处理说明</text>
        <text>{{ item.spec }}</text>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">用户明细</text>
          <text class="subtle">按售后单 / 订单商品维度核对</text>
        </view>
      </view>
      <view v-for="user in item.users" :key="user.orderNo" class="detail-row">
        <text>{{ user.name }}</text>
        <text>{{ user.mobile }} · {{ user.qty }} 件 · {{ user.orderNo }}</text>
      </view>
    </view>

    <view class="fixed-bottom">
      <button class="primary" @click="handover">确认司机取回</button>
      <button class="plain" @click="showRoleToast('已复制退货交接信息')">复制信息</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { confirmAction, currentProfile, showRoleToast } from '@/stores/role';

const id = ref('');
const profile = computed(() => currentProfile.value);
const emptyReturn = {
    id: 'empty',
    title: '暂无退货商品',
    spec: '当前身份没有可查看的退货交接记录',
    qty: 0,
    price: '0.00',
    status: '暂无记录',
    users: []
};
const delivery = computed(() => profile.value.deliveryRecords[0] || { label: '暂无配送日', goods: [], returns: [emptyReturn] });
const item = computed(() => delivery.value.returns.find((row) => row.id === id.value) || delivery.value.returns[0] || emptyReturn);

onLoad((query) => {
    id.value = String(query?.id || '');
});

async function handover() {
    const ok = await confirmAction(`确认 ${item.value.title} 已由司机取回？`, '确认退货交接');
    if (ok) {
        showRoleToast('退货交接接口暂未接通，已保留交互');
    }
}
</script>

<style lang="scss" scoped>
.fixed-bottom button {
  flex: 1;
  min-height: 70rpx;
  font-size: 24rpx;
}
</style>
