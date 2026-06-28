<template>
  <view class="page no-tab favorite-page">
    <view class="section">
      <view class="head-row">
        <view>
          <text class="title">我的收藏</text>
          <text class="subtle">失效商品会提示原因，可手动清理。</text>
        </view>
        <button class="plain small" @click="clearAll">清空</button>
      </view>
    </view>

    <ProductCard
      v-for="item in products"
      :key="item.publishSkuId"
      :product="item"
      :favorite="true"
      @open="openDetail(item.productId)"
      @favorite="removeFavorite(item.productId)"
      @notice="toggleNotice(item.productId)"
      @add="addFavoriteProduct(item)"
    />

    <EmptyActionCard
      v-if="products.length === 0"
      title="暂无收藏"
      sub="商品详情页点击收藏后会展示在这里。"
      icon="藏"
      button-text="去首页"
      @action="goHome"
    />
    <ConfirmDialog
      :visible="clearVisible"
      title="清空收藏"
      content="确认清空当前本地收藏列表？"
      confirm-text="清空"
      danger
      @cancel="clearVisible = false"
      @confirm="confirmClear"
    />
    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';

import ConfirmDialog from '@/components/ConfirmDialog.vue';
import EmptyActionCard from '@/components/EmptyActionCard.vue';
import ProductCard from '@/components/ProductCard.vue';
import UserToast from '@/components/UserToast.vue';
import { pageProducts, type UserProductCardDTO } from '@/api/user';
import { addProductToCart, navigateUser, showUserToast, toggleFavorite, toggleNotice, useUserState } from '@/stores/userState';

const state = useUserState();
const products = ref<UserProductCardDTO[]>([]);
const clearVisible = ref(false);

async function loadData() {
    try {
        const page = await pageProducts({ pageNum: 1, pageSize: 20, cityId: state.city.id, stationId: state.station.id });
        const rows = page.list || [];
        products.value = state.favorites.size > 0 ? rows.filter((item) => state.favorites.has(item.productId)) : rows.slice(0, 3);
    } catch (error) {
        showUserToast('收藏接口暂不可用，已展示本地收藏状态', 'warn');
        products.value = [];
    }
}

function openDetail(productId: number) {
    navigateUser(`/pages/product/detail?id=${productId}`);
}

function removeFavorite(productId: number) {
    toggleFavorite(productId);
    products.value = products.value.filter((item) => item.productId !== productId);
}

function addFavoriteProduct(item: UserProductCardDTO) {
    addProductToCart(item);
}

function clearAll() {
    clearVisible.value = true;
}

function confirmClear() {
    state.favorites.clear();
    products.value = [];
    clearVisible.value = false;
    showUserToast('收藏已清空');
}

function goHome() {
    uni.switchTab({ url: '/pages/home/index' });
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.favorite-page {
  padding-bottom: 40rpx;
}

.head-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
}
</style>
