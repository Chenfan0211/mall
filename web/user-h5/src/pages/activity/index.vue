<template>
  <view class="activity-page shop-page" data-m-page="activity">
    <view class="activity-nav">
      <button class="activity-back" @click="goHome">‹</button>
      <view class="activity-title">{{ activityTitle }}</view>
      <view class="activity-menu">••• ◎</view>
    </view>

    <view v-if="activityImageUrl" class="activity-hero">
      <view class="activity-hero-img" :style="backgroundImageStyle(activityImageUrl)" />
      <view class="activity-hero-text">
        <b>{{ activityHeroTitle }}</b>
        <span>{{ activitySubTitle }}</span>
      </view>
    </view>

    <view class="product-sort-card">
      <b>排序</b>
      <view>
        <button v-for="item in sortTabs" :key="item" :class="{ active: activeSort === item }" @click="activeSort = item">
          {{ item }}
        </button>
      </view>
    </view>

    <view class="activity-grid product-list">
      <view
        v-for="item in sortedProducts"
        :key="item.publishSkuId"
        class="shop-product"
        :class="{ soldout: isSoldout(item) }"
        @click="openDetail(item.productId)"
      >
        <button
          class="product-fav-btn"
          :class="{ active: state.favorites.has(item.productId) }"
          @click.stop="toggleFavorite(item.productId)"
        >
          {{ state.favorites.has(item.productId) ? '已藏' : '收藏' }}
        </button>
        <view class="shop-product-media">
          <view class="shop-product-img" :style="backgroundImageStyle(item.mainImageUrl || fallbackProductImages[0])" />
        </view>
        <view class="shop-product-info">
          <h4>{{ item.productName }}</h4>
          <view class="shop-product-spec">{{ productSpecText(item) }}</view>
          <view class="price-row">
            <span class="shop-price">¥{{ item.salePrice }}</span>
            <span v-if="productOldPrice(item)" class="old-price">¥{{ productOldPrice(item) }}</span>
          </view>
          <view class="shop-sale-meta">
            <span class="sold-count">已售{{ item.soldQty || 0 }}</span>
            <span class="sale-stock"><b>{{ isSoldout(item) ? '已售罄' : `剩余${item.availableQty}` }}</b></span>
          </view>
        </view>
        <view class="shop-product-tag-row">
          <span class="shop-product-date">提货日期 {{ productPickupText(item.deliveryDate) }}</span>
          <button class="rank-line" @click.stop="openRank">鲜选榜单</button>
        </view>
        <button
          class="round-add"
          :aria-label="isSoldout(item) ? '已售罄' : '加入购物车'"
          @click.stop="isSoldout(item) ? toggleNotice(item.productId) : addProduct(item)"
        >
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <circle cx="8.8" cy="20" r="1.5"></circle>
            <circle cx="17.8" cy="20" r="1.5"></circle>
            <path d="M3.5 4.5h2.4L8 15.7h9.7l2.2-7.8H7.2"></path>
            <path d="M15.8 4.2v6.4"></path>
            <path d="M12.6 7.4H19"></path>
          </svg>
        </button>
      </view>

      <EmptyActionCard
        v-if="sortedProducts.length === 0"
        title="暂无活动商品"
        sub="当前活动暂无可购买商品，先回首页查看其他商品。"
        icon="鲜"
        button-text="返回首页"
        @action="goHome"
      />
    </view>

    <UserToast />
    <UserTabBar active="home" />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';

import EmptyActionCard from '@/components/EmptyActionCard.vue';
import UserTabBar from '@/components/UserTabBar.vue';
import UserToast from '@/components/UserToast.vue';
import {
    getHomeAssets,
    pageActivityProducts,
    type UserHomeBannerDTO,
    type UserHomePromoDTO,
    type UserProductCardDTO
} from '@/api/user';
import { addProductToCart, navigateUser, showUserToast, toggleFavorite, toggleNotice, useUserState } from '@/stores/userState';
import { currentRouteQuery } from '@/utils/routeQuery';
import {
    fallbackProductImages,
    isSoldout,
    productOldPrice,
    productPickupText,
    productSpecText,
    withProductImages
} from '@/utils/userFallbackData';

const state = useUserState();
const products = ref<UserProductCardDTO[]>([]);
const activeSort = ref('推荐');
const activityCode = ref('HOME_MAIN');
const activityAsset = ref<UserHomeBannerDTO | UserHomePromoDTO | null>(null);
const sortTabs = ['推荐', '销量', '上新', '价格'];

const activityTitle = computed(() => activityAsset.value?.title || '活动专题');
const activityHeroTitle = computed(() => {
    const asset = activityAsset.value;
    if (!asset) {
        return '活动专题';
    }
    return 'subTitle' in asset && asset.subTitle ? asset.subTitle : asset.title;
});
const activitySubTitle = computed(() => {
    const asset = activityAsset.value;
    if (!asset) {
        return '当前自提点可买';
    }
    return 'description' in asset && asset.description ? asset.description : '当前自提点可买';
});
const activityImageUrl = computed(() => activityAsset.value?.imageUrl || '');

