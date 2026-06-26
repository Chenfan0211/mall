<template>
  <view class="page">
    <view class="section">
      <text class="title">购物车</text>
      <view v-for="item in cartItems" :key="item.id" class="cart-row">
        <view>
          <text class="name">{{ item.productName }}</text>
          <text class="subtle">{{ item.validStatus === 1 ? '正常' : '失效' }}</text>
        </view>
        <text>x{{ item.qty }}</text>
      </view>
    </view>
    <button class="primary" @click="checkout">去结算</button>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageCartItems, type UserCartItemDTO } from '@/api/user';

const cartItems = ref<UserCartItemDTO[]>([]);

async function loadData() {
    const page = await pageCartItems({
        pageNum: 1,
        pageSize: 20,
        userId: 740001,
        cityId: 440100,
        stationId: 720001
    });
    cartItems.value = page.list;
}

function checkout() {
    uni.navigateTo({ url: '/pages/checkout/index' });
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.cart-row {
  display: flex;
  justify-content: space-between;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f0dfd6;
}

.name {
  display: block;
  font-weight: 700;
}

.primary {
  color: #ffffff;
  background: #d94b34;
  border-radius: 20rpx;
}
</style>
