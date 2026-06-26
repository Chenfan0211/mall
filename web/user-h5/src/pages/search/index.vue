<template>
  <view class="page">
    <view class="section">
      <input v-model="keyword" class="search-input" placeholder="搜索当前自提点可买商品" confirm-type="search" @confirm="loadResults" />
      <view class="tag-row">
        <text v-for="item in hotWords" :key="item" class="pill" @click="selectHotWord(item)">{{ item }}</text>
      </view>
    </view>
    <view v-for="item in results" :key="item.publishSkuId" class="section result-row" @click="openDetail(item.productId)">
      <view>
        <text class="title">{{ item.productName }}</text>
        <text class="subtle">{{ item.skuName }} · 库存 {{ item.availableQty }}</text>
      </view>
      <text class="price">¥{{ item.salePrice }}</text>
    </view>
    <view v-if="results.length === 0" class="section">
      <text class="subtle">暂无搜索结果</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageProducts, type UserProductCardDTO } from '@/api/user';

const keyword = ref('');
const hotWords = ['葡萄', '鸡蛋', '牛奶'];
const results = ref<UserProductCardDTO[]>([]);

async function loadResults() {
    const page = await pageProducts({
        pageNum: 1,
        pageSize: 20,
        keyword: keyword.value,
        cityId: 440100,
        stationId: 720001
    });
    results.value = page.list || [];
}

function selectHotWord(value: string) {
    keyword.value = value;
    void loadResults();
}

function openDetail(productId: number) {
    uni.navigateTo({ url: `/pages/product/detail?id=${productId}` });
}

onMounted(() => {
    void loadResults();
});
</script>

<style lang="scss" scoped>
.search-input {
  height: 88rpx;
  padding: 0 24rpx;
  background: #fffaf6;
  border: 1rpx solid #f0dfd6;
  border-radius: 20rpx;
}

.tag-row {
  display: flex;
  gap: 12rpx;
  margin-top: 20rpx;
}

.result-row {
  display: flex;
  justify-content: space-between;
}

.price {
  color: #d94b34;
  font-weight: 700;
}
</style>
