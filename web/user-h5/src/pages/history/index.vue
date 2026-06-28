<template>
  <view class="page no-tab history-page">
    <view class="section">
      <view class="head-row">
        <view>
          <text class="title">浏览历史</text>
          <text class="subtle">按最近浏览商品展示，清空只影响本机记录。</text>
        </view>
        <button class="plain small" @click="clearVisible = true">清空</button>
      </view>
    </view>

    <view v-for="item in products" :key="item.publishSkuId" class="section history-row" @click="openDetail(item.productId)">
      <view class="history-thumb">鲜</view>
      <view>
        <text>{{ item.productName }}</text>
        <text>{{ item.skuName }} · 库存 {{ item.availableQty }} · ¥{{ item.salePrice }}</text>
        <text>今天浏览</text>
      </view>
    </view>

    <EmptyActionCard
      v-if="products.length === 0"
      title="暂无浏览记录"
      sub="进入商品详情后，会在这里展示最近浏览。"
      icon="史"
      button-text="去首页"
      @action="goHome"
    />
    <ConfirmDialog
      :visible="clearVisible"
      title="清空浏览历史"
      content="确认清空当前浏览历史？"
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
import UserToast from '@/components/UserToast.vue';
import { pageProducts, type UserProductCardDTO } from '@/api/user';
import { navigateUser, showUserToast, useUserState } from '@/stores/userState';

const state = useUserState();
const products = ref<UserProductCardDTO[]>([]);
const clearVisible = ref(false);

async function loadData() {
    try {
        const page = await pageProducts({ pageNum: 1, pageSize: 8, cityId: state.city.id, stationId: state.station.id });
        products.value = page.list || [];
    } catch (error) {
        showUserToast('浏览历史接口暂不可用', 'warn');
    }
}

function openDetail(productId: number) {
    navigateUser(`/pages/product/detail?id=${productId}`);
}

function confirmClear() {
    products.value = [];
    clearVisible.value = false;
    showUserToast('浏览历史已清空');
}

function goHome() {
    uni.switchTab({ url: '/pages/home/index' });
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.head-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
}

.history-row {
  display: grid;
  grid-template-columns: 92rpx minmax(0, 1fr);
  gap: 16rpx;
  align-items: center;
}

.history-thumb {
  display: grid;
  width: 92rpx;
  height: 92rpx;
  place-items: center;
  color: #7c1d12;
  background: linear-gradient(135deg, #fff4c7, #f05a37);
  border-radius: 18rpx;
  font-weight: 900;
}

.history-row view:last-child {
  min-width: 0;
}

.history-row view:last-child text {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.history-row view:last-child text:first-child {
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
}

.history-row view:last-child text:not(:first-child) {
  margin-top: 6rpx;
  color: #8c6a58;
  font-size: 22rpx;
}
</style>
