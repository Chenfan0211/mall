<template>
  <view class="page no-tab search-page">
    <view class="search-head">
      <button class="back-btn" @click="goBack">‹</button>
      <input v-model="keyword" class="search-input" placeholder="搜索当前自提点可买商品" confirm-type="search" @confirm="runSearch" />
      <button class="small primary" @click="runSearch">搜索</button>
    </view>

    <view class="search-section">
      <view class="section-title">
        <text>历史搜索</text>
        <button class="plain small" @click="clearHistory">一键清空</button>
      </view>
      <view class="chip-row">
        <text v-for="item in state.searchHistory" :key="item" class="pill" @click="selectWord(item)">{{ item }}</text>
      </view>
    </view>

    <view class="search-section">
      <view class="section-title">
        <text>热门搜索</text>
        <text>实时推荐</text>
      </view>
      <view class="chip-row">
        <text v-for="item in hotWords" :key="item" class="pill" @click="selectWord(item)">{{ item }}</text>
      </view>
    </view>

    <view class="search-section">
      <view class="section-title">
        <text>{{ resultTitle }}</text>
        <text>当前自提点</text>
      </view>
      <view class="chip-row sort-row">
        <text v-for="item in sortTabs" :key="item" class="pill" :class="{ active: activeSort === item }" @click="activeSort = item">
          {{ item }}
        </text>
      </view>
      <view class="product-list">
        <ProductCard
          v-for="item in sortedResults"
          :key="item.publishSkuId"
          :product="item"
          :favorite="state.favorites.has(item.productId)"
          @open="openDetail(item.productId)"
          @favorite="toggleFavorite(item.productId)"
          @notice="toggleNotice(item.productId)"
          @add="handleAddProduct(item)"
        />
        <EmptyActionCard
          v-if="sortedResults.length === 0"
          title="暂无搜索结果"
          sub="没有找到可购买商品，已为你推荐热销商品。"
          icon="搜"
          button-text="返回首页"
          @action="goHome"
        />
      </view>
    </view>
    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';

import EmptyActionCard from '@/components/EmptyActionCard.vue';
import ProductCard from '@/components/ProductCard.vue';
import UserToast from '@/components/UserToast.vue';
import { pageProducts, type UserProductCardDTO } from '@/api/user';
import {
    addProductToCart,
    navigateUser,
    rememberSearchKeyword,
    showUserToast,
    toggleFavorite,
    toggleNotice,
    useUserState
} from '@/stores/userState';

const state = useUserState();
const keyword = ref('');
const activeSort = ref('匹配度');
const results = ref<UserProductCardDTO[]>([]);
const hotWords = ['蓝莓', '苹果', '早餐组合', '有机蔬菜', '牛奶'];
const sortTabs = ['匹配度', '销量', '上新', '价格'];
const resultTitle = computed(() => (keyword.value ? `“${keyword.value}”搜索结果` : '搜索结果'));
const sortedResults = computed(() => {
    const list = [...results.value];
    if (activeSort.value === '价格') {
        list.sort((a, b) => Number(a.salePrice || 0) - Number(b.salePrice || 0));
    }
    if (activeSort.value === '销量') {
        list.sort((a, b) => Number(b.soldQty || 0) - Number(a.soldQty || 0));
    }
    return list.sort((a, b) => Number(b.availableQty > 0) - Number(a.availableQty > 0));
});

async function runSearch() {
    rememberSearchKeyword(keyword.value);
    try {
        const page = await pageProducts({
            pageNum: 1,
            pageSize: 20,
            keyword: keyword.value,
            userId: state.user.id,
            cityId: state.city.id,
            stationId: state.station.id
        });
        results.value = page.list || [];
    } catch (error) {
        showUserToast('搜索失败，请稍后重试', 'warn');
    }
}

function selectWord(value: string) {
    keyword.value = value;
    void runSearch();
}

function clearHistory() {
    state.searchHistory = [];
    showUserToast('搜索历史已清空');
}

function handleAddProduct(item: UserProductCardDTO) {
    if (!state.authenticated) {
        state.afterLoginUrl = '/pages/search/index';
        showUserToast('请先登录，登录后可加入购物车', 'warn');
        navigateUser('/pages/login/index');
        return;
    }
    addProductToCart(item);
}

function openDetail(productId: number) {
    navigateUser(`/pages/product/detail?id=${productId}`);
}

function goBack() {
    uni.navigateBack({
        fail() {
            uni.switchTab({ url: '/pages/home/index' });
        }
    });
}

function goHome() {
    uni.switchTab({ url: '/pages/home/index' });
}

onMounted(() => {
    void runSearch();
});
</script>

<style lang="scss" scoped>
.search-page {
  padding-top: 28rpx;
}

.search-head {
  display: grid;
  grid-template-columns: 64rpx minmax(0, 1fr) 110rpx;
  gap: 12rpx;
  align-items: center;
}

.back-btn {
  width: 64rpx;
  min-width: 64rpx;
  height: 64rpx;
  min-height: 64rpx;
  padding: 0;
  color: #172033 !important;
  background: #ffffff !important;
  font-size: 46rpx;
  box-shadow: none;
}

.search-section {
  margin-top: 18rpx;
  padding: 22rpx;
  background: #ffffff;
  border: 1rpx solid #f0dfd6;
  border-radius: 26rpx;
}

.section-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  margin-bottom: 16rpx;
}

.section-title text:first-child {
  color: #172033;
  font-size: 30rpx;
  font-weight: 900;
}

.section-title text:last-child {
  color: #8c6a58;
  font-size: 22rpx;
}

.sort-row {
  margin-bottom: 16rpx;
}

.product-list {
  margin-top: 14rpx;
}
</style>
