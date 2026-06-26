<template>
  <view class="page">
    <view class="section">
      <text class="title">结算确认</text>
      <view class="row">
        <text>自提点</text>
        <text>{{ stationId }}</text>
      </view>
      <view class="row">
        <text>提货人</text>
        <text>测试用户正常</text>
      </view>
      <view v-for="item in cartItems" :key="item.id" class="row">
        <text>{{ item.productName }} × {{ item.qty }}</text>
        <text class="subtle">¥{{ item.salePrice }}</text>
      </view>
      <view class="row">
        <text>合计</text>
        <text class="price">¥{{ totalAmount }}</text>
      </view>
    </view>
    <button class="primary" @click="pay">提交订单</button>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { pageCartItems, submitOrder, type UserCartItemDTO } from '@/api/user';

const userId = 900001;
const cityId = 440100;
const stationId = 720001;
const cartItems = ref<UserCartItemDTO[]>([]);
const totalAmount = computed(() =>
    cartItems.value
        .reduce((sum, item) => sum + Number(item.salePrice || 0) * Number(item.qty || 0), 0)
        .toFixed(2)
);

async function loadCartItems() {
    const page = await pageCartItems({ pageNum: 1, pageSize: 50, userId, stationId, selectedFlag: 1 });
    cartItems.value = page.list || [];
}

async function pay() {
    const order = await submitOrder({
        userId,
        cityId,
        stationId,
        cartItemIds: cartItems.value.map((item) => item.id),
        pickupName: '测试用户正常',
        pickupMobile: '13800000000',
        idempotentKey: `checkout-${Date.now()}`
    });
    uni.navigateTo({ url: `/pages/payment/result?orderId=${order.id}` });
}

onMounted(() => {
    void loadCartItems();
});
</script>

<style lang="scss" scoped>
.row {
  display: flex;
  justify-content: space-between;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0dfd6;
}

.price {
  color: #d94b34;
  font-weight: 700;
}

.primary {
  color: #ffffff;
  background: #d94b34;
  border-radius: 20rpx;
}
</style>
