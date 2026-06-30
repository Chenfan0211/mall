<template>
  <view class="service-page shop-page" data-m-page="browseHistory">
    <view class="service-head">
      <view class="order-nav">
        <button class="order-back" @click="goMine">‹</button>
        <b>浏览历史</b>
        <span>{{ products.length }}条</span>
      </view>
    </view>

    <view class="service-list">
      <view class="pay-result-actions">
        <button @click="clearVisible = true">清空浏览历史</button>
        <button @click="goHome">去首页逛逛</button>
      </view>

      <view v-if="products.length > 0" class="history-date-group">
        <view class="history-date-title">今天<span>{{ products.length }}条</span></view>
        <view
          v-for="item in products"
          :key="item.publishSkuId"
          class="service-product-row"
          @click="openDetail(item.productId)"
        >
          <view class="service-product-thumb" :style="serviceProductThumbStyle(item.mainImageUrl)" />
          <view class="service-product-main">
            <b>{{ item.productName }}</b>
            <span>{{ productSpecText(item) }}<br />¥{{ item.salePrice }} · 库存 {{ item.availableQty }}</span>
            <em>最近浏览 {{ historyTime }}</em>
          </view>
          <button class="service-action" @click.stop="openDetail(item.productId)">再次查看</button>
        </view>
      </view>

      <EmptyActionCard
        v-if="products.length === 0"
        title="暂无浏览历史"
        sub="最近浏览的商品会显示在这里。"
        icon="史"
        button-text="去首页逛逛"
        @action="goHome"
      />
    </view>

    <ConfirmDialog
      :visible="clearVisible"
      title="清空浏览历史"
      content="清空后将不再展示浏览记录。"
      confirm-text="确认清空"
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
import { navigateUser, showUserToast, useUserState } from '@/stores/userState';
import { fallbackProductImages, fallbackProducts, productSpecText, withProductImages } from '@/utils/userFallbackData';

const state = useUserState();
const products = ref<UserProductCardDTO[]>([]);
const clearVisible = ref(false);
const historyTime = '2026-06-19 10:20';

async function loadData() {
    try {
        const page = await pageProducts(
            { pageNum: 1, pageSize: 8, cityId: state.city.id, stationId: state.station.id },
            { silent: true }
        );
        products.value = page?.list?.length ? withProductImages(page.list).slice(0, 6) : fallbackProducts.slice(0, 6);
    } catch {
        products.value = fallbackProducts.slice(0, 6);
        console.info('用户端浏览历史接口不可用，已展示本地浏览记录');
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

function confirmClear() {
    products.value = [];
    clearVisible.value = false;
    showUserToast('浏览历史已清空');
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

<style lang="scss" scoped>
.history-date-group {
  display: grid;
  gap: 14rpx;
}

.history-date-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #172033;
  font-size: 26rpx;
  font-weight: 900;
}

.history-date-title span {
  color: #8c6a58;
  font-size: 25rpx;
}
</style>
