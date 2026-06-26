<template>
  <view class="page">
    <view class="section">
      <text class="title">提货订单</text>
      <text class="subtle">只展示当前自提点订单</text>
    </view>
    <view v-for="item in orders" :key="item.id" class="section">
      <text class="title">{{ item.orderNo }}</text>
      <text class="subtle">{{ item.pickupName || '用户' }} · {{ item.fulfillStatus }}</text>
      <button class="plain" @click="openDetail">查看详情</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageStationOrders, type StationOrderDTO } from '@/api/station';

const orders = ref<StationOrderDTO[]>([]);

async function loadData() {
    const page = await pageStationOrders({ pageNum: 1, pageSize: 20, stationId: 720001 });
    orders.value = page.list;
}

function openDetail() {
    uni.navigateTo({ url: '/pages/messages/index' });
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.plain {
  margin-top: 20rpx;
  color: #d94b34;
  background: #fff2e9;
  border-radius: 20rpx;
}
</style>
