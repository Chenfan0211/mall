<template>
  <view class="page">
    <view class="section">
      <text class="title">今天</text>
      <view v-for="item in products" :key="item.publishSkuId" class="history-row" @click="openDetail(item.productId)">
        <text>{{ item.productName }}</text>
        <text class="subtle">库存 {{ item.availableQty }}</text>
      </view>
      <text v-if="products.length === 0" class="subtle">暂无浏览记录</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageProducts, type UserProductCardDTO } from '@/api/user';

const products = ref<UserProductCardDTO[]>([]);

async function loadData() {
    const page = await pageProducts({ pageNum: 1, pageSize: 20, cityId: 440100, stationId: 720001 });
    products.value = page.list || [];
}

function openDetail(productId: number) {
    uni.navigateTo({ url: `/pages/product/detail?id=${productId}` });
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.history-row {
  display: flex;
  justify-content: space-between;
  padding: 22rpx 0;
  border-bottom: 1rpx solid #f0dfd6;
}
</style>