const sortedProducts = computed(() => {
    const list = [...products.value];
    if (activeSort.value === '销量') {
        list.sort((left, right) => Number(right.soldQty || 0) - Number(left.soldQty || 0));
    }
    if (activeSort.value === '上新') {
        list.sort((left, right) => Number(right.publishSkuId || 0) - Number(left.publishSkuId || 0));
    }
    if (activeSort.value === '价格') {
        list.sort((left, right) => Number(left.salePrice || 0) - Number(right.salePrice || 0));
    }
    return list.sort((left, right) => Number(right.availableQty > 0) - Number(left.availableQty > 0));
});

async function loadData() {
    const code = activityCode.value;
    await loadActivityAsset(code);
    try {
        const page = await pageActivityProducts(
            code,
            { pageNum: 1, pageSize: 20, cityId: state.city.id, stationId: state.station.id },
            { silent: true }
        );
        products.value = page?.list?.length ? withProductImages(page.list) : [];
    } catch {
        products.value = [];
        console.info('用户端活动商品接口不可用，活动页不展示兜底商品');
    }
}

async function loadActivityAsset(code: string) {
    try {
        const assets = await getHomeAssets(
            {
                cityId: state.city.id,
                stationId: state.station.id
            },
            { silent: true }
        );
        const banners = assets?.banners || [];
        const promos = assets?.promos || [];
        activityAsset.value = [...banners, ...promos].find((item) => (item.activityCode || item.code) === code) || null;
    } catch {
        activityAsset.value = null;
        console.info('用户端活动资源接口不可用，活动页仅展示商品空态或商品列表');
    }
}

function addProduct(item: UserProductCardDTO) {
    if (!state.authenticated) {
        state.afterLoginUrl = `/pages/activity/index?activityCode=${encodeURIComponent(activityCode.value)}`;
        showUserToast('请先登录，登录后可加入购物车', 'warn');
        navigateUser('/pages/login/index');
        return;
    }
    addProductToCart(item);
}

function openDetail(productId: number) {
    navigateUser(`/pages/product/detail?id=${productId}`);
}

function openRank() {
    navigateUser('/pages/rank/index');
}

function goHome() {
    navigateUser('/pages/home/index', true);
}

function backgroundImageStyle(url?: string) {
    return {
        backgroundImage: `url("${url || fallbackProductImages[0]}")`
    };
}

onMounted(() => {
    const query = currentRouteQuery();
    activityCode.value = query.activityCode || query.code || 'HOME_MAIN';
    void loadData();
});
</script>

<style lang="scss" scoped>
.activity-page {
  min-height: 100vh;
  padding: 0 0 184rpx;
  background: #f3f3f3;
}

.activity-nav,
.activity-hero-text,
.product-sort-card,
.product-sort-card view,
.shop-sale-meta,
.shop-product-tag-row {
  display: flex;
  align-items: center;
}

.activity-nav {
  position: sticky;
  top: 0;
  z-index: 6;
  display: grid;
  grid-template-columns: 112rpx minmax(0, 1fr) 172rpx;
  height: 152rpx;
  padding: 48rpx 24rpx 0;
  color: #111111;
  background: #ffffff;
  border-bottom: 1rpx solid #ededed;
}

.activity-back {
  width: 68rpx;
  min-width: 68rpx;
  height: 68rpx;
  min-height: 68rpx;
  padding: 0;
  color: #111111 !important;
  background: #ffffff !important;
  border-radius: 50% !important;
  box-shadow: none;
  font-size: 76rpx;
  font-weight: 400;
  line-height: 54rpx;
}

.activity-title {
  color: #111111;
  font-size: 48rpx;
  font-weight: 900;
  line-height: 1;
  text-align: center;
}

.activity-menu {
  justify-content: space-around;
  width: 172rpx;
  min-width: 172rpx;
  height: 80rpx;
  color: #111111;
  background: #ffffff;
  border: 1rpx solid #f0f0f0;
  border-radius: 44rpx;
  box-shadow: 0 8rpx 28rpx rgba(16, 24, 40, 0.05);
  font-size: 48rpx;
  line-height: 80rpx;
}

.activity-hero {
  position: relative;
  height: 236rpx;
  margin: 20rpx 20rpx 16rpx;
  overflow: hidden;
  border-radius: 20rpx;
  box-shadow: none;
}

.activity-hero-img {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
}

.activity-hero::after {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.08), rgba(0, 0, 0, 0.28));
  content: "";
}

.activity-hero-text {
  position: absolute;
  left: 28rpx;
  right: 28rpx;
  bottom: 24rpx;
  z-index: 1;
  align-items: flex-start;
  flex-direction: column;
  gap: 8rpx;
  color: #ffffff;
  text-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.28);
}

