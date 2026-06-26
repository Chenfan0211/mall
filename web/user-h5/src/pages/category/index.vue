<template>
  <view class="page category-page">
    <view class="category-menu">
      <view
        v-for="item in categories"
        :key="item.id"
        class="category-item"
        :class="{ active: activeCategoryId === item.id }"
        @click="selectCategory(item.id)"
      >
        {{ item.categoryName }}
      </view>
    </view>
    <view class="category-list">
      <view v-for="item in products" :key="item.publishSkuId" class="section product-row" @click="openDetail(item.productId)">
        <text class="title">{{ item.productName }}</text>
        <text class="subtle">{{ item.skuName }} · 库存 {{ item.availableQty }}</text>
      </view>
      <view v-if="products.length === 0" class="section">
        <text class="subtle">暂无商品</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageCategories, pageProducts, type UserCategoryDTO, type UserProductCardDTO } from '@/api/user';

const categories = ref<UserCategoryDTO[]>([]);
const products = ref<UserProductCardDTO[]>([]);
const activeCategoryId = ref<number | undefined>();

async function loadCategories() {
    const page = await pageCategories({ pageNum: 1, pageSize: 20, status: 1 });
    categories.value = page.list || [];
    activeCategoryId.value = categories.value[0]?.id;
}

async function loadProducts() {
    const page = await pageProducts({
        pageNum: 1,
        pageSize: 20,
        categoryId: activeCategoryId.value,
        cityId: 440100,
        stationId: 720001
    });
    products.value = page.list || [];
}

async function selectCategory(id: number) {
    activeCategoryId.value = id;
    await loadProducts();
}

function openDetail(productId: number) {
    uni.navigateTo({ url: `/pages/product/detail?id=${productId}` });
}

onMounted(async () => {
    await loadCategories();
    await loadProducts();
});
</script>

<style lang="scss" scoped>
.category-page {
  display: grid;
  grid-template-columns: 180rpx 1fr;
  gap: 20rpx;
}

.category-menu {
  overflow: hidden;
  background: #ffffff;
  border-radius: 24rpx;
}

.category-item {
  padding: 24rpx 18rpx;
  border-bottom: 1rpx solid #f0dfd6;
  font-size: 26rpx;
}

.category-item.active {
  color: #d94b34;
  font-weight: 700;
  background: #fff2e9;
}

.product-row {
  margin-bottom: 16rpx;
}
</style>
