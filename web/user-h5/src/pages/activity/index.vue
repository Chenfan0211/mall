<template>
  <view class="page no-tab activity-page">
    <view class="section hero">
      <text class="title">周末鲜果专题</text>
      <text class="subtle">活动只影响展示，不改变价格库存校验。</text>
    </view>

    <ProductCard
      v-for="item in products"
      :key="item.publishSkuId"
      :product="item"
      :favorite="state.favorites.has(item.productId)"
      @open="openDetail(item.productId)"
      @favorite="toggleFavorite(item.productId)"
      @notice="toggleNotice(item.productId)"
      @add="addProduct(item)"
    />

    <EmptyActionCard
      v-if="products.length === 0"
      title="活动商品暂未上架"
      sub="当前自提点暂无活动商品，可先返回首页。"
      icon="活"
      button-text="返回首页"
      @action="goHome"
    />
    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';

import EmptyActionCard from '@/components/EmptyActionCard.vue';
import ProductCard from '@/components/ProductCard.vue';
import UserToast from '@/components/UserToast.vue';
import { pageProducts, type UserProductCardDTO } from '@/api/user';
import { addProductToCart, navigateUser, showUserToast, toggleFavorite, toggleNotice, useUserState } from '@/stores/userState';

const state = useUserState();
const products = ref<UserProductCardDTO[]>([]);

async function loadData() {
    try {
        const page = await pageProducts({ pageNum: 1, pageSize: 20, cityId: state.city.id, stationId: state.station.id });
        products.value = page.list || [];
    } catch (error) {
        showUserToast('活动商品接口暂不可用', 'warn');
    }
}

function addProduct(item: UserProductCardDTO) {
    if (!state.authenticated) {
        state.afterLoginUrl = '/pages/activity/index';
        navigateUser('/pages/login/index');
        return;
    }
    addProductToCart(item);
}

function openDetail(productId: number) {
    navigateUser(`/pages/product/detail?id=${productId}`);
}

function goHome() {
    uni.switchTab({ url: '/pages/home/index' });
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.activity-page {
  padding-bottom: 40rpx;
}

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
</style>
