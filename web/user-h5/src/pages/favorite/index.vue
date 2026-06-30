<template>
  <view class="service-page shop-page" data-m-page="favorites">
    <view class="service-head">
      <view class="order-nav">
        <button class="order-back" @click="goMine">‹</button>
        <b>我的收藏</b>
        <span>{{ products.length }}个</span>
      </view>
    </view>

    <view class="service-list">
      <view class="pay-result-actions">
        <button @click="clearAll">清理失效收藏</button>
        <button @click="goHome">去首页逛逛</button>
      </view>

      <view
        v-for="item in products"
        :key="item.publishSkuId"
        class="service-product-row"
        :class="{ invalid: isSoldout(item) }"
        @click="openDetail(item.productId)"
      >
        <view class="service-product-thumb" :style="serviceProductThumbStyle(item.mainImageUrl)" />
        <view class="service-product-main">
          <b>{{ item.productName }}</b>
          <span>{{ productSpecText(item) }}<br />¥{{ item.salePrice }}{{ isSoldout(item) ? ' · 已失效' : '' }}</span>
          <em>{{ isSoldout(item) ? '商品已售罄或下架' : `收藏时间 ${favoriteTime}` }}</em>
        </view>
        <button class="service-action" :class="{ used: isSoldout(item) }" @click.stop="isSoldout(item) ? removeFavorite(item.productId) : openDetail(item.productId)">
          {{ isSoldout(item) ? '已失效' : '去看看' }}
        </button>
      </view>

      <EmptyActionCard
        v-if="products.length === 0"
        title="暂无收藏商品"
        sub="收藏商品后会显示在这里。"
        icon="藏"
        button-text="去首页逛逛"
        @action="goHome"
      />
    </view>

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
    <UserTabBar active="mine" />
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';

import ConfirmDialog from '@/components/ConfirmDialog.vue';
import EmptyActionCard from '@/components/EmptyActionCard.vue';
import UserTabBar from '@/components/UserTabBar.vue';
import UserToast from '@/components/UserToast.vue';
import { pageProducts, type UserProductCardDTO } from '@/api/user';
import { navigateUser, showUserToast, toggleFavorite, useUserState } from '@/stores/userState';
import { fallbackProductImages, fallbackProducts, isSoldout, productSpecText, withProductImages } from '@/utils/userFallbackData';

const state = useUserState();
const products = ref<UserProductCardDTO[]>([]);
const clearVisible = ref(false);
const favoriteTime = '2026-06-19 10:00';

async function loadData() {
    try {
        const page = await pageProducts(
            { pageNum: 1, pageSize: 20, cityId: state.city.id, stationId: state.station.id },
            { silent: true }
        );
        const rows = page?.list?.length ? withProductImages(page.list) : fallbackProducts;
        products.value = state.favorites.size > 0 ? rows.filter((item) => state.favorites.has(item.productId)) : rows.slice(0, 4);
    } catch {
        products.value = fallbackProducts.slice(0, 4);
        console.info('用户端收藏页商品接口不可用，已展示本地兜底收藏');
    }
}

function openDetail(productId: number) {
    navigateUser(`/pages/product/detail?id=${productId}`);
}

function serviceProductThumbStyle(url?: string) {
    return {
        backgroundImage: `url("${url || fallbackProductImages[0]}")`
    };
}

function removeFavorite(productId: number) {
    if (state.favorites.has(productId)) {
        toggleFavorite(productId);
    }
    products.value = products.value.filter((item) => item.productId !== productId);
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

function goMine() {
    navigateUser('/pages/mine/index', true);
}

function goHome() {
    navigateUser('/pages/home/index', true);
}

onMounted(() => {
    void loadData();
});
</script>
