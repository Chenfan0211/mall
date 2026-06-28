<template>
  <view class="page home-page">
    <view class="shop-header">
      <view class="shop-header-row">
        <view class="logo-mark">
          <text>鲜选到家</text>
          <text>今日鲜选</text>
        </view>
        <view class="shop-search" @click="openSearch">⌕ 搜索您喜欢的</view>
        <view class="mini-pill" @click="openMessages">••• ◎</view>
      </view>
      <view class="station-bar" @click="stationSheetVisible = true">
        <view class="station-main">
          <text>{{ state.station.name }}</text>
          <text>提货 {{ state.station.deliveryTime || deliveryDate }}</text>
          <view class="station-meta-row">
            <text>提货人 {{ receiverName }}</text>
            <text>团点自提</text>
          </view>
        </view>
        <button class="plain small">切换</button>
      </view>
    </view>

    <view v-if="state.station.status !== 1" class="station-alert">
      <text>{{ state.station.abnormalReason || '当前自提点暂不可选，请重新选择自提点' }}</text>
      <button class="plain small" @click="stationSheetVisible = true">重新选择</button>
    </view>

    <view class="cat-grid">
      <view v-for="item in categoryCards" :key="item.id" class="cat-item" @click="openCategory(item.id)">
        <view class="cat-img">{{ item.shortName }}</view>
        <text>{{ item.categoryName }}</text>
      </view>
      <view class="cat-item" @click="openRank">
        <view class="cat-img rank">榜</view>
        <text>热销榜</text>
      </view>
    </view>

    <view class="hero-banner" @click="openActivity">
      <view class="banner-text">
        <text class="banner-kicker">本周鲜果专场</text>
        <text class="banner-title">基地直采</text>
        <text class="banner-title">次日到团点</text>
        <text>爆汁橙、蓝莓、梨礼盒限时上新</text>
      </view>
      <view class="banner-dots">
        <text class="active"></text>
        <text></text>
        <text></text>
      </view>
    </view>

    <view class="promo-grid">
      <view class="promo-card" @click="openActivity">
        <view>
          <text>果园直供</text>
          <text>全部 ›</text>
        </view>
        <view class="promo-img fruit">鲜</view>
      </view>
      <view class="promo-stack">
        <view class="promo-card mini" @click="openActivity">
          <view>
            <text>今日上新</text>
            <text>全部 ›</text>
          </view>
          <view class="promo-img small-img">新</view>
        </view>
        <view class="promo-card mini" @click="openActivity">
          <view>
            <text>早餐烘焙</text>
            <text>全部 ›</text>
          </view>
          <view class="promo-img bakery">早</view>
        </view>
      </view>
    </view>

    <view class="sort-card">
      <view class="sort-head">
        <text>推荐排序</text>
        <text>仅展示当前自提点可买商品</text>
      </view>
      <view class="chip-row">
        <text v-for="item in sortTabs" :key="item" class="pill" :class="{ active: activeSort === item }" @click="activeSort = item">
          {{ item }}
        </text>
      </view>
    </view>

    <view class="product-list">
      <ProductCard
        v-for="item in sortedProducts"
        :key="item.publishSkuId"
        :product="item"
        :favorite="state.favorites.has(item.productId)"
        @open="openProduct(item.productId)"
        @favorite="toggleFavorite(item.productId)"
        @notice="toggleNotice(item.productId)"
        @add="handleAddProduct(item)"
      />
      <EmptyActionCard
        v-if="sortedProducts.length === 0"
        title="暂无可售商品"
        sub="当前自提点还没有上架团期商品。"
        icon="鲜"
        button-text="切换自提点"
        @action="stationSheetVisible = true"
      />
    </view>

    <StationSheet :visible="stationSheetVisible" @close="stationSheetVisible = false" />
    <UserToast />
    <UserTabBar active="home" />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';

import ProductCard from '@/components/ProductCard.vue';
import EmptyActionCard from '@/components/EmptyActionCard.vue';
import StationSheet from '@/components/StationSheet.vue';
import UserTabBar from '@/components/UserTabBar.vue';
import UserToast from '@/components/UserToast.vue';
import { getHomeSummary, pageCategories, pageProducts, type UserCategoryDTO, type UserProductCardDTO } from '@/api/user';
import {
    addProductToCart,
    defaultReceiver,
    navigateUser,
    showUserToast,
    toggleFavorite,
    toggleNotice,
    useUserState
} from '@/stores/userState';

const state = useUserState();
const stationSheetVisible = ref(false);
const categories = ref<UserCategoryDTO[]>([]);
const products = ref<UserProductCardDTO[]>([]);
const deliveryDate = ref('待定');
const activeSort = ref('推荐');
const sortTabs = ['推荐', '销量', '上新', '价格'];

