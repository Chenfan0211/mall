<template>
  <view class="page">
    <view class="section hero">
      <text class="title">周末鲜果专题</text>
      <text class="subtle">活动只影响展示，不改变价格库存校验。</text>
    </view>
    <view v-for="item in products" :key="item.publishSkuId" class="section product-row" @click="openDetail(item.productId)">
      <view>
        <text class="title">{{ item.productName }}</text>
        <text class="subtle">{{ item.skuName }} · 库存 {{ item.availableQty }}</text>
      </view>
      <text class="price">¥{{ item.salePrice }}</text>
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
.hero {
  color: #ffffff;
  background: linear-gradient(135deg, #d94b34 0%, #ef7a37 62%, #f7b36c 100%);
  border: 0;
  box-shadow: 0 16rpx 32rpx rgba(217, 75, 52, 0.22);
}

.hero .title,
.hero .subtle {
  color: #ffffff;
}

.product-row {
  display: flex;
  justify-content: space-between;
}

.price {
  color: #d94b34;
  font-weight: 700;
}
</style>
