<template>
  <view class="page">
    <view class="section tabs">
      <text v-for="tab in tabs" :key="tab.value" class="pill">{{ tab.label }}</text>
    </view>
    <view v-for="item in orders" :key="item.id" class="section">
      <text class="title">{{ item.orderNo }}</text>
      <text class="subtle">交易 {{ item.tradeStatus }} 路 履约 {{ item.fulfillStatus }}</text>
      <button v-if="item.tradeStatus === 0" class="plain" @click="openPay(item.id)">继续支付</button>
      <button v-else-if="item.fulfillStatus === 3" class="plain" @click="openAftersale(item.id)">申请售后</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageOrders, type UserOrderDTO } from '@/api/user';

const tabs = [
    { label: '待付款', value: 0 },
    { label: '备货中', value: 1 },
    { label: '待自提', value: 2 },
    { label: '已完成', value: 3 },
    { label: '售后中', value: 4 }
];

const orders = ref<UserOrderDTO[]>([]);

async function loadData() {
    const page = await pageOrders({
        pageNum: 1,
        pageSize: 20,
        userId: 740001
    });
    orders.value = page.list;
}

function openPay(id: number) {
    uni.navigateTo({ url: `/pages/payment/result?id=${id}` });
}

function openAftersale(id: number) {
    uni.navigateTo({ url: `/pages/aftersale/index?id=${id}` });
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.tabs {
  display: flex;
  gap: 12rpx;
  overflow-x: auto;
}

.plain {
  margin-top: 20rpx;
  color: #d94b34;
  background: #fff2e9;
  border-radius: 20rpx;
}
</style>