const fallbackCategories = [
    { id: 1, categoryName: '新鲜水果', shortName: '果' },
    { id: 2, categoryName: '时令蔬菜', shortName: '蔬' },
    { id: 3, categoryName: '肉禽蛋奶', shortName: '鲜' },
    { id: 4, categoryName: '米面粮油', shortName: '粮' }
];

const receiverName = computed(() => defaultReceiver()?.name || state.user.nickname);
const categoryCards = computed(() => {
    const rows = categories.value.slice(0, 4).map((item) => ({
        id: item.id,
        categoryName: item.categoryName,
        shortName: item.categoryName.slice(0, 1)
    }));
    return rows.length > 0 ? rows : fallbackCategories;
});
const sortedProducts = computed(() => {
    const list = [...products.value];
    if (activeSort.value === '价格') {
        list.sort((a, b) => Number(a.salePrice || 0) - Number(b.salePrice || 0));
    }
    if (activeSort.value === '销量') {
        list.sort((a, b) => Number(b.soldQty || 0) - Number(a.soldQty || 0));
    }
    return list.sort((a, b) => Number(b.availableQty > 0) - Number(a.availableQty > 0));
});

async function loadData() {
    try {
        const [home, categoryPage, productPage] = await Promise.all([
            getHomeSummary({
                userId: state.user.id,
                cityId: state.city.id,
                stationId: state.station.id
            }),
            pageCategories({ pageNum: 1, pageSize: 10, status: 1 }),
            pageProducts({
                pageNum: 1,
                pageSize: 10,
                userId: state.user.id,
                cityId: state.city.id,
                stationId: state.station.id
            })
        ]);
        categories.value = categoryPage.list || [];
        products.value = productPage.list || [];
        deliveryDate.value = products.value[0]?.deliveryDate || state.station.deliveryTime || '待定';
        if (home.stationId) {
            state.station.id = home.stationId;
        }
    } catch (error) {
        showUserToast('商品加载失败，已展示本地页面状态', 'warn');
    }
}

function handleAddProduct(item: UserProductCardDTO) {
    if (!state.authenticated) {
        showUserToast('请先登录，登录后可加入购物车', 'warn');
        state.afterLoginUrl = '/pages/home/index';
        navigateUser('/pages/login/index');
        return;
    }
    addProductToCart(item);
}

function openProduct(productId: number) {
    navigateUser(`/pages/product/detail?id=${productId}`);
}

function openSearch() {
    navigateUser('/pages/search/index');
}

function openMessages() {
    navigateUser('/pages/message/index');
}

function openCategory(categoryId: number) {
    uni.switchTab({ url: `/pages/category/index?categoryId=${categoryId}` });
}

function openActivity() {
    navigateUser('/pages/activity/index');
}

function openRank() {
    navigateUser('/pages/rank/index');
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.home-page {
  padding-left: 0;
  padding-right: 0;
}

.shop-header {
  padding: 12rpx 24rpx 20rpx;
  color: #ffffff;
  background: linear-gradient(135deg, #d94b34 0%, #f28a42 100%);
}

.shop-header-row {
  display: grid;
  grid-template-columns: 160rpx minmax(0, 1fr) 94rpx;
  gap: 16rpx;
  align-items: center;
}

.logo-mark {
  display: grid;
  color: #ffffff;
  font-size: 36rpx;
  font-weight: 900;
  line-height: 1.05;
}

.logo-mark text:last-child {
  margin-top: 4rpx;
  font-size: 20rpx;
  font-weight: 800;
  opacity: 0.9;
}

.shop-search {
  display: flex;
  align-items: center;
  height: 76rpx;
  padding: 0 24rpx;
  color: #7b5f51;
  background: #ffffff;
  border-radius: 40rpx;
  font-size: 26rpx;
  font-weight: 800;
}

.mini-pill {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 68rpx;
  color: #ffffff;
  background: rgba(38, 48, 67, 0.22);
  border-radius: 36rpx;
  font-size: 24rpx;
  font-weight: 800;
}

.station-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  min-height: 122rpx;
  margin-top: 20rpx;
  padding: 16rpx 16rpx 16rpx 24rpx;
  color: #1f2937;
  background: #ffffff;
  border-radius: 32rpx;
  box-shadow: 0 6rpx 0 rgba(0, 0, 0, 0.04);
}

.station-main {
  min-width: 0;
  display: grid;
  gap: 7rpx;
}

.station-main > text:first-child {
  overflow: hidden;
  font-size: 30rpx;
  font-weight: 900;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.station-main > text:nth-child(2) {
  color: #8b6a57;
  font-size: 22rpx;
  font-weight: 800;
}

.station-meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
}

.station-meta-row text {
  padding: 4rpx 12rpx;
  color: #a33a1d;
  background: #fff1e9;
  border-radius: 999rpx;
  font-size: 20rpx;
  font-weight: 800;
}

.station-alert {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12rpx;
  margin: 18rpx 20rpx 0;
  padding: 18rpx;
  color: #b42318;
  background: #fff1e9;
  border: 1rpx solid #ffd2c2;
  border-radius: 22rpx;
  font-size: 23rpx;
}

.cat-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 18rpx 10rpx;
  margin: 20rpx 20rpx 0;
  padding: 24rpx 12rpx 20rpx;
  background: #ffffff;
  border-radius: 28rpx;
}

