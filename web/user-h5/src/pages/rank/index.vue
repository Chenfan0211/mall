<template>
  <view class="page">
    <view class="section">
      <text class="title">自提点热卖榜</text>
      <text class="subtle">排序不替代真实可售校验。</text>
    </view>
    <view v-for="(item, index) in products" :key="item.publishSkuId" class="section rank-row" @click="openDetail(item.productId)">
      <text class="rank">#{{ index + 1 }}</text>
      <view>
        <text class="title">{{ item.productName }}</text>
        <text class="subtle">已售 {{ item.soldQty }} · 库存 {{ item.availableQty }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageProducts, type UserProductCardDTO } from '@/api/user';

const products = ref<UserProductCardDTO[]>([]);

async function loadData() {
    const page = await pageProducts({ pageNum: 1, pageSize: 20, cityId: 440100, stationId: 720001 });
    products.value = (page.list || []).sort((left, right) => (right.soldQty || 0) - (left.soldQty || 0));
}

function openDetail(productId: number) {
    uni.navigateTo({ url: `/pages/product/detail?id=${productId}` });
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.rank-row {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.rank {
  color: #d94b34;
  font-size: 38rpx;
  font-weight: 700;
}
</style>
