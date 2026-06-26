<template>
  <view class="page">
    <view v-for="item in products" :key="item.publishSkuId" class="section favorite-row" @click="openDetail(item.productId)">
      <view>
        <text class="title">{{ item.productName }}</text>
        <text class="subtle">{{ item.skuName }} · 库存 {{ item.availableQty }}</text>
      </view>
      <text class="subtle">查看</text>
    </view>
    <view v-if="products.length === 0" class="section">
      <text class="subtle">暂无商品</text>
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
.favorite-row {
  display: flex;
  justify-content: space-between;
}
</style>