.cat-item {
  display: grid;
  justify-items: center;
  gap: 10rpx;
  min-width: 0;
  color: #263043;
  font-size: 21rpx;
  font-weight: 800;
  line-height: 1.2;
  text-align: center;
}

.cat-item > text {
  overflow: hidden;
  max-width: 100%;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cat-img {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 88rpx;
  height: 88rpx;
  color: #7c1d12;
  background: linear-gradient(135deg, #fff4c7 0%, #ffcf55 42%, #f05a37 100%);
  border-radius: 26rpx;
  box-shadow: 0 8rpx 24rpx rgba(38, 48, 67, 0.12);
  font-size: 30rpx;
  font-weight: 900;
}

.cat-img.rank {
  color: #ffffff;
  background: linear-gradient(135deg, #172033, #d94b34);
}

.hero-banner {
  position: relative;
  overflow: hidden;
  height: 320rpx;
  margin: 20rpx;
  background:
    linear-gradient(90deg, rgba(23, 32, 51, 0.72), rgba(23, 32, 51, 0.18) 56%, rgba(217, 75, 52, 0.18)),
    linear-gradient(135deg, #263043 0%, #d94b34 58%, #f7b36c 100%);
  border-radius: 28rpx;
  box-shadow: 0 20rpx 44rpx rgba(38, 48, 67, 0.16);
}

.hero-banner::after {
  content: "";
  position: absolute;
  right: -40rpx;
  bottom: -40rpx;
  width: 230rpx;
  height: 230rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.16);
}

.banner-text {
  position: absolute;
  left: 32rpx;
  top: 34rpx;
  z-index: 1;
  display: grid;
  gap: 6rpx;
  color: #ffffff;
  text-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.35);
}

.banner-kicker {
  width: fit-content;
  padding: 6rpx 16rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 999rpx;
  font-size: 23rpx;
  font-weight: 800;
}

.banner-title {
  font-size: 52rpx;
  font-weight: 900;
  line-height: 1.06;
}

.banner-dots {
  position: absolute;
  left: 32rpx;
  bottom: 24rpx;
  z-index: 1;
  display: flex;
  gap: 10rpx;
}

.banner-dots text {
  width: 14rpx;
  height: 14rpx;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 999rpx;
}

.banner-dots .active {
  width: 44rpx;
  background: #ffffff;
}

.promo-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
  gap: 18rpx;
  margin: 0 20rpx 20rpx;
}

.promo-stack {
  display: grid;
  gap: 18rpx;
}

.promo-card {
  overflow: hidden;
  min-height: 296rpx;
  padding: 18rpx;
  background: #ffffff;
  border: 1rpx solid #f0dfd6;
  border-radius: 24rpx;
}

.promo-card.mini {
  min-height: 138rpx;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 72rpx;
  gap: 10rpx;
  align-items: center;
}

.promo-card view:first-child {
  display: flex;
  justify-content: space-between;
  color: #172033;
  font-size: 30rpx;
  font-weight: 900;
}

.promo-card view:first-child text:last-child {
  color: #e85d3f;
  font-size: 22rpx;
}

.promo-img {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 190rpx;
  margin-top: 16rpx;
  color: #ffffff;
  border-radius: 18rpx;
  font-size: 46rpx;
  font-weight: 900;
}

.promo-card.mini .promo-img {
  width: 72rpx;
  height: 72rpx;
  margin-top: 0;
  font-size: 30rpx;
}

.promo-img.fruit,
.promo-img.small-img {
  background: linear-gradient(135deg, #ffcf55, #f05a37);
}

.promo-img.bakery {
  background: linear-gradient(135deg, #f7b36c, #a36d14);
}

.sort-card {
  margin: 0 20rpx 16rpx;
  padding: 20rpx;
  background: #ffffff;
  border: 1rpx solid #f0dfd6;
  border-radius: 24rpx;
}

.sort-head {
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
  margin-bottom: 14rpx;
}

.sort-head text:first-child {
  color: #172033;
  font-size: 30rpx;
  font-weight: 900;
}

.sort-head text:last-child {
  color: #8c6a58;
  font-size: 22rpx;
}

.product-list {
  margin: 0 20rpx;
}
</style>