.activity-hero-text b {
  font-size: 44rpx;
  font-weight: 900;
  line-height: 1.1;
}

.activity-hero-text span {
  font-size: 26rpx;
  font-weight: 700;
}

.product-sort-card {
  justify-content: space-between;
  gap: 16rpx;
  margin: 0 20rpx 16rpx;
  padding: 14rpx 16rpx;
  background: #ffffff;
  border: 0;
  border-radius: 18rpx;
  box-shadow: 0 2rpx 10rpx rgba(16, 24, 40, 0.04);
}

.product-sort-card b {
  color: #172033;
  font-size: 26rpx;
  font-weight: 900;
}

.product-sort-card view {
  gap: 8rpx;
}

.product-sort-card button {
  min-width: 72rpx;
  min-height: 48rpx;
  padding: 0 14rpx;
  color: #8c6a58 !important;
  background: transparent !important;
  border-radius: 999rpx !important;
  font-size: 25rpx;
  box-shadow: none;
}

.product-sort-card button.active {
  color: #ffffff !important;
  background: #e85d3f !important;
}

.activity-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
  padding: 0 20rpx 24rpx;
}

.shop-product {
  position: relative;
  display: block;
  min-width: 0;
  min-height: 0;
  padding: 0;
  overflow: hidden;
  background: #ffffff;
  border: 0;
  border-radius: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(16, 24, 40, 0.06);
}

.shop-product.soldout {
  opacity: 0.72;
}

.shop-product-media {
  position: relative;
  width: 100%;
  height: 184rpx;
  overflow: hidden;
  background: #fff4eb;
  border-radius: 0;
}

.shop-product-img {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
}

.shop-product.soldout .shop-product-media::after {
  position: absolute;
  inset: 0;
  display: grid;
  color: #ffffff;
  background: rgba(23, 32, 51, 0.46);
  content: "已售罄";
  font-size: 26rpx;
  font-weight: 900;
  place-items: center;
}

.product-fav-btn {
  position: absolute;
  top: 10rpx;
  right: 10rpx;
  z-index: 2;
  min-width: 70rpx;
  min-height: 44rpx;
  padding: 4rpx 14rpx;
  color: #a06c52 !important;
  background: rgba(255, 250, 246, 0.92) !important;
  border: 1rpx solid #f0dfd6;
  border-radius: 999rpx !important;
  font-size: 26rpx;
  box-shadow: none;
}

.product-fav-btn.active {
  color: #e85d3f !important;
  border-color: #f2c9b7;
}

.shop-product-info {
  min-width: 0;
  min-height: 176rpx;
  padding: 16rpx;
}

.shop-product-info h4 {
  display: -webkit-box;
  overflow: hidden;
  min-height: 68rpx;
  margin: 0 0 6rpx;
  color: #172033;
  font-size: 26rpx;
  font-weight: 900;
  line-height: 1.28;
  text-overflow: ellipsis;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.shop-product-spec {
  overflow: hidden;
  min-height: 34rpx;
  height: auto;
  color: #8a5a32;
  font-size: 25rpx;
  line-height: 1.25;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price-row {
  margin-top: 8rpx;
}

.shop-price {
  color: #f20d0d;
  font-size: 34rpx;
  font-weight: 900;
}

.old-price {
  margin-left: 6rpx;
  color: #b99c8a;
  font-size: 26rpx;
  text-decoration: line-through;
}

.shop-sale-meta {
  flex-wrap: wrap;
  gap: 6rpx;
  margin-top: 8rpx;
}

.shop-sale-meta span {
  min-height: 40rpx;
  padding: 4rpx 10rpx;
  color: #7b5f51;
  background: #fff2e9;
  border-radius: 999rpx;
  font-size: 25rpx;
  font-weight: 700;
  line-height: 1.2;
}

.shop-sale-meta b {
  color: #e85d3f;
}

.shop-product-tag-row {
  gap: 6rpx;
  padding: 0 16rpx 16rpx;
}

.shop-product-date,
.rank-line {
  min-height: 42rpx;
  padding: 4rpx 12rpx;
  border-radius: 999rpx;
  font-size: 25rpx;
  line-height: 1.2;
}

.shop-product-date {
  overflow: hidden;
  max-width: 188rpx;
  color: #975b31;
  background: #fff1da;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.rank-line {
  color: #e85d3f !important;
  background: #fff5f1 !important;
  border: 0;
  box-shadow: none;
}

.round-add {
  position: absolute;
  right: 16rpx;
  bottom: 78rpx;
  display: grid;
  width: 64rpx;
  min-width: 64rpx;
  height: 64rpx;
  min-height: 64rpx;
  padding: 0;
  background: #f20d0d !important;
  border-radius: 50% !important;
  box-shadow: 0 10rpx 18rpx rgba(242, 13, 13, 0.22);
  place-items: center;
}

.round-add svg {
  width: 36rpx;
  height: 36rpx;
  fill: none;
  stroke: #ffffff;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 2.2;
}
</style>
