<template>
  <view class="page no-tab rank-page">
    <view class="section">
      <text class="title">自提点热卖榜</text>
      <text class="subtle">排序不替代真实可售校验。</text>
    </view>

    <view v-for="(item, index) in products" :key="item.publishSkuId" class="rank-card">
      <text class="rank">#{{ index + 1 }}</text>
      <ProductCard
        :product="item"
        :favorite="state.favorites.has(item.productId)"
        @open="openDetail(item.productId)"
        @favorite="toggleFavorite(item.productId)"
        @notice="toggleNotice(item.productId)"
        @add="addProduct(item)"
      />
    </view>

    <EmptyActionCard
      v-if="products.length === 0"
      title="暂无榜单"
      sub="当前自提点还没有足够销量数据。"
      icon="榜"
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
        products.value = (page.list || []).sort((left, right) => (right.soldQty || 0) - (left.soldQty || 0));
    } catch (error) {
        showUserToast('榜单接口暂不可用', 'warn');
    }
}

function addProduct(item: UserProductCardDTO) {
    if (!state.authenticated) {
        state.afterLoginUrl = '/pages/rank/index';
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
.rank-page {
  padding-bottom: 40rpx;
}

.rank-card {
  position: relative;
}

.rank {
  position: absolute;
  left: 10rpx;
  top: 10rpx;
  z-index: 4;
  min-width: 68rpx;
  height: 44rpx;
  color: #ffffff;
  background: #172033;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 900;
  line-height: 44rpx;
  text-align: center;
}
</style>
