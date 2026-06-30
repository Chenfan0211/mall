<template>
  <view class="page home-page shop-page" data-m-page="home">
    <view class="shop-header">
      <view class="status-bar">
        <text>14:49</text>
        <text>▮ ▮ ▮ WiFi 80</text>
      </view>
      <view class="shop-header-row">
        <view class="logo-mark">
          <small>今日鲜选</small>
          <text>鲜选到家</text>
        </view>
        <view class="shop-search" @click="openSearch">
          <text>搜索您喜欢的</text>
        </view>
        <view class="mini-pill" @click="openMessages">••• ◎</view>
      </view>
      <view class="station-bar" @click="stationSheetVisible = true">
        <view class="station-main">
          <strong>中轮 - {{ state.station.name }}</strong>
          <em>提货 {{ stationPickupText }}</em>
          <view class="station-meta-row">
            <span class="station-meta-pill">提货人 <b>{{ receiverName }}</b></span>
            <span class="station-meta-pill">团点自提</span>
          </view>
        </view>
        <button class="station-switch" @click.stop="stationSheetVisible = true">切换</button>
      </view>
    </view>

    <view v-if="state.station.status !== 1" class="station-alert">
      <text>{{ state.station.abnormalReason || '当前自提点暂不可选，请重新选择自提点' }}</text>
      <button class="plain small" @click="stationSheetVisible = true">重新选择</button>
    </view>

    <view class="cat-grid">
      <view v-for="item in categoryCards" :key="item.id" class="cat-item" @click="openCategory(item.id)">
        <view class="cat-img" :style="backgroundImageStyle(item.imageUrl)" />
        <text class="cat-name">{{ item.label }}</text>
      </view>
      <view class="dot-page"><span></span><span></span></view>
    </view>

    <view v-if="activeBanner" class="hero-banner" :style="backgroundImageStyle(activeBanner.imageUrl)" @click="openHomeAsset(activeBanner)">
      <view class="banner-text">
        <span class="banner-kicker">{{ activeBanner.title }}</span>
        <b>{{ bannerMainTitle }}</b>
        <span>{{ bannerSubTitle }}</span>
      </view>
      <view class="banner-dots">
        <span class="active"></span>
        <span></span>
        <span></span>
      </view>
    </view>

    <view v-if="primaryPromo" class="promo-grid" :class="{ 'single-promo': secondaryPromoCards.length === 0 }">
      <view class="promo-card" @click="openHomeAsset(primaryPromo)">
        <b>{{ primaryPromo.title }} <span>{{ primaryPromo.actionText || '全部' }} ❯</span></b>
        <view class="promo-img" :style="backgroundImageStyle(primaryPromo.imageUrl)" />
      </view>
      <view v-if="secondaryPromoCards.length > 0">
        <view
          v-for="(item, index) in secondaryPromoCards"
          :key="item.activityCode || item.code"
          class="promo-card"
          :class="{ 'promo-gap': index > 0 }"
          @click="openHomeAsset(item)"
        >
          <b>{{ item.title }} <span>{{ item.actionText || '全部' }} ❯</span></b>
          <view class="promo-img" :style="backgroundImageStyle(item.imageUrl)" />
        </view>
      </view>
    </view>

    <view class="product-sort-card" data-sort-card="product">
      <b>推荐排序</b>
      <view>
        <button
          v-for="item in sortOptions"
          :key="item.value"
          :class="{ active: activeSort === item.value }"
          @click="activeSort = item.value"
        >
          {{ item.label }}
        </button>
      </view>
    </view>

    <view class="product-list">
      <view
        v-for="item in sortedProducts"
        :key="item.publishSkuId"
        class="shop-product"
        :class="{ soldout: isSoldout(item) }"
        @click="openProduct(item.productId)"
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
            <span v-if="Number(item.soldQty || 0) > 0" class="sold-count">已售{{ item.soldQty }}</span>
            <span class="sale-stock">
              <b>{{ isSoldout(item) ? '已售罄' : `剩余${item.availableQty}` }}</b>
            </span>
          </view>
        </view>
        <view class="shop-product-tag-row">
          <span class="shop-product-date">提货日期 {{ productPickupMonthDay(item.deliveryDate) }}</span>
          <button class="rank-line" @click.stop="openRank">鲜果榜排行第{{ productRank(item) }}名</button>
        </view>
        <button
          class="round-add"
          :aria-label="isSoldout(item) ? '已售罄' : '加入购物车'"
          @click.stop="isSoldout(item) ? toggleNotice(item.productId) : handleAddProduct(item)"
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
import { computed, onMounted, ref, watch } from 'vue';

import EmptyActionCard from '@/components/EmptyActionCard.vue';
import StationSheet from '@/components/StationSheet.vue';
import UserTabBar from '@/components/UserTabBar.vue';
import UserToast from '@/components/UserToast.vue';
import {
    getHomeAssets,
    getHomeSummary,
    pageCategories,
    pageProducts,
    type UserCategoryDTO,
    type UserHomeAssetDTO,
    type UserHomeBannerDTO,
    type UserHomePromoDTO,
    type UserProductCardDTO
} from '@/api/user';
import {
    addProductToCart,
    defaultReceiver,
    navigateUser,
    showUserToast,
    toggleFavorite,
    toggleNotice,
    useUserState
} from '@/stores/userState';
import {
    fallbackCategories,
    fallbackProductImages,
    fallbackProducts,
    isSoldout,
    productOldPrice,
    productPickupText,
    productSpecText,
    normalizeCategoryName,
    withProductImages
} from '@/utils/userFallbackData';

const state = useUserState();
const stationSheetVisible = ref(false);
const categories = ref<UserCategoryDTO[]>([]);
const products = ref<UserProductCardDTO[]>([]);
const homeAssets = ref<UserHomeAssetDTO | null>(null);
const activeSort = ref<'recommend' | 'sales' | 'new' | 'price'>('recommend');
let loadVersion = 0;
const sortOptions = [
    { value: 'recommend', label: '推荐' },
    { value: 'sales', label: '销量' },
    { value: 'new', label: '上新' },
    { value: 'price', label: '价格' }
] as const;

const receiverName = computed(() => defaultReceiver()?.name || state.user.nickname);
const categoryAssetMap = computed(() => {
    const map = new Map<number, string>();
    (homeAssets.value?.categories || []).forEach((item) => {
        if (item.categoryId && item.imageUrl) {
            map.set(Number(item.categoryId), item.imageUrl);
        }
    });
    return map;
});
const categoryCards = computed(() => {
    const allCategory = {
        ...fallbackCategories[0],
        imageUrl: categoryAssetMap.value.get(0) || fallbackCategories[0].imageUrl
    };
    const rows = categories.value.filter((item) => item.categoryName !== allCategory.label).slice(0, 9).map((item) => {
        return {
            id: item.id,
            categoryName: item.categoryName,
            label: normalizeCategoryName(item.categoryName),
            imageUrl: item.imageUrl || categoryAssetMap.value.get(Number(item.id)) || fallbackProductImages[0]
        };
    });
    if (rows.length > 0) {
        return [allCategory, ...rows].slice(0, 10);
    }
    const existingNames = new Set([allCategory.label, ...rows.map((item) => item.label)]);
    const filled = [
        allCategory,
        ...rows,
        ...fallbackCategories.slice(1).filter((item) => !existingNames.has(item.label) && !rows.some((row) => row.id === item.id))
    ];
    return filled.slice(0, 10);
});
const activeBanner = computed(() => {
    const rows = [...(homeAssets.value?.banners || [])]
        .filter((item) => item.imageUrl)
        .sort((a, b) => Number(a.sortNo || 0) - Number(b.sortNo || 0));
    return rows[0] || null;
});
const bannerMainTitle = computed(() => (activeBanner.value?.subTitle || '').replace(/\s+/g, '\n'));
const bannerSubTitle = computed(() => activeBanner.value?.description || '');
const promoCards = computed(() => {
    const rows = [...(homeAssets.value?.promos || [])]
        .filter((item) => item.imageUrl)
        .sort((a, b) => Number(a.sortNo || 0) - Number(b.sortNo || 0));
    return rows.slice(0, 3);
});
const primaryPromo = computed(() => promoCards.value[0] || null);
const secondaryPromoCards = computed(() => promoCards.value.slice(1, 3));
const sortedProducts = computed(() => {
    const rows = [...products.value].sort((a, b) => Number(b.availableQty > 0) - Number(a.availableQty > 0));
    if (activeSort.value === 'sales') {
        return rows.sort((a, b) => Number(b.availableQty > 0) - Number(a.availableQty > 0) || Number(b.soldQty || 0) - Number(a.soldQty || 0));
    }
    if (activeSort.value === 'new') {
        return rows.sort((a, b) => Number(b.availableQty > 0) - Number(a.availableQty > 0) || Number(b.publishSkuId || 0) - Number(a.publishSkuId || 0));
    }
    if (activeSort.value === 'price') {
        return rows.sort((a, b) => Number(b.availableQty > 0) - Number(a.availableQty > 0) || Number(a.salePrice || 0) - Number(b.salePrice || 0));
    }
    return rows;
});
const stationPickupText = computed(() => productPickupText(state.station.deliveryTime || '明日'));
const rankMap = computed(() => {
    const map = new Map<number, number>();
    sortedProducts.value.forEach((item, index) => {
        map.set(Number(item.publishSkuId), index + 1);
    });
    return map;
});

async function loadData() {
    const currentLoad = ++loadVersion;
    const queryContext = {
        userId: state.user.id,
        cityId: state.city.id,
        stationId: state.station.id
    };
    const isCurrentLoad = () => currentLoad === loadVersion;
    let hasRejected = false;

    try {
        const summary = await getHomeSummary(queryContext, { silent: true });
        if (!isCurrentLoad()) {
            return;
        }
        if (summary?.stationId && !state.stations.some((item) => item.id === state.station.id)) {
            state.station.id = summary.stationId;
        }
    } catch {
        hasRejected = true;
    }

    try {
        const assets = await getHomeAssets(
            {
                cityId: state.city.id,
                stationId: state.station.id
            },
            { silent: true }
        );
        if (!isCurrentLoad()) {
            return;
        }
        homeAssets.value = assets || null;
    } catch {
        hasRejected = true;
        homeAssets.value = null;
    }

    try {
        const categoryResult = await pageCategories({ pageNum: 1, pageSize: 10, status: 1 }, { silent: true });
        if (!isCurrentLoad()) {
            return;
        }
        categories.value = categoryResult?.list?.length ? categoryResult.list : [];
    } catch {
        hasRejected = true;
        categories.value = [];
    }

    try {
        const productResult = await pageProducts(
            {
                pageNum: 1,
                pageSize: 10,
                ...queryContext
            },
            { silent: true }
        );
        if (!isCurrentLoad()) {
            return;
        }
        products.value = productResult?.list?.length ? withProductImages(productResult.list) : fallbackProducts;
    } catch {
        hasRejected = true;
        products.value = fallbackProducts;
    }

    if (hasRejected) {
        console.info('用户端首页接口不可用，已展示本地兜底数据');
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
    if (categoryId === 0) {
        navigateUser('/pages/category/index', true);
        return;
    }
    navigateUser(`/pages/category/index?categoryId=${categoryId}`, true);
}

function openHomeAsset(asset?: UserHomeBannerDTO | UserHomePromoDTO) {
    if (!asset) {
        return;
    }
    const activityCode = asset.activityCode || asset.code;
    const linkUrl = asset.linkUrl || (activityCode ? `/pages/activity/index?activityCode=${encodeURIComponent(activityCode)}` : '');
    if (linkUrl) {
        navigateUser(linkUrl);
    }
}

function openRank() {
    navigateUser('/pages/rank/index');
}

function productRank(item: UserProductCardDTO) {
    return rankMap.value.get(Number(item.publishSkuId)) || 1;
}

function productPickupMonthDay(value?: string) {
    if (!value) {
        return '待定';
    }
    const matched = value.match(/(\d{4})-(\d{1,2})-(\d{1,2})/);
    if (matched) {
        return `${matched[2].padStart(2, '0')}-${matched[3].padStart(2, '0')}`;
    }
    const offsetMap: Record<string, number> = {
        今日: 0,
        今天: 0,
        明日: 1,
        明天: 1,
        后日: 2,
        后天: 2
    };
    const token = Object.keys(offsetMap).find((key) => value.includes(key));
    if (token) {
        const date = new Date();
        date.setDate(date.getDate() + offsetMap[token]);
        return `${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
    }
    const shortMatched = value.match(/(\d{1,2})[./-](\d{1,2})/);
    if (shortMatched) {
        return `${shortMatched[1].padStart(2, '0')}-${shortMatched[2].padStart(2, '0')}`;
    }
    return value.replace(/\s.*$/, '').slice(0, 5);
}

function backgroundImageStyle(url?: string) {
    return {
        backgroundImage: `url("${url || fallbackProductImages[0]}")`
    };
}

onMounted(() => {
    void loadData();
});

watch(
    () => [state.city.id, state.station.id],
    ([cityId, stationId], [previousCityId, previousStationId]) => {
        if (cityId !== previousCityId || stationId !== previousStationId) {
            void loadData();
        }
    }
);
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
  padding: 0 0 176rpx;
  background: #f3eadf;
}

.shop-header {
  min-height: 234rpx;
  padding: 12rpx 24rpx 20rpx;
  color: #ffffff;
  background: linear-gradient(135deg, #d94b34 0%, #f28a42 100%);
}

.shop-header-row {
  display: grid;
  grid-template-columns: 202rpx minmax(0, 1fr) 132rpx;
  gap: 16rpx;
  align-items: center;
}

.logo-mark {
  color: #ffffff;
  font-size: 44rpx;
  font-weight: 900;
  letter-spacing: 0;
  line-height: 1.05;
  white-space: nowrap;
}

.logo-mark small {
  display: block;
  margin-bottom: 4rpx;
  font-size: 26rpx;
  font-weight: 900;
  opacity: 1;
}

.shop-search {
  display: flex;
  align-items: center;
  gap: 14rpx;
  height: 76rpx;
  min-height: 76rpx;
  padding: 0 28rpx;
  color: #7b5f51;
  background: #ffffff;
  border-radius: 40rpx;
  box-shadow: 0 12rpx 28rpx rgba(111, 52, 20, 0.12);
  font-size: 26rpx;
  font-weight: 800;
}

.shop-search::before {
  content: "⌕";
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
}

.mini-pill {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 76rpx;
  color: #ffffff;
  background: rgba(135, 79, 38, 0.42);
  border-radius: 999rpx;
  font-size: 32rpx;
  font-weight: 900;
}

.station-bar {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: center;
  gap: 16rpx;
  min-height: 84rpx;
  margin-top: 18rpx;
  padding: 0 10rpx 0 32rpx;
  color: #1f2937;
  background: #ffffff;
  border-radius: 999rpx;
  box-shadow: 0 12rpx 28rpx rgba(111, 52, 20, 0.13);
}

.station-main {
  display: flex;
  align-items: center;
  gap: 10rpx;
  min-width: 0;
}

.station-main::before {
  content: "◆";
  flex: 0 0 auto;
  color: #172033;
  font-size: 25rpx;
}

.station-main strong {
  display: block;
  overflow: hidden;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.1;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.station-main em {
  display: none;
}

.station-switch {
  flex: 0 0 auto;
  min-width: 172rpx;
  min-height: 64rpx;
  padding: 0 20rpx;
  color: #c2412d !important;
  background: #fff7f1 !important;
  border: 1rpx solid #e85d3f;
  border-radius: 999rpx;
  box-shadow: none;
  font-size: 26rpx;
  font-weight: 900;
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
  font-size: 26rpx;
}

.cat-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 24rpx 12rpx;
  margin: 20rpx 24rpx;
  padding: 28rpx 16rpx 24rpx;
  background: #ffffff;
  border: 0;
  border-radius: 28rpx;
  box-shadow: none;
}

.cat-item {
  display: grid;
  justify-items: center;
  gap: 12rpx;
  min-width: 0;
  color: #263043;
  font-size: 25rpx;
  font-weight: 800;
  line-height: 1.2;
  text-align: center;
}

.cat-name {
  overflow: hidden;
  max-width: 100%;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cat-img {
  width: 96rpx;
  height: 96rpx;
  background-size: cover;
  background-position: center;
  border-radius: 28rpx;
  box-shadow: 0 8rpx 24rpx rgba(38, 48, 67, 0.12);
}

.dot-page {
  grid-column: 1 / -1;
  text-align: center;
  line-height: 16rpx;
}

.dot-page span {
  display: inline-block;
  width: 14rpx;
  height: 14rpx;
  margin: 0 6rpx;
  background: #d7d7d7;
  border-radius: 8rpx;
}

.dot-page span:first-child {
  width: 40rpx;
  background: #e85d3f;
}

.hero-banner {
  position: relative;
  overflow: hidden;
  height: 308rpx;
  margin: 20rpx 24rpx;
  background-size: cover;
  background-position: center;
  border-radius: 28rpx;
  box-shadow: 0 20rpx 44rpx rgba(38, 48, 67, 0.16);
}

.hero-banner::after {
  position: absolute;
  inset: 0;
  content: "";
  background: linear-gradient(90deg, rgba(23, 32, 51, 0.72), rgba(23, 32, 51, 0.18) 55%, rgba(217, 75, 52, 0.18));
}

.banner-text {
  position: absolute;
  top: 36rpx;
  left: 32rpx;
  z-index: 2;
  display: grid;
  gap: 8rpx;
  color: #ffffff;
  text-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.35);
}

.banner-kicker {
  width: fit-content;
  padding: 6rpx 16rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 999rpx;
  font-size: 25rpx;
  font-weight: 800;
}

.banner-text b {
  display: block;
  margin: 8rpx 0;
  font-size: 52rpx;
  font-weight: 900;
  line-height: 1.12;
  white-space: pre-line;
}

.banner-text span:last-child {
  max-width: 430rpx;
  font-size: 26rpx;
  font-weight: 800;
  line-height: 1.35;
}

.banner-dots {
  position: absolute;
  left: 32rpx;
  bottom: 24rpx;
  z-index: 2;
  display: flex;
  gap: 10rpx;
}

.banner-dots span {
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
  gap: 20rpx;
  margin: 20rpx 24rpx;
}

.promo-card {
  position: relative;
  overflow: hidden;
  min-height: 280rpx;
  padding: 16rpx;
  background: #ffffff;
  border: 0;
  border-radius: 20rpx;
}

.promo-card b {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.2;
}

.promo-card b span {
  color: #e85d3f;
  font-size: 25rpx;
  font-weight: 900;
}

.promo-img {
  width: 100%;
  height: 180rpx;
  margin-top: 12rpx;
  background-size: cover;
  background-position: center;
  border-radius: 16rpx;
}

.promo-gap {
  margin-top: 20rpx;
}

.product-sort-card {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  gap: 18rpx;
  align-items: center;
  margin: 16rpx 24rpx 14rpx;
  padding: 18rpx 20rpx;
  background: #ffffff;
  border: 0;
  border-radius: 18rpx;
}

.product-sort-card b {
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
}

.product-sort-card view {
  display: flex;
  gap: 12rpx;
  overflow-x: auto;
  white-space: nowrap;
  scrollbar-width: none;
}

.product-sort-card view::-webkit-scrollbar {
  display: none;
}

.product-sort-card button {
  flex: 0 0 auto;
  min-height: 56rpx;
  padding: 0 22rpx;
  color: #263043 !important;
  background: #fffaf6 !important;
  border: 1rpx solid #f0dfd6;
  border-radius: 999rpx;
  box-shadow: none;
  font-size: 26rpx;
  font-weight: 900;
}

.product-sort-card button.active {
  color: #ffffff !important;
  background: #e85d3f !important;
  border-color: #e85d3f;
}

.product-list {
  display: grid;
  gap: 12rpx;
  margin: 0;
  padding: 0 20rpx 20rpx;
}

.shop-product {
  position: relative;
  display: grid;
  grid-template-columns: 192rpx minmax(0, 1fr);
  gap: 16rpx;
  align-items: start;
  min-height: 220rpx;
  margin: 0;
  padding: 16rpx 96rpx 16rpx 16rpx;
  overflow: hidden;
  background: #ffffff;
  border: 0;
  border-radius: 6rpx;
  box-shadow: 0 4rpx 16rpx rgba(23, 36, 32, 0.05);
}

.shop-product.soldout {
  background: #fbf7f3;
  opacity: 0.72;
}

.shop-product-media {
  position: relative;
  display: grid;
  width: 192rpx;
  gap: 12rpx;
  align-self: stretch;
  align-content: start;
}

.shop-product-img {
  width: 192rpx;
  height: 192rpx;
  background-size: cover;
  background-position: center;
  border-radius: 16rpx;
  background-color: #f7f1ea;
}

.shop-product.soldout .shop-product-img {
  filter: grayscale(1) saturate(0.45) brightness(0.92);
}

.shop-product.soldout .shop-product-media::after {
  position: absolute;
  top: 96rpx;
  left: 96rpx;
  z-index: 2;
  min-width: 112rpx;
  padding: 10rpx 16rpx;
  color: #ffffff;
  content: "已售罄";
  background: rgba(121, 76, 69, 0.88);
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 900;
  line-height: 1;
  text-align: center;
  transform: translate(-50%, -50%);
}

.product-fav-btn {
  position: absolute;
  top: 16rpx;
  right: 16rpx;
  z-index: 3;
  min-width: 68rpx;
  min-height: 52rpx;
  padding: 0 14rpx;
  color: #1f8a70 !important;
  background: #ffffff !important;
  border: 1rpx solid rgba(31, 138, 112, 0.24);
  border-radius: 999rpx;
  box-shadow: 0 8rpx 24rpx rgba(23, 36, 32, 0.08);
  font-size: 26rpx;
  font-weight: 900;
}

.product-fav-btn.active {
  color: #ffffff !important;
  background: #1f8a70 !important;
  border-color: #1f8a70;
}

.shop-product-info {
  min-width: 0;
  padding: 0;
}

.shop-product-info h4 {
  display: -webkit-box;
  margin: 0 0 6rpx;
  overflow: hidden;
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.28;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.shop-product-spec {
  margin: 0 0 10rpx;
  overflow: hidden;
  color: #5c665f;
  font-size: 25rpx;
  line-height: 1.25;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8rpx;
  min-width: 0;
  white-space: nowrap;
}

.shop-price {
  color: #e60012;
  font-size: 40rpx;
  font-weight: 900;
  line-height: 1;
}

.old-price {
  color: #999999;
  font-size: 26rpx;
  text-decoration: line-through;
}

.shop-sale-meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, max-content));
  gap: 8rpx 12rpx;
  align-items: center;
  margin-top: 8rpx;
  color: #5e6a60;
  font-size: 25rpx;
  font-weight: 700;
  line-height: 1.25;
}

.shop-sale-meta span {
  display: inline-flex;
  align-items: center;
  min-height: 32rpx;
  padding: 2rpx 8rpx;
  white-space: nowrap;
  background: #f8faf7;
  border: 1rpx solid #e8eee8;
  border-radius: 999rpx;
}

.shop-sale-meta .sale-stock {
  gap: 10rpx;
  padding: 0;
  background: transparent;
  border: 0;
}

.shop-sale-meta .sale-stock b {
  display: inline-flex;
  align-items: center;
  min-height: 38rpx;
  padding: 4rpx 12rpx;
  color: #526257;
  background: #f7faf6;
  border: 1rpx solid #e4ece4;
  border-radius: 999rpx;
  font-size: 25rpx;
  font-weight: 800;
  white-space: nowrap;
}

.shop-product-tag-row {
  grid-column: 1 / -1;
  display: flex;
  align-items: center;
  gap: 20rpx;
  min-width: 0;
  margin-top: -4rpx;
}

.shop-product-date {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: fit-content;
  min-height: 44rpx;
  padding: 0 16rpx;
  color: #6f5736;
  background: #fbfaf6;
  border: 1rpx solid #ece6da;
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 800;
  white-space: nowrap;
}

.rank-line {
  display: inline-flex;
  flex: 0 1 auto;
  align-items: center;
  width: fit-content;
  max-width: 100%;
  min-height: 44rpx;
  padding: 6rpx 16rpx;
  overflow: hidden;
  color: #516173 !important;
  background: #f7f9fb !important;
  border: 1rpx solid #e6ebf1;
  border-radius: 14rpx;
  box-shadow: none;
  font-size: 25rpx;
  font-weight: 800;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.round-add {
  position: absolute;
  right: 20rpx;
  bottom: 24rpx;
  z-index: 2;
  width: 68rpx;
  min-width: 68rpx;
  height: 68rpx;
  min-height: 68rpx;
  padding: 0;
  color: #ffffff !important;
  background: #e85d3f !important;
  border: 0;
  border-radius: 50%;
  box-shadow: 0 12rpx 32rpx rgba(232, 93, 63, 0.34);
}

.shop-product.soldout .round-add {
  background: #c8c0ba !important;
  box-shadow: none;
}

.round-add svg {
  width: 44rpx;
  height: 44rpx;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 2.6;
}

.status-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 30rpx;
  margin-bottom: 4rpx;
  color: #ffffff;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1;
}

.home-page {
  padding: 0 0 calc(144rpx + env(safe-area-inset-bottom));
  background: #f7f1ea;
}

.shop-header {
  min-height: 266rpx;
  padding: 24rpx 24rpx 20rpx;
  background: linear-gradient(135deg, #d94b34 0%, #f28a42 100%);
  border-radius: 0;
  box-shadow: none;
}

.shop-header-row {
  grid-template-columns: 196rpx minmax(0, 1fr) 128rpx;
  gap: 20rpx;
}

.logo-mark {
  font-size: 40rpx;
  line-height: 1.02;
  white-space: nowrap;
}

.logo-mark small {
  margin-bottom: 4rpx;
  font-size: 26rpx;
  line-height: 1;
}

.shop-search {
  height: 76rpx;
  min-height: 76rpx;
  padding: 0 24rpx;
  border-radius: 999rpx;
  color: #2d241f;
  font-size: 26rpx;
}

.mini-pill {
  width: 128rpx;
  height: 68rpx;
  min-height: 68rpx;
  padding: 0;
  background: rgba(127, 78, 40, 0.45);
  font-size: 34rpx;
}

.station-bar {
  min-height: 112rpx;
  margin-top: 20rpx;
  padding: 18rpx 16rpx 18rpx 28rpx;
  border: 1rpx solid rgba(240, 223, 214, 0.9);
  border-radius: 32rpx;
  box-shadow: 0 8rpx 18rpx rgba(111, 52, 20, 0.08);
}

.station-main {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  gap: 4rpx 10rpx;
  min-width: 0;
}

.station-main::before {
  grid-row: 1 / 3;
  content: "◆";
  color: #172033;
  font-size: 25rpx;
  line-height: 34rpx;
}

.station-main strong {
  position: relative;
  display: block;
  padding-left: 0;
  overflow: hidden;
  color: #172033;
  font-size: 28rpx;
  line-height: 1.18;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.station-main strong::before {
  content: none;
}

.station-main em {
  display: block;
  overflow: hidden;
  color: #7b5f51;
  font-size: 25rpx;
  font-style: normal;
  font-weight: 800;
  line-height: 1.2;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.station-meta-row {
  grid-column: 1 / -1;
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
  padding-left: 28rpx;
  margin-top: 8rpx;
}

.station-meta-pill {
  display: inline-flex;
  align-items: center;
  max-width: 100%;
  min-height: 34rpx;
  padding: 0 12rpx;
  overflow: hidden;
  color: #7b5f51;
  background: #fbfaf6;
  border: 1rpx solid #ece6da;
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 800;
  line-height: 1;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.station-meta-pill b {
  min-width: 0;
  overflow: hidden;
  color: #172033;
  text-overflow: ellipsis;
}

.station-switch {
  min-width: 168rpx;
  min-height: 64rpx;
  color: #e85d3f !important;
  background: #fff6ef !important;
  border-color: #ef6a45;
  border-radius: 999rpx !important;
  font-size: 26rpx;
}

.cat-grid {
  gap: 24rpx 12rpx;
  margin: 16rpx 24rpx 20rpx;
  padding: 28rpx 16rpx 24rpx;
  border-radius: 28rpx;
  box-shadow: none;
}

.cat-item {
  min-height: 116rpx;
  gap: 12rpx;
  font-size: 25rpx;
  font-weight: 900;
  align-content: start;
}

.cat-img {
  width: 96rpx;
  height: 96rpx;
  border-radius: 28rpx;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.10);
}

.dot-page {
  height: 12rpx;
  margin-top: -6rpx;
  line-height: 12rpx;
}

.dot-page span {
  width: 36rpx;
  height: 12rpx;
}

.dot-page span:first-child {
  width: 48rpx;
}

.hero-banner {
  height: 308rpx;
  min-height: 308rpx;
  margin: 0 24rpx 20rpx;
  border-radius: 28rpx;
  box-shadow: 0 16rpx 36rpx rgba(126, 76, 49, 0.10);
}

.hero-banner::after {
  background: linear-gradient(90deg, rgba(23, 32, 51, 0.70), rgba(23, 32, 51, 0.10));
}

.banner-text {
  top: 46rpx;
  left: 36rpx;
  gap: 12rpx;
}

.banner-kicker {
  padding: 10rpx 20rpx;
  font-size: 30rpx;
}

.banner-text b {
  margin: 4rpx 0;
  font-size: 56rpx;
  line-height: 1.08;
}

.banner-text span:last-child {
  max-width: 560rpx;
  font-size: 30rpx;
}

.banner-dots {
  left: 36rpx;
  bottom: 24rpx;
}

.promo-grid {
  grid-template-columns: 1fr 1fr;
  gap: 20rpx;
  margin: 0 24rpx 20rpx;
}

.promo-grid > view {
  display: grid;
  gap: 20rpx;
  min-width: 0;
}

.promo-card {
  height: 284rpx;
  min-height: 284rpx;
  padding: 0;
  border-radius: 24rpx;
  box-shadow: 0 16rpx 36rpx rgba(126, 76, 49, 0.08);
}

.promo-grid > view .promo-card {
  height: 132rpx;
  min-height: 132rpx;
}

.promo-card b {
  position: absolute;
  z-index: 2;
  top: 20rpx;
  left: 20rpx;
  right: 20rpx;
  font-size: 36rpx;
  text-shadow: 0 2rpx 0 rgba(255, 255, 255, 0.55);
}

.promo-grid > view .promo-card b {
  font-size: 30rpx;
}

.promo-img {
  position: absolute;
  inset: 0;
  height: 62rpx;
  width: 100%;
  height: 100%;
  margin-top: 0;
  border-radius: inherit;
}

.promo-gap {
  display: block;
  margin-top: 0;
}

.product-sort-card {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  gap: 18rpx;
  align-items: center;
  margin: 0 24rpx 16rpx;
  padding: 18rpx 20rpx;
  background: #ffffff;
  border: 0;
  border-radius: 18rpx;
  box-shadow: 0 10rpx 24rpx rgba(126, 76, 49, 0.06);
}

.product-sort-card b {
  font-size: 28rpx;
}

.product-sort-card > view {
  display: flex;
  gap: 12rpx;
  overflow-x: auto;
  white-space: nowrap;
  scrollbar-width: none;
}

.product-sort-card > view::-webkit-scrollbar {
  display: none;
}

.product-sort-card button {
  flex: 0 0 auto;
  min-height: 48rpx;
  padding: 0 20rpx;
  color: #e85d3f !important;
  background: #fff7f1 !important;
  border-color: #f2d6c4;
  border-radius: 999rpx !important;
  box-shadow: none;
  font-size: 26rpx;
  font-weight: 900;
}

.product-sort-card button.active {
  background: #172033 !important;
  border-color: #172033;
}

.product-list {
  gap: 12rpx;
  padding: 0 24rpx calc(132rpx + env(safe-area-inset-bottom));
}

.shop-product {
  grid-template-columns: 192rpx minmax(0, 1fr);
  grid-template-rows: auto auto;
  column-gap: 20rpx;
  row-gap: 10rpx;
  min-height: 248rpx;
  padding: 20rpx 104rpx 22rpx 20rpx;
  border: 1rpx solid #edf1ed;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(23, 36, 32, 0.05);
}

.shop-product-media {
  width: 192rpx;
  height: 192rpx;
  grid-row: 1 / 2;
  border-radius: 16rpx;
  overflow: hidden;
}

.shop-product-img {
  width: 192rpx;
  height: 192rpx;
  border-radius: 16rpx;
}

.shop-product.soldout .shop-product-media::after {
  top: 98rpx;
  left: 96rpx;
}

.product-fav-btn {
  top: 18rpx;
  right: 18rpx;
  min-width: 64rpx;
  min-height: 44rpx;
  padding: 0 12rpx;
  color: #7b5f51 !important;
  background: rgba(255, 255, 255, 0.88) !important;
  border-color: rgba(126, 76, 49, 0.14);
  box-shadow: none;
  font-size: 25rpx;
}

.shop-product-info h4 {
  display: -webkit-box;
  margin: 0 0 6rpx;
  overflow: hidden;
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.28;
  white-space: normal;
  word-break: break-all;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.shop-product-spec {
  display: block;
  margin: 0 0 8rpx;
  overflow: hidden;
  color: #5c665f;
  font-size: 25rpx;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.shop-price {
  font-size: 40rpx;
}

.old-price {
  display: inline;
  color: #999999;
  font-size: 26rpx;
  text-decoration: line-through;
}

.shop-sale-meta {
  display: flex;
  flex-wrap: nowrap;
  gap: 10rpx;
  width: 100%;
  max-width: 286rpx;
  margin-top: 8rpx;
  overflow: hidden;
  font-size: 25rpx;
}

.shop-sale-meta span,
.shop-sale-meta .sale-stock b {
  min-width: 0;
  min-height: 34rpx;
  padding: 0 10rpx;
  color: #a94b2a;
  background: #fff2e8;
  border: 0;
  font-size: 25rpx;
  font-weight: 900;
  line-height: 1;
  text-overflow: clip;
}

.shop-product-tag-row {
  position: static;
  grid-column: 2 / 3;
  grid-row: 2 / 3;
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
  align-items: center;
  min-width: 0;
  min-height: 40rpx;
  padding-right: 0;
  margin: 0;
}

.shop-product-date,
.rank-line {
  max-width: 100%;
  min-height: 40rpx;
  height: 40rpx;
  padding: 0 12rpx;
  overflow: hidden;
  border: 0;
  border-radius: 12rpx;
  font-size: 26rpx;
  font-weight: 900;
  line-height: 40rpx;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.rank-line {
  display: inline-flex;
  justify-self: start;
  color: #1e71ad !important;
  background: #eaf4ff !important;
}

.round-add {
  right: 20rpx;
  bottom: 24rpx;
  width: 68rpx;
  min-width: 68rpx;
  height: 68rpx;
  min-height: 68rpx;
  border-radius: 50% !important;
  background: linear-gradient(135deg, #e95b43, #da4d35) !important;
  color: #ffffff !important;
  box-shadow: 0 16rpx 36rpx rgba(217, 75, 52, 0.26);
}

.round-add svg {
  width: 42rpx;
  height: 42rpx;
  stroke-width: 2.6;
}

.home-page {
  padding: 0 0 calc(132rpx + env(safe-area-inset-bottom));
  background: #f3eadf;
}

.shop-header {
  min-height: 244rpx;
  padding: 12rpx 24rpx 20rpx;
}

.status-bar {
  height: 36rpx;
  margin-bottom: 6rpx;
  font-size: 26rpx;
}

.shop-header-row {
  grid-template-columns: 136rpx minmax(0, 1fr) 68rpx;
  gap: 18rpx;
}

.logo-mark {
  font-size: 32rpx;
  line-height: 1.02;
  white-space: normal;
}

.logo-mark small {
  margin-bottom: 2rpx;
  font-size: 25rpx;
}

.shop-search {
  height: 64rpx;
  min-height: 64rpx;
  padding: 0 24rpx;
  color: #2d241f;
  border-radius: 40rpx;
  font-size: 26rpx;
}

.mini-pill {
  width: 68rpx;
  min-width: 68rpx;
  height: 64rpx;
  min-height: 64rpx;
  font-size: 26rpx;
}

.station-bar {
  min-height: 92rpx;
  margin-top: 18rpx;
  padding: 16rpx 14rpx 16rpx 26rpx;
  border-radius: 16rpx;
  box-shadow: 0 6rpx 0 rgba(0, 0, 0, 0.04);
}

.station-main {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  gap: 4rpx 10rpx;
}

.station-main::before {
  grid-row: 1 / 3;
  content: "◆";
  font-size: 16rpx;
  line-height: 30rpx;
}

.station-main strong {
  font-size: 26rpx;
}

.station-main em {
  display: block;
  color: #8b6a57;
  font-size: 25rpx;
  font-style: normal;
  font-weight: 800;
  line-height: 1.2;
}

.station-meta-row,
.station-meta-pill {
  display: none;
}

.station-switch {
  min-width: 148rpx;
  min-height: 64rpx;
  padding: 0 20rpx;
  color: #c2412d !important;
  background: #ffffff !important;
  border: 1rpx solid #e85d3f;
  border-radius: 12rpx !important;
  font-size: 26rpx;
}

.cat-grid {
  gap: 24rpx 12rpx;
  margin: 20rpx 24rpx;
  padding: 28rpx 16rpx 24rpx;
  border-radius: 28rpx;
}

.cat-item {
  min-height: 120rpx;
  gap: 0;
  font-size: 25rpx;
}

.cat-img {
  width: 96rpx;
  height: 96rpx;
  margin: 0 auto 12rpx;
  border-radius: 28rpx;
}

.hero-banner {
  height: 308rpx;
  min-height: 308rpx;
  margin: 20rpx 24rpx;
  border-radius: 28rpx;
}

.banner-text {
  top: 36rpx;
  left: 32rpx;
  gap: 8rpx;
}

.banner-kicker {
  padding: 6rpx 16rpx;
  font-size: 26rpx;
}

.banner-text b {
  margin: 8rpx 0;
  font-size: 52rpx;
}

.banner-text span:last-child {
  max-width: 480rpx;
  font-size: 26rpx;
}

.promo-grid {
  gap: 20rpx;
  margin: 20rpx 24rpx;
}

.promo-grid > view {
  gap: 20rpx;
}

.promo-card {
  min-height: 280rpx;
  height: 280rpx;
  padding: 16rpx;
  border-radius: 20rpx;
}

.promo-grid > view .promo-card {
  min-height: 130rpx;
  height: 130rpx;
}

.promo-card b {
  top: 16rpx;
  left: 16rpx;
  right: 16rpx;
  font-size: 34rpx;
}

.promo-grid > view .promo-card b {
  font-size: 28rpx;
}

.promo-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  margin: 0;
  background-size: cover;
  background-position: center;
  border-radius: inherit;
}

.promo-card::after {
  position: absolute;
  inset: 0;
  content: "";
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.46), rgba(255, 255, 255, 0.06));
}

.promo-card b {
  z-index: 2;
}

.product-sort-card {
  margin: 20rpx 24rpx 16rpx;
  padding: 18rpx 20rpx;
  border-radius: 18rpx;
}

.product-list {
  gap: 16rpx;
  padding: 0 20rpx calc(148rpx + env(safe-area-inset-bottom));
}

.shop-product {
  grid-template-columns: 220rpx minmax(0, 1fr);
  gap: 20rpx;
  min-height: 248rpx;
  padding: 18rpx 104rpx 20rpx 18rpx;
  border: 1rpx solid #edf1ed;
  border-radius: 16rpx;
}

.shop-product-media {
  width: 220rpx;
  height: 192rpx;
}

.shop-product-img {
  width: 192rpx;
  height: 192rpx;
}

.shop-product-info h4 {
  font-size: 28rpx;
  line-height: 1.28;
}

.shop-product-spec {
  font-size: 25rpx;
}

.shop-sale-meta {
  max-width: 100%;
}

.shop-product-tag-row {
  grid-column: 1 / -1;
  grid-row: auto;
  margin-top: -2rpx;
}

.round-add {
  right: 20rpx;
  bottom: 24rpx;
}

/* Keep this final block close to the UI reference screenshot. */
.home-page {
  padding-bottom: calc(172rpx + env(safe-area-inset-bottom));
  background: #f7f1ea;
}

.shop-header {
  min-height: 300rpx;
  padding: 18rpx 20rpx 20rpx;
  border-radius: 0;
}

.status-bar {
  height: 48rpx;
  margin-bottom: 8rpx;
  padding: 0 64rpx 0 4rpx;
  font-size: 32rpx;
  line-height: 48rpx;
}

.shop-header-row {
  min-height: 88rpx;
  grid-template-columns: 208rpx minmax(0, 1fr) 128rpx;
  gap: 16rpx;
}

.logo-mark {
  font-size: 46rpx;
  font-weight: 950;
  line-height: 1.02;
  white-space: nowrap;
}

.logo-mark small {
  font-size: 30rpx;
  font-weight: 950;
}

.shop-search {
  height: 80rpx;
  min-height: 80rpx;
  padding: 0 30rpx;
  font-size: 28rpx;
}

.shop-search::before {
  content: "⌕";
  font-size: 32rpx;
}

.mini-pill {
  width: 128rpx;
  min-width: 128rpx;
  height: 80rpx;
  min-height: 80rpx;
  font-size: 0;
}

.mini-pill::before {
  content: "...  ⊙";
  font-size: 36rpx;
  font-weight: 950;
}

.station-bar {
  min-height: 84rpx;
  margin-top: 18rpx;
  padding: 0 10rpx 0 32rpx;
  border: 0;
  border-radius: 999rpx;
  box-shadow: 0 16rpx 36rpx rgba(111, 52, 20, 0.13);
}

.station-main {
  display: flex;
  gap: 14rpx;
}

.station-main::before {
  content: "◆";
  font-size: 26rpx;
  line-height: 1;
}

.station-main strong {
  font-size: 32rpx;
  font-weight: 950;
}

.station-main em {
  display: none;
}

.station-switch {
  min-width: 208rpx;
  min-height: 64rpx;
  padding: 0 24rpx;
  background: #fff7f1 !important;
  border-radius: 999rpx !important;
  font-size: 30rpx;
}

.cat-grid {
  gap: 28rpx 12rpx;
  margin: 20rpx 24rpx 24rpx;
  padding: 28rpx 16rpx 42rpx;
  border-radius: 14rpx;
  box-shadow: 0 16rpx 36rpx rgba(126, 76, 49, 0.08);
}

.cat-item {
  min-height: 164rpx;
  gap: 16rpx;
}

.cat-img {
  width: 100rpx;
  height: 100rpx;
  border-radius: 24rpx;
}

.cat-name {
  font-size: 28rpx;
  font-weight: 950;
  white-space: normal;
}

.dot-page {
  height: 12rpx;
  margin-top: -6rpx;
  display: flex;
  justify-content: center;
  gap: 16rpx;
}

.dot-page span,
.dot-page span:first-child {
  width: 36rpx;
  height: 12rpx;
}

.hero-banner {
  height: 308rpx;
  min-height: 308rpx;
  margin: 0 24rpx 24rpx;
  border-radius: 14rpx;
}

.banner-text {
  top: 0;
  left: 0;
  max-width: 78%;
  padding: 46rpx 36rpx;
}

.banner-kicker {
  font-size: 30rpx;
}

.banner-text b {
  font-size: 52rpx;
}

.banner-text span:last-child {
  font-size: 26rpx;
}

.promo-grid {
  gap: 16rpx;
  margin: 0 24rpx 24rpx;
}

.promo-grid > view {
  gap: 16rpx;
}

.promo-card {
  height: 244rpx;
  min-height: 244rpx;
  border-radius: 12rpx;
}

.promo-grid > view .promo-card {
  height: 112rpx;
  min-height: 112rpx;
}

.promo-card b {
  font-size: 32rpx;
}

.promo-grid > view .promo-card b {
  font-size: 30rpx;
}

.product-sort-card {
  display: none;
}

.product-list {
  gap: 16rpx;
  padding: 0 16rpx calc(172rpx + env(safe-area-inset-bottom));
}

.shop-product {
  grid-template-columns: 196rpx minmax(0, 1fr);
  grid-template-rows: auto auto;
  min-height: 232rpx;
  padding: 18rpx 104rpx 18rpx 18rpx;
  border: 1rpx solid rgba(126, 76, 49, 0.1);
  border-radius: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(23, 36, 32, 0.05);
}

.product-fav-btn {
  display: none;
}

.shop-product-media {
  grid-row: 1 / 3;
  width: 196rpx;
  height: 196rpx;
}

.shop-product-img {
  width: 196rpx;
  height: 196rpx;
  border-radius: 16rpx;
}

.shop-product-info h4 {
  margin-bottom: 8rpx;
  font-size: 28rpx;
  line-height: 1.28;
  -webkit-line-clamp: 2;
}

.shop-product-spec {
  display: block;
  margin-bottom: 10rpx;
  color: #5c665f;
  font-size: 25rpx;
  line-height: 1.25;
}

.old-price {
  display: inline;
  font-size: 25rpx;
}

.shop-price {
  font-size: 40rpx;
}

.shop-sale-meta {
  max-width: 256rpx;
  margin-top: 10rpx;
  flex-wrap: nowrap;
}

.shop-sale-meta .sold-count,
.shop-sale-meta .sale-stock {
  height: 38rpx;
  min-height: 38rpx;
  padding: 0 12rpx;
  color: #a94b2a;
  background: #fff2e8;
  border: 0;
  font-size: 25rpx;
  font-weight: 950;
}

.shop-sale-meta .sale-stock b {
  min-height: 0;
  padding: 0;
  color: inherit;
  background: transparent;
  border: 0;
  font-size: inherit;
  font-weight: inherit;
}

.shop-product-tag-row {
  grid-column: 2 / 3;
  margin: 0;
  gap: 12rpx;
}

.shop-product-date,
.rank-line {
  height: 44rpx;
  min-height: 44rpx;
  padding: 0 16rpx;
  border: 0;
  border-radius: 14rpx;
  font-size: 25rpx;
  font-weight: 950;
  line-height: 44rpx;
}

.shop-product-date {
  color: #a66210;
  background: #fff1d8;
}

.rank-line {
  color: #1e71ad !important;
  background: #eaf4ff !important;
}

.round-add {
  right: 24rpx;
  bottom: 24rpx;
  width: 68rpx;
  min-width: 68rpx;
  height: 68rpx;
  min-height: 68rpx;
}

.round-add svg {
  width: 44rpx;
  height: 44rpx;
}

/* Final reference lock: match design/商城项目UI设计.html user .shop-* prototype. */
.home-page.shop-page {
  width: min(100%, 390px);
  min-height: 100vh;
  padding: 0 0 calc(140rpx + env(safe-area-inset-bottom));
  background: #f7f1ea;
}

.home-page .shop-header {
  min-height: 266rpx;
  padding: 18rpx 28rpx 24rpx;
  color: #ffffff;
  background: linear-gradient(135deg, #ee4d2d 0%, #f47738 100%);
  border-radius: 0;
  box-shadow: none;
}

.home-page .status-bar {
  height: 44rpx;
  margin: 0 0 8rpx;
  padding: 0 4rpx;
  color: #ffffff;
  font-size: 26rpx;
  font-weight: 900;
  line-height: 44rpx;
}

.home-page .shop-header-row {
  display: grid;
  grid-template-columns: 128rpx minmax(0, 1fr) 72rpx;
  gap: 16rpx;
  align-items: center;
  min-height: 72rpx;
}

.home-page .logo-mark {
  color: #ffffff;
  font-size: 32rpx;
  font-weight: 950;
  line-height: 1.02;
  white-space: nowrap;
}

.home-page .logo-mark small {
  display: block;
  margin: 0 0 2rpx;
  font-size: 26rpx;
  font-weight: 950;
}

.home-page .shop-search {
  height: 64rpx;
  min-height: 64rpx;
  padding: 0 24rpx;
  color: #172033;
  background: #ffffff;
  border-radius: 999rpx;
  box-shadow: none;
  font-size: 26rpx;
  font-weight: 900;
}

.home-page .shop-search::before {
  content: "⌕";
  color: #172033;
  font-size: 26rpx;
}

.home-page .mini-pill {
  width: 72rpx;
  min-width: 72rpx;
  height: 64rpx;
  min-height: 64rpx;
  padding: 0;
  color: #ffffff;
  background: rgba(127, 78, 40, 0.45);
  border-radius: 999rpx;
  font-size: 0;
}

.home-page .mini-pill::before {
  content: "... ◎";
  font-size: 26rpx;
  font-weight: 950;
}

.home-page .station-bar {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: center;
  gap: 14rpx;
  min-height: 78rpx;
  margin-top: 18rpx;
  padding: 0 10rpx 0 28rpx;
  color: #172033;
  background: #ffffff;
  border: 0;
  border-radius: 999rpx;
  box-shadow: 0 12rpx 26rpx rgba(111, 52, 20, 0.13);
}

.home-page .station-main {
  display: flex;
  align-items: center;
  gap: 10rpx;
  min-width: 0;
}

.home-page .station-main::before {
  content: "◆";
  flex: 0 0 auto;
  color: #172033;
  font-size: 25rpx;
  line-height: 1;
}

.home-page .station-main strong {
  min-width: 0;
  overflow: hidden;
  color: #172033;
  font-size: 28rpx;
  font-weight: 950;
  line-height: 1.1;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.home-page .station-main em {
  display: none;
}

.home-page .station-switch {
  min-width: 120rpx;
  min-height: 58rpx;
  padding: 0 18rpx;
  color: #d94b34 !important;
  background: #fff7f1 !important;
  border: 1rpx solid #ef6a45;
  border-radius: 999rpx !important;
  box-shadow: none;
  font-size: 26rpx;
  font-weight: 950;
}

.home-page .cat-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 24rpx 12rpx;
  margin: 20rpx 28rpx 24rpx;
  padding: 28rpx 16rpx 42rpx;
  background: #ffffff;
  border: 0;
  border-radius: 14rpx;
  box-shadow: 0 16rpx 36rpx rgba(126, 76, 49, 0.08);
}

.home-page .cat-item {
  display: grid;
  justify-items: center;
  align-content: start;
  gap: 14rpx;
  min-height: 164rpx;
  min-width: 0;
  color: #172033;
  text-align: center;
}

.home-page .cat-img {
  width: 100rpx;
  height: 100rpx;
  background-position: center;
  background-size: cover;
  border-radius: 24rpx;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.10);
}

.home-page .cat-name {
  max-width: 132rpx;
  overflow: hidden;
  color: #172033;
  font-size: 26rpx;
  font-weight: 950;
  line-height: 1.12;
  text-overflow: ellipsis;
  white-space: normal;
}

.home-page .dot-page {
  grid-column: 1 / -1;
  display: flex;
  justify-content: center;
  gap: 16rpx;
  height: 12rpx;
  margin-top: -6rpx;
  line-height: 12rpx;
}

.home-page .dot-page span,
.home-page .dot-page span:first-child {
  width: 36rpx;
  height: 12rpx;
  margin: 0;
  background: #d2d2d2;
  border-radius: 999rpx;
}

.home-page .dot-page span:first-child {
  background: #e76545;
}

.home-page .hero-banner {
  position: relative;
  height: 308rpx;
  min-height: 308rpx;
  margin: 0 28rpx 24rpx;
  overflow: hidden;
  background-position: center;
  background-size: cover;
  border: 0;
  border-radius: 14rpx;
  box-shadow: 0 16rpx 36rpx rgba(126, 76, 49, 0.10);
}

.home-page .hero-banner::after {
  position: absolute;
  inset: 0;
  content: "";
  background: linear-gradient(90deg, rgba(23, 32, 51, 0.70), rgba(23, 32, 51, 0.10));
}

.home-page .banner-text {
  position: absolute;
  inset: 0 auto auto 0;
  z-index: 2;
  display: grid;
  max-width: 78%;
  gap: 8rpx;
  padding: 46rpx 36rpx;
  color: #ffffff;
  text-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.35);
}

.home-page .banner-kicker {
  width: fit-content;
  padding: 10rpx 20rpx;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.18);
  border-radius: 999rpx;
  font-size: 28rpx;
  font-weight: 950;
  line-height: 1;
}

.home-page .banner-text b {
  display: block;
  margin: 8rpx 0 0;
  color: #ffffff;
  font-size: 54rpx;
  font-weight: 950;
  line-height: 1.08;
  letter-spacing: 0;
}

.home-page .banner-text span:last-child {
  max-width: 480rpx;
  color: #ffffff;
  font-size: 26rpx;
  font-weight: 850;
  line-height: 1.25;
}

.home-page .banner-dots {
  position: absolute;
  left: 36rpx;
  bottom: 24rpx;
  z-index: 2;
  display: flex;
  gap: 10rpx;
}

.home-page .banner-dots span {
  width: 32rpx;
  height: 10rpx;
  background: rgba(255, 255, 255, 0.55);
  border-radius: 999rpx;
}

.home-page .banner-dots span.active {
  width: 48rpx;
  background: #ffffff;
}

.home-page .promo-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20rpx;
  margin: 0 28rpx 20rpx;
}

.home-page .promo-grid > view {
  display: grid;
  gap: 20rpx;
  min-width: 0;
}

.home-page .promo-card {
  position: relative;
  display: block;
  height: 284rpx;
  min-height: 284rpx;
  padding: 0;
  overflow: hidden;
  background: #ffffff;
  border: 0;
  border-radius: 12rpx;
  box-shadow: 0 16rpx 36rpx rgba(126, 76, 49, 0.08);
}

.home-page .promo-grid > view .promo-card {
  height: 132rpx;
  min-height: 132rpx;
}

.home-page .promo-card::after {
  position: absolute;
  inset: 0;
  content: "";
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.46), rgba(255, 255, 255, 0.06));
}

.home-page .promo-card b {
  position: absolute;
  top: 20rpx;
  right: 20rpx;
  left: 20rpx;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #172033;
  font-size: 32rpx;
  font-weight: 950;
  line-height: 1.1;
  text-shadow: 0 2rpx 0 rgba(255, 255, 255, 0.55);
}

.home-page .promo-grid > view .promo-card b {
  font-size: 28rpx;
}

.home-page .promo-card b span {
  color: #df583a;
  font-size: 25rpx;
  font-weight: 950;
  text-shadow: none;
}

.home-page .promo-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  margin: 0;
  background-position: center;
  background-size: cover;
  border-radius: inherit;
}

.home-page .product-sort-card {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  gap: 18rpx;
  align-items: center;
  margin: 0 28rpx 16rpx;
  padding: 18rpx 20rpx;
  background: #ffffff;
  border: 0;
  border-radius: 14rpx;
  box-shadow: 0 10rpx 24rpx rgba(126, 76, 49, 0.06);
}

.home-page .product-sort-card b {
  color: #172033;
  font-size: 28rpx;
  font-weight: 950;
}

.home-page .product-sort-card > view {
  display: flex;
  gap: 12rpx;
  overflow-x: auto;
  white-space: nowrap;
  scrollbar-width: none;
}

.home-page .product-sort-card > view::-webkit-scrollbar {
  display: none;
}

.home-page .product-sort-card button {
  flex: 0 0 auto;
  min-height: 44rpx;
  padding: 0 18rpx;
  color: #d94b34 !important;
  background: #fff7f1 !important;
  border: 1rpx solid #f2d6c4;
  border-radius: 999rpx !important;
  box-shadow: none;
  font-size: 25rpx;
  font-weight: 950;
}

.home-page .product-sort-card button.active {
  color: #ffffff !important;
  background: #172033 !important;
  border-color: #172033;
}

.home-page .product-list {
  display: grid;
  gap: 16rpx;
  margin: 0;
  padding: 0 16rpx calc(164rpx + env(safe-area-inset-bottom));
}

.home-page .shop-product {
  position: relative;
  display: grid;
  grid-template-columns: 196rpx minmax(0, 1fr);
  grid-template-rows: auto 48rpx;
  column-gap: 16rpx;
  row-gap: 10rpx;
  align-items: start;
  min-height: 264rpx;
  padding: 18rpx 112rpx 18rpx 18rpx;
  overflow: hidden;
  background: #ffffff;
  border: 1rpx solid rgba(126, 76, 49, 0.10);
  border-radius: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(23, 36, 32, 0.05);
}

.home-page .shop-product.soldout {
  background: #fbf7f3;
  opacity: 0.72;
}

.home-page .shop-product-media {
  grid-row: 1 / 2;
  width: 196rpx;
  height: 196rpx;
  overflow: hidden;
  border-radius: 16rpx;
}

.home-page .shop-product-img {
  width: 196rpx;
  height: 196rpx;
  background-color: #f7f1ea;
  background-position: center;
  background-size: cover;
  border-radius: 16rpx;
}

.home-page .product-fav-btn {
  display: none;
}

.home-page .shop-product-info {
  min-width: 0;
}

.home-page .shop-product-info h4 {
  display: block;
  margin: 0 0 6rpx;
  overflow: visible;
  color: #172033;
  font-size: 28rpx;
  font-weight: 950;
  line-height: 1.20;
  white-space: normal;
  word-break: break-all;
}

.home-page .shop-product-spec {
  margin: 0 0 8rpx;
  overflow: hidden;
  color: #5c665f;
  font-size: 25rpx;
  line-height: 1.25;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.home-page .price-row {
  display: flex;
  align-items: baseline;
  gap: 8rpx;
  min-width: 0;
}

.home-page .shop-price {
  color: #e60012;
  font-size: 40rpx;
  font-weight: 950;
  line-height: 1;
}

.home-page .old-price {
  display: none;
}

.home-page .shop-sale-meta {
  display: flex;
  flex-wrap: nowrap;
  gap: 8rpx;
  width: 100%;
  max-width: 256rpx;
  margin-top: 10rpx;
  overflow: hidden;
}

.home-page .shop-sale-meta .sold-count,
.home-page .shop-sale-meta .sale-stock {
  display: inline-flex;
  align-items: center;
  min-width: 0;
  height: 38rpx;
  min-height: 38rpx;
  padding: 0 12rpx;
  overflow: hidden;
  color: #a94b2a;
  background: #fff2e8;
  border: 0;
  border-radius: 999rpx;
  font-size: 25rpx;
  font-weight: 950;
  line-height: 38rpx;
  white-space: nowrap;
}

.home-page .shop-sale-meta .sale-stock b {
  min-height: 0;
  padding: 0;
  color: inherit;
  background: transparent;
  border: 0;
  font-size: inherit;
  font-weight: inherit;
}

.home-page .shop-product-tag-row {
  grid-column: 1 / 3;
  grid-row: 2 / 3;
  display: grid;
  grid-template-columns: 196rpx minmax(0, 1fr);
  align-items: center;
  gap: 16rpx;
  min-height: 44rpx;
  margin: 0;
  padding: 0 96rpx 0 0;
  overflow: hidden;
}

.home-page .shop-product-date,
.home-page .rank-line {
  justify-self: start;
  max-width: 100%;
  height: 44rpx;
  min-height: 44rpx;
  padding: 0 14rpx;
  overflow: hidden;
  border: 0;
  border-radius: 14rpx;
  box-shadow: none;
  font-size: 25rpx;
  font-weight: 950;
  line-height: 44rpx;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.home-page .shop-product-date {
  color: #a66210;
  background: #fff1d8;
}

.home-page .rank-line {
  color: #1e71ad !important;
  background: #eaf4ff !important;
}

.home-page .round-add {
  position: absolute;
  right: 24rpx;
  bottom: 60rpx;
  z-index: 3;
  width: 68rpx;
  min-width: 68rpx;
  height: 68rpx;
  min-height: 68rpx;
  padding: 0;
  color: #ffffff !important;
  background: linear-gradient(135deg, #e95b43, #da4d35) !important;
  border: 0;
  border-radius: 50% !important;
  box-shadow: 0 16rpx 36rpx rgba(217, 75, 52, 0.26);
}

.home-page .shop-product.soldout .round-add {
  background: #c8c0ba !important;
  box-shadow: none;
}

.home-page .round-add svg {
  width: 44rpx;
  height: 44rpx;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 2.6;
}

/* Design snapshot lock: latest user H5 home prototype overrides. */
.home-page.shop-page {
  padding-bottom: calc(172rpx + env(safe-area-inset-bottom));
  background: #f6efe7;
}

.home-page .shop-header {
  position: relative;
  min-height: 296rpx;
  padding: 36rpx 36rpx 116rpx;
  background: linear-gradient(135deg, #db4c32 0%, #ec8a3b 100%);
  border-radius: 0 0 44rpx 44rpx;
}

.home-page .status-bar {
  height: 44rpx;
  margin: 0;
  padding: 0 8rpx;
  font-size: 32rpx;
  line-height: 44rpx;
}

.home-page .shop-header-row {
  grid-template-columns: minmax(0, 1fr) 316rpx 128rpx;
  gap: 16rpx;
  min-height: 76rpx;
  margin-top: 16rpx;
}

.home-page .logo-mark {
  display: grid;
  min-width: 0;
  color: #ffffff;
}

.home-page .logo-mark small {
  font-size: 26rpx;
  line-height: 1.15;
}

.home-page .logo-mark text {
  color: #ffffff;
  font-size: 44rpx;
  font-weight: 950;
  line-height: 1.05;
  white-space: nowrap;
}

.home-page .shop-search {
  height: 76rpx;
  min-height: 76rpx;
  padding: 0 28rpx;
  color: #6f5a4b;
  background: #ffffff;
  border: 0;
  border-radius: 999rpx;
  box-shadow: 0 16rpx 36rpx rgba(111, 52, 20, 0.12);
  font-size: 26rpx;
}

.home-page .mini-pill {
  width: auto;
  min-width: 128rpx;
  height: 76rpx;
  min-height: 76rpx;
  padding: 0 22rpx;
  background: rgba(119, 72, 37, 0.38);
}

.home-page .mini-pill::before {
  content: "...  ◎";
  font-size: 36rpx;
  letter-spacing: 2rpx;
}

.home-page .station-bar {
  position: absolute;
  right: 36rpx;
  bottom: 20rpx;
  left: 36rpx;
  min-height: 84rpx;
  margin: 0;
  padding: 0 12rpx 0 28rpx;
}

.home-page .station-main {
  display: flex;
  gap: 10rpx;
}

.home-page .station-main::before {
  content: "📍";
  font-size: 26rpx;
}

.home-page .station-main strong {
  font-size: 28rpx;
}

.home-page .station-main em,
.home-page .station-meta-row {
  display: none;
}

.home-page .station-switch {
  min-width: 104rpx;
  min-height: 64rpx;
  padding: 0 24rpx;
  font-size: 26rpx;
}

.home-page .cat-grid {
  gap: 28rpx 12rpx;
  margin: 20rpx 28rpx 24rpx;
  padding: 28rpx 16rpx 40rpx;
  border-radius: 28rpx;
  box-shadow: 0 16rpx 36rpx rgba(126, 76, 49, 0.08);
}

.home-page .cat-item {
  gap: 14rpx;
  min-height: 156rpx;
}

.home-page .cat-img {
  width: 100rpx;
  height: 100rpx;
  border-radius: 24rpx;
}

.home-page .cat-name {
  max-width: 124rpx;
  font-size: 26rpx;
  line-height: 1.15;
}

.home-page .hero-banner {
  height: 304rpx;
  min-height: 304rpx;
  margin: 0 28rpx 24rpx;
  border-radius: 28rpx;
}

.home-page .banner-text {
  max-width: 76%;
  padding: 44rpx 36rpx;
}

.home-page .banner-kicker {
  padding: 8rpx 18rpx;
  font-size: 26rpx;
}

.home-page .banner-text b {
  font-size: 52rpx;
}

.home-page .promo-grid {
  gap: 20rpx;
  margin: 0 28rpx 20rpx;
}

.home-page .promo-grid > view:nth-child(2) .promo-card + .promo-card {
  display: none;
}

.home-page .promo-card {
  height: 268rpx;
  min-height: 268rpx;
  padding: 20rpx;
  border-radius: 24rpx;
}

.home-page .promo-grid > view .promo-card {
  height: 268rpx;
  min-height: 268rpx;
}

.home-page .promo-card b {
  top: 20rpx;
  right: 20rpx;
  left: 20rpx;
  font-size: 32rpx;
}

.home-page .product-sort-card,
.home-page [data-sort-card="product"] {
  display: none;
}

.home-page .product-list {
  gap: 14rpx;
  margin: 0 0 calc(172rpx + env(safe-area-inset-bottom));
  padding: 0 16rpx 20rpx;
}

.home-page .shop-product {
  grid-template-columns: 196rpx minmax(0, 1fr);
  grid-template-rows: auto 52rpx;
  gap: 10rpx 20rpx;
  min-height: 264rpx;
  padding: 18rpx 112rpx 18rpx 18rpx;
  border-color: #efe1d7;
  border-radius: 24rpx;
  box-shadow: 0 10rpx 24rpx rgba(126, 76, 49, 0.09);
}

.home-page .shop-product-media {
  width: 196rpx;
  height: 196rpx;
}

.home-page .shop-product-img {
  width: 176rpx;
  height: 176rpx;
  border-radius: 16rpx;
}

.home-page .shop-product-info h4 {
  margin: 0 0 6rpx;
  font-size: 34rpx;
  line-height: 1.18;
}

.home-page .shop-product-spec {
  display: none;
}

.home-page .shop-price {
  font-size: 50rpx;
}

.home-page .shop-sale-meta {
  gap: 10rpx;
  margin-top: 10rpx;
}

.home-page .shop-product-tag-row {
  grid-template-columns: 196rpx minmax(0, 1fr);
  min-height: 48rpx;
  padding: 0 104rpx 0 0;
}

.home-page .shop-product-date,
.home-page .rank-line {
  height: 44rpx;
  min-height: 44rpx;
  padding: 0 14rpx;
  font-size: 26rpx;
}

.home-page .shop-product-date {
  max-width: 196rpx;
}

.home-page .rank-line {
  max-width: 256rpx;
}

.home-page .round-add {
  right: 24rpx;
  bottom: 80rpx;
}

/* Final UI match for design/商城项目UI设计.html .shop-* home prototype. */
.home-page.shop-page {
  width: min(100%, 390px);
  padding-bottom: calc(132rpx + env(safe-area-inset-bottom));
  background: #f3eadf;
}

.home-page .shop-header {
  min-height: 246rpx;
  padding: 28rpx 24rpx 92rpx;
  background: linear-gradient(135deg, #ed5a35 0%, #f18b3f 100%);
  border-radius: 0;
}

.home-page .status-bar {
  height: 40rpx;
  padding: 0 4rpx;
  font-size: 28rpx;
  line-height: 40rpx;
}

.home-page .shop-header-row {
  grid-template-columns: 118rpx minmax(0, 1fr) 82rpx;
  gap: 12rpx;
  min-height: 72rpx;
  margin-top: 8rpx;
}

.home-page .logo-mark small {
  font-size: 25rpx;
}

.home-page .logo-mark text {
  font-size: 38rpx;
}

.home-page .shop-search {
  height: 64rpx;
  min-height: 64rpx;
  padding: 0 22rpx;
  font-size: 26rpx;
  box-shadow: none;
}

.home-page .shop-search::before {
  font-size: 26rpx;
}

.home-page .mini-pill {
  min-width: 82rpx;
  height: 64rpx;
  min-height: 64rpx;
  padding: 0;
}

.home-page .mini-pill::before {
  content: "... ◎";
  font-size: 26rpx;
  letter-spacing: 0;
}

.home-page .station-bar {
  right: 24rpx;
  bottom: 24rpx;
  left: 24rpx;
  min-height: 70rpx;
  padding: 0 8rpx 0 24rpx;
  border-radius: 18rpx;
  box-shadow: none;
}

.home-page .station-main::before {
  content: "";
  width: 14rpx;
  height: 14rpx;
  border: 4rpx solid #172033;
  border-radius: 50%;
}

.home-page .station-main strong {
  font-size: 26rpx;
}

.home-page .station-switch {
  min-width: 124rpx;
  min-height: 56rpx;
  padding: 0 20rpx;
  background: #fff1e7 !important;
  border-color: #e48b6a;
  border-radius: 8rpx !important;
  font-size: 26rpx;
}

.home-page .cat-grid {
  gap: 18rpx 8rpx;
  margin: 16rpx 24rpx 20rpx;
  padding: 22rpx 12rpx 26rpx;
  border-radius: 12rpx;
  box-shadow: none;
}

.home-page .cat-item {
  gap: 8rpx;
  min-height: 112rpx;
}

.home-page .cat-img {
  width: 72rpx;
  height: 72rpx;
  border-radius: 14rpx;
  box-shadow: 0 6rpx 16rpx rgba(126, 76, 49, 0.1);
}

.home-page .cat-name {
  max-width: 112rpx;
  font-size: 25rpx;
}

.home-page .dot-page {
  height: 10rpx;
  margin-top: -2rpx;
  gap: 10rpx;
}

.home-page .dot-page span,
.home-page .dot-page span:first-child {
  width: 22rpx;
  height: 8rpx;
}

.home-page .hero-banner {
  height: 238rpx;
  min-height: 238rpx;
  margin: 0 24rpx 20rpx;
  border-radius: 12rpx;
  box-shadow: none;
}

.home-page .banner-text {
  max-width: 78%;
  padding: 28rpx 28rpx;
}

.home-page .banner-kicker {
  padding: 7rpx 16rpx;
  font-size: 25rpx;
}

.home-page .banner-text b {
  font-size: 42rpx;
}

.home-page .banner-text span:last-child {
  font-size: 25rpx;
}

.home-page .promo-grid {
  gap: 12rpx;
  margin: 0 24rpx 20rpx;
}

.home-page .promo-grid > view:nth-child(2) {
  display: grid;
  gap: 12rpx;
}

.home-page .promo-grid > view:nth-child(2) .promo-card + .promo-card {
  display: block;
}

.home-page .promo-card {
  height: 122rpx;
  min-height: 122rpx;
  padding: 14rpx;
  border-radius: 8rpx;
  box-shadow: none;
}

.home-page .promo-grid > .promo-card {
  height: 256rpx;
  min-height: 256rpx;
}

.home-page .promo-grid > view .promo-card {
  height: 122rpx;
  min-height: 122rpx;
}

.home-page .promo-card b {
  top: 14rpx;
  right: 14rpx;
  left: 14rpx;
  font-size: 28rpx;
}

.home-page .promo-grid > view .promo-card b {
  font-size: 26rpx;
}

.home-page .product-sort-card,
.home-page [data-sort-card="product"] {
  display: grid;
  margin: 0 24rpx 14rpx;
  padding: 16rpx 20rpx;
  border-radius: 12rpx;
  box-shadow: none;
}

.home-page .product-sort-card b {
  font-size: 28rpx;
}

.home-page .product-sort-card button {
  min-height: 42rpx;
  padding: 0 18rpx;
  font-size: 25rpx;
}

.home-page .product-list {
  gap: 12rpx;
  margin: 0;
  padding: 0 24rpx calc(150rpx + env(safe-area-inset-bottom));
}

.home-page .shop-product {
  grid-template-columns: 148rpx minmax(0, 1fr);
  grid-template-rows: auto 42rpx;
  gap: 8rpx 14rpx;
  min-height: 188rpx;
  padding: 14rpx 86rpx 14rpx 14rpx;
  border: 1rpx solid #eadbd0;
  border-radius: 8rpx;
  box-shadow: none;
}

.home-page .shop-product-media {
  width: 148rpx;
  height: 148rpx;
}

.home-page .shop-product-img {
  width: 148rpx;
  height: 148rpx;
  border-radius: 8rpx;
}

.home-page .shop-product-info h4 {
  margin-bottom: 4rpx;
  font-size: 28rpx;
  line-height: 1.22;
  -webkit-line-clamp: 2;
}

.home-page .shop-product-spec {
  display: block;
  margin-bottom: 6rpx;
  font-size: 25rpx;
}

.home-page .shop-price {
  font-size: 36rpx;
}

.home-page .shop-sale-meta {
  gap: 6rpx;
  max-width: 240rpx;
  margin-top: 6rpx;
}

.home-page .shop-sale-meta .sold-count,
.home-page .shop-sale-meta .sale-stock {
  height: auto;
  min-height: 40rpx;
  padding: 4rpx 12rpx;
  font-size: 26rpx;
  line-height: 1.15;
}

.home-page .shop-product-tag-row {
  grid-template-columns: 148rpx minmax(0, 1fr);
  min-height: 40rpx;
  padding: 0 72rpx 0 0;
}

.home-page .shop-product-date,
.home-page .rank-line {
  height: auto;
  min-height: 46rpx;
  padding: 6rpx 12rpx;
  border-radius: 8rpx;
  font-size: 26rpx;
  line-height: 1.2;
}

.home-page .shop-product-date {
  max-width: 148rpx;
}

.home-page .rank-line {
  max-width: 180rpx;
}

.home-page .round-add {
  right: 18rpx;
  bottom: 34rpx;
  width: 56rpx;
  min-width: 56rpx;
  height: 56rpx;
  min-height: 56rpx;
}

.home-page .round-add svg {
  width: 36rpx;
  height: 36rpx;
}

/* Design lock: match design/商城项目UI设计.html .shop-* user home prototype. */
.home-page.shop-page {
  width: min(100%, 390px);
  max-width: 390px;
  min-height: 100vh;
  margin: 0 auto;
  padding: 0 0 calc(116rpx + env(safe-area-inset-bottom));
  overflow-x: hidden;
  background: #f7f1ea;
}

.home-page .shop-header {
  min-height: auto;
  padding: 12rpx 24rpx 20rpx;
  background: linear-gradient(135deg, #d94b34 0%, #f28a42 100%);
  border-radius: 0;
}

.home-page .status-bar {
  display: flex;
  height: 68rpx;
  padding: 0 24rpx;
  font-size: 30rpx;
  line-height: 68rpx;
}

.home-page .shop-header-row {
  grid-template-columns: 196rpx minmax(0, 1fr) 128rpx;
  gap: 20rpx;
  min-height: 76rpx;
  margin-top: 0;
}

.home-page .logo-mark small {
  margin-bottom: 6rpx;
  font-size: 26rpx;
}

.home-page .logo-mark text {
  font-size: 40rpx;
}

.home-page .shop-search {
  height: 76rpx;
  min-height: 76rpx;
  padding: 0 24rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
}

.home-page .mini-pill {
  min-width: 128rpx;
  height: 68rpx;
  min-height: 68rpx;
  padding: 0;
  background: rgba(38, 48, 67, 0.22);
  border-radius: 36rpx;
  font-size: 28rpx;
}

.home-page .mini-pill::before {
  content: none;
}

.home-page .station-bar {
  position: static;
  min-height: 104rpx;
  margin-top: 20rpx;
  padding: 16rpx 16rpx 16rpx 28rpx;
  border-radius: 32rpx;
  box-shadow: 0 6rpx 0 rgba(0, 0, 0, 0.04);
}

.home-page .station-main {
  display: grid;
  gap: 6rpx;
}

.home-page .station-main::before {
  display: none;
}

.home-page .station-main strong {
  font-size: 28rpx;
  line-height: 1.2;
}

.home-page .station-main em {
  display: block;
  color: #8b6a57;
  font-size: 25rpx;
  font-style: normal;
  font-weight: 800;
}

.home-page .station-meta-row {
  display: none;
}

.home-page .station-switch {
  min-width: auto;
  min-height: 64rpx;
  padding: 0 24rpx;
  color: #c2412d !important;
  background: #ffffff !important;
  border: 1rpx solid #e85d3f;
  border-radius: 34rpx !important;
  font-size: 26rpx;
}

.home-page .cat-grid {
  gap: 24rpx 12rpx;
  margin: 20rpx 24rpx;
  padding: 28rpx 16rpx 24rpx;
  border-radius: 28rpx;
  box-shadow: none;
}

.home-page .cat-item {
  gap: 12rpx;
  min-height: auto;
  font-size: 25rpx;
}

.home-page .cat-img {
  width: 96rpx;
  height: 96rpx;
  border-radius: 28rpx;
}

.home-page .cat-name {
  max-width: 100%;
  font-size: 25rpx;
  white-space: nowrap;
}

.home-page .dot-page {
  height: 14rpx;
  margin-top: 0;
  gap: 6rpx;
}

.home-page .dot-page span,
.home-page .dot-page span:first-child {
  width: 14rpx;
  height: 14rpx;
}

.home-page .dot-page span:first-child {
  width: 40rpx;
}

.home-page .hero-banner {
  height: 308rpx;
  min-height: 308rpx;
  margin: 20rpx 24rpx;
  border-radius: 28rpx;
  box-shadow: 0 20rpx 44rpx rgba(38, 48, 67, 0.16);
}

.home-page .banner-text {
  max-width: 78%;
  padding: 0;
  top: 36rpx;
  left: 32rpx;
}

.home-page .banner-kicker {
  padding: 6rpx 16rpx;
  font-size: 26rpx;
}

.home-page .banner-text b {
  font-size: 52rpx;
}

.home-page .banner-text span:last-child {
  font-size: 26rpx;
}

.home-page .promo-grid {
  gap: 20rpx;
  margin: 20rpx 24rpx;
}

.home-page .promo-grid > view {
  display: grid;
  gap: 20rpx;
}

.home-page .promo-card,
.home-page .promo-grid > .promo-card {
  min-height: 280rpx;
  height: auto;
  padding: 16rpx;
  border-radius: 20rpx;
}

.home-page .promo-grid > view .promo-card {
  min-height: 130rpx;
  height: auto;
}

.home-page .promo-grid > view:nth-child(2) .promo-card + .promo-card {
  display: block;
}

.home-page .promo-gap {
  margin-top: 0;
}

.home-page .promo-card b {
  position: relative;
  top: auto;
  right: auto;
  left: auto;
  margin-bottom: 12rpx;
  font-size: 34rpx;
}

.home-page .promo-grid > view .promo-card b {
  font-size: 26rpx;
}

.home-page .promo-img {
  position: static;
  width: 100%;
  height: 180rpx;
  margin: 0;
  border-radius: 16rpx;
}

.home-page .promo-grid > view .promo-img {
  position: absolute;
  inset: 0;
  height: 100%;
  border-radius: inherit;
}

.home-page .product-sort-card,
.home-page [data-sort-card="product"] {
  display: grid;
  margin: 16rpx 24rpx 14rpx;
  padding: 16rpx 20rpx;
  border-radius: 18rpx;
}

.home-page .product-list {
  gap: 16rpx;
  margin: 8rpx 20rpx 0;
  padding: 0 0 calc(120rpx + env(safe-area-inset-bottom));
}

.home-page .shop-product {
  grid-template-columns: 220rpx minmax(0, 1fr);
  grid-template-rows: auto auto;
  gap: 10rpx 20rpx;
  min-height: 252rpx;
  padding: 18rpx 104rpx 20rpx 18rpx;
  border: 1rpx solid #edf1ed;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(23, 36, 32, 0.05);
}

.home-page .shop-product-media {
  width: 220rpx;
  height: auto;
}

.home-page .shop-product-img {
  width: 192rpx;
  height: 192rpx;
  border-radius: 16rpx;
}

.home-page .product-fav-btn {
  display: inline-flex;
}

.home-page .shop-product-info h4 {
  margin: 0 0 6rpx;
  font-size: 28rpx;
  line-height: 1.28;
}

.home-page .shop-product-spec {
  display: block;
  margin-bottom: 10rpx;
  font-size: 25rpx;
}

.home-page .shop-price {
  font-size: 40rpx;
}

.home-page .old-price {
  display: inline;
}

.home-page .shop-sale-meta {
  display: grid;
  grid-template-columns: repeat(2, max-content);
  gap: 6rpx 8rpx;
  max-width: 100%;
  margin-top: 8rpx;
}

.home-page .shop-sale-meta .sold-count,
.home-page .shop-sale-meta .sale-stock {
  height: auto;
  min-height: 32rpx;
  padding: 2rpx 8rpx;
  font-size: 25rpx;
}

.home-page .shop-product-tag-row {
  grid-column: 1 / -1;
  grid-template-columns: none;
  display: flex;
  gap: 20rpx;
  min-height: 44rpx;
  margin-top: -4rpx;
  padding: 0 72rpx 0 0;
}

.home-page .shop-product-date,
.home-page .rank-line {
  height: auto;
  min-height: 44rpx;
  max-width: none;
  padding: 0 16rpx;
  border-radius: 14rpx;
  font-size: 25rpx;
}

.home-page .round-add {
  right: 20rpx;
  bottom: 24rpx;
  width: 68rpx;
  min-width: 68rpx;
  height: 68rpx;
  min-height: 68rpx;
}

.home-page .round-add svg {
  width: 44rpx;
  height: 44rpx;
}

/* Final design lock: keep the runtime home page aligned with the .shop-* prototype. */
.home-page.shop-page {
  padding-bottom: calc(92rpx + env(safe-area-inset-bottom));
  background: #f6efe7;
}

.home-page .shop-header {
  position: relative;
  min-height: 300rpx;
  padding: 36rpx 20rpx 20rpx;
  overflow: visible;
  background: linear-gradient(135deg, #d94b34 0%, #f28a42 100%);
  border-radius: 0 0 8rpx 8rpx;
  box-shadow: none;
}

.home-page .status-bar {
  height: 48rpx;
  padding: 0 64rpx;
  color: #ffffff;
  font-size: 36rpx;
  font-weight: 950;
  line-height: 48rpx;
}

.home-page .shop-header-row {
  width: 100%;
  min-height: 88rpx;
  margin-top: 8rpx;
  display: grid;
  grid-template-columns: 208rpx minmax(0, 1fr) 128rpx;
  gap: 16rpx;
  align-items: center;
}

.home-page .logo-mark {
  min-width: 0;
  display: grid;
  gap: 2rpx;
  color: #ffffff;
  line-height: 1.02;
  white-space: nowrap;
}

.home-page .logo-mark small {
  display: block;
  margin: 0;
  color: #ffffff;
  font-size: 30rpx;
  font-weight: 950;
  line-height: 1;
  opacity: 1;
}

.home-page .logo-mark text {
  color: #ffffff;
  font-size: 46rpx;
  font-weight: 950;
  line-height: 1.02;
  letter-spacing: 0;
}

.home-page .shop-search {
  width: 100%;
  min-width: 0;
  height: 80rpx;
  min-height: 80rpx;
  padding: 0 30rpx;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 14rpx;
  color: #59483d;
  background: #ffffff;
  border: 0;
  border-radius: 999rpx;
  box-shadow: 0 16rpx 36rpx rgba(111, 52, 20, 0.12);
  font-size: 28rpx;
  font-weight: 850;
  line-height: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.home-page .shop-search::before {
  content: "⌕";
  flex: 0 0 auto;
  color: #172033;
  font-size: 32rpx;
  line-height: 1;
}

.home-page .mini-pill {
  width: 128rpx;
  min-width: 128rpx;
  height: 80rpx;
  min-height: 80rpx;
  padding: 0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  background: rgba(127, 78, 40, 0.45);
  border: 0;
  border-radius: 999rpx;
  box-shadow: none;
  font-size: 0;
}

.home-page .mini-pill::before {
  content: "...  ⊙";
  color: #ffffff;
  font-size: 36rpx;
  font-weight: 950;
  line-height: 1;
}

.home-page .station-bar {
  position: relative;
  left: auto;
  right: auto;
  bottom: auto;
  width: 100%;
  min-height: 84rpx;
  margin: 18rpx 0 0;
  padding: 0 10rpx 0 32rpx;
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: center;
  column-gap: 16rpx;
  color: #172033;
  background: #ffffff;
  border: 0;
  border-radius: 999rpx;
  box-shadow: 0 16rpx 36rpx rgba(111, 52, 20, 0.13);
}

.home-page .station-main {
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 14rpx;
}

.home-page .station-main::before {
  content: "⌖";
  display: inline-block;
  flex: 0 0 auto;
  color: #e85d3f;
  font-size: 30rpx;
  line-height: 1;
}

.home-page .station-main strong {
  min-width: 0;
  display: block;
  color: #172033;
  font-size: 32rpx;
  font-weight: 950;
  line-height: 1.1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.home-page .station-main em,
.home-page .station-meta-row,
.home-page .station-meta-pill {
  display: none;
}

.home-page .station-switch {
  position: relative;
  min-width: 176rpx;
  height: 64rpx;
  min-height: 64rpx;
  padding: 0 24rpx;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #d94b34 !important;
  background: #fff7f1 !important;
  border: 1rpx solid #ef6a45;
  border-radius: 999rpx !important;
  font-size: 0;
  font-weight: 950;
  line-height: 1;
  white-space: nowrap;
}

.home-page .station-switch::before {
  content: "切换自提点";
  font-size: 30rpx;
}

.home-page .station-switch::after {
  content: none;
}

.home-page .station-alert {
  margin: 16rpx 28rpx 0;
}

.home-page .cat-grid {
  margin: 20rpx 28rpx 24rpx;
  padding: 24rpx 16rpx 24rpx;
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 18rpx 12rpx;
  background: #ffffff;
  border: 0;
  border-radius: 28rpx;
  box-shadow: 0 16rpx 36rpx rgba(126, 76, 49, 0.08);
}

.home-page .cat-item {
  min-height: 118rpx;
  padding: 0;
  display: grid;
  justify-items: center;
  align-content: start;
  gap: 10rpx;
  background: transparent;
  border: 0;
  box-shadow: none;
}

.home-page .cat-img {
  width: 96rpx;
  height: 96rpx;
  border-radius: 24rpx;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.1);
}

.home-page .cat-name {
  max-width: 132rpx;
  color: #172033;
  font-size: 26rpx;
  font-weight: 950;
  line-height: 1.12;
  text-align: center;
  white-space: normal;
}

.home-page .cat-grid .dot-page {
  grid-column: 1 / -1;
  height: 12rpx;
  margin-top: 0;
  display: flex;
  justify-content: center;
  gap: 16rpx;
}

.home-page .cat-grid .dot-page span {
  width: 36rpx;
  height: 12rpx;
  background: #d2d2d2;
  border-radius: 999rpx;
}

.home-page .cat-grid .dot-page span:first-child {
  width: 36rpx;
  height: 12rpx;
  background: #e76545;
}

.home-page .hero-banner {
  height: 308rpx;
  min-height: 308rpx;
  margin: 0 28rpx 24rpx;
  overflow: hidden;
  background-position: center;
  background-size: cover;
  border: 0;
  border-radius: 28rpx;
  box-shadow: 0 16rpx 36rpx rgba(126, 76, 49, 0.1);
}

.home-page .banner-text {
  max-width: 78%;
  padding: 46rpx 36rpx;
  top: auto;
  left: auto;
}

.home-page .banner-kicker {
  width: fit-content;
  padding: 10rpx 20rpx;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.18);
  border-radius: 999rpx;
  font-size: 30rpx;
  font-weight: 950;
  line-height: 1;
}

.home-page .banner-text b {
  margin-top: 16rpx;
  color: #ffffff;
  font-size: 56rpx;
  font-weight: 950;
  line-height: 1.08;
  letter-spacing: 0;
}

.home-page .banner-text span:not(.banner-kicker) {
  color: #ffffff;
  font-size: 30rpx;
  font-weight: 850;
  line-height: 1.25;
}

.home-page .promo-grid {
  margin: 0 28rpx 20rpx;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20rpx;
}

.home-page .promo-grid.single-promo {
  grid-template-columns: minmax(0, 1fr);
}

.home-page .promo-grid > view {
  min-width: 0;
  display: grid;
  gap: 20rpx;
}

.home-page .promo-grid > view:nth-child(2) .promo-card + .promo-card {
  display: block;
}

.home-page .promo-card,
.home-page .promo-grid > .promo-card,
.home-page .promo-grid > view .promo-card {
  position: relative;
  min-height: 284rpx;
  height: 284rpx;
  padding: 0;
  display: block;
  overflow: hidden;
  background: #ffffff;
  border: 0;
  border-radius: 24rpx;
  box-shadow: 0 16rpx 36rpx rgba(126, 76, 49, 0.08);
}

.home-page .promo-grid > view .promo-card {
  min-height: 132rpx;
  height: 132rpx;
}

.home-page .promo-card b,
.home-page .promo-grid > view .promo-card b {
  position: absolute;
  z-index: 2;
  left: 20rpx;
  right: 20rpx;
  top: 20rpx;
  min-height: 48rpx;
  margin: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #172033;
  font-size: 36rpx;
  font-weight: 950;
  line-height: 1.1;
  text-shadow: 0 2rpx 0 rgba(255, 255, 255, 0.55);
}

.home-page .promo-grid > view .promo-card b {
  font-size: 26rpx;
}

.home-page .promo-card b span {
  color: #df583a;
  font-size: 28rpx;
  font-weight: 950;
  text-shadow: none;
}

.home-page .promo-img,
.home-page .promo-grid > view .promo-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  min-height: 0;
  display: block;
  background-position: center;
  background-size: cover;
  border-radius: inherit;
}

.home-page .promo-card::after {
  content: "";
  position: absolute;
  inset: 0;
  z-index: 1;
  pointer-events: none;
  background: linear-gradient(180deg, rgba(255, 250, 245, 0.78), rgba(255, 250, 245, 0.04) 48%, rgba(0, 0, 0, 0.08));
}

.home-page .product-sort-card,
.home-page [data-sort-card="product"] {
  display: none;
}

.home-page .product-list {
  margin: 0 0 calc(92rpx + env(safe-area-inset-bottom));
  padding: 0 10rpx 20rpx;
  display: grid;
  gap: 14rpx;
}

.home-page .shop-product {
  position: relative;
  min-height: 320rpx;
  margin: 0;
  padding: 18rpx 112rpx 18rpx 18rpx;
  display: grid;
  grid-template-columns: 208rpx minmax(0, 1fr);
  grid-template-rows: auto auto;
  column-gap: 24rpx;
  row-gap: 12rpx;
  align-items: start;
  grid-auto-columns: 0;
  overflow: hidden;
  background: #ffffff;
  border: 1rpx solid rgba(126, 76, 49, 0.06);
  border-radius: 10rpx;
  box-shadow: 0 4rpx 14rpx rgba(126, 76, 49, 0.06);
}

.home-page .product-fav-btn {
  display: none;
}

.home-page .shop-product-media {
  position: relative;
  width: 180rpx;
  height: 180rpx;
  display: block;
  grid-column: 1 / 2;
  grid-row: 1 / 2;
  overflow: visible;
  border-radius: 10rpx;
}

.home-page .shop-product-img,
.home-page .shop-product-media > .shop-product-img {
  width: 180rpx;
  height: 180rpx;
  min-width: 0;
  min-height: 180rpx;
  display: block;
  background-position: center;
  background-size: cover;
  border-radius: 10rpx;
  box-shadow: none;
}

.home-page .shop-product-info {
  grid-column: 2 / 3;
  grid-row: 1 / 2;
  min-width: 0;
  padding: 0;
}

.home-page .shop-product-info h4 {
  min-height: 0;
  margin: 0 0 8rpx;
  color: #071a36;
  display: -webkit-box;
  overflow: hidden;
  font-size: 34rpx;
  font-weight: 950;
  line-height: 1.18;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.home-page .shop-product-spec {
  display: none;
}

.home-page .price-row {
  min-width: 0;
  margin: 2rpx 0 0;
  display: flex;
  align-items: baseline;
  flex-wrap: nowrap;
  gap: 14rpx;
}

.home-page .shop-product .shop-price {
  color: #f00010;
  font-size: 56rpx;
  font-weight: 950;
  line-height: 1;
}

.home-page .shop-product .old-price {
  display: inline-block;
  max-width: 150rpx;
  color: #9f9f9f;
  overflow: hidden;
  font-size: 26rpx;
  font-weight: 800;
  line-height: 1;
  text-decoration: line-through;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.home-page .shop-sale-meta {
  margin-top: 12rpx;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12rpx;
  overflow: visible;
}

.home-page .shop-sale-meta .sold-count,
.home-page .shop-sale-meta .sale-stock {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: auto;
  min-height: 48rpx;
  padding: 8rpx 16rpx;
  color: #b84a19;
  background: #fff1e8;
  border: 0;
  border-radius: 999rpx;
  font-size: 30rpx;
  font-weight: 950;
  line-height: 1;
}

.home-page .shop-sale-meta .sale-stock b {
  display: inline;
  min-height: 0;
  padding: 0;
  color: inherit;
  background: transparent;
  border: 0;
  font-size: inherit;
  line-height: inherit;
}

.home-page .shop-product-tag-row {
  grid-column: 1 / 3;
  min-height: auto;
  margin: 4rpx 0 0;
  padding: 0 112rpx 0 0;
  display: grid;
  grid-template-columns: 180rpx minmax(0, 1fr);
  align-items: center;
  gap: 24rpx;
}

.home-page .rank-line {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 50rpx;
  height: auto;
  max-width: none;
  justify-self: start;
  padding: 7rpx 16rpx;
  border: 0;
  border-radius: 10rpx;
  font-size: 26rpx;
  font-weight: 950;
  line-height: 1.2;
  white-space: nowrap;
}

.home-page .shop-product-date {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 50rpx;
  max-width: 208rpx;
  min-width: 208rpx;
  margin-top: 8rpx;
  padding: 7rpx 12rpx;
  color: #a66210;
  background: #fff1d8;
  border: 0;
  border-radius: 10rpx;
  font-size: 26rpx;
  font-weight: 950;
  line-height: 1.2;
  white-space: nowrap;
}

.home-page .rank-line {
  color: #1e71ad;
  background: #eaf4ff;
}

.home-page .shop-product > .round-add {
  position: absolute;
  right: 24rpx;
  top: 50%;
  bottom: auto;
  transform: translateY(-50%);
  width: 96rpx;
  min-width: 96rpx;
  height: 96rpx;
  min-height: 96rpx;
  padding: 0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  background: linear-gradient(135deg, #e95b43, #da4d35);
  border: 0;
  border-radius: 50%;
  box-shadow: 0 14rpx 30rpx rgba(217, 75, 52, 0.24);
  font-size: 0;
}

.home-page .round-add svg {
  width: 54rpx;
  height: 54rpx;
}

.home-page .shop-product.soldout {
  opacity: 0.72;
  background: #fbf7f3;
}

.home-page .shop-product.soldout .shop-product-img {
  filter: grayscale(1) saturate(0.45) brightness(0.92);
}

.home-page .shop-product.soldout .shop-product-media::after {
  content: "已售罄";
  position: absolute;
  left: 50%;
  top: 50%;
  z-index: 2;
  min-width: 116rpx;
  padding: 10rpx 16rpx;
  transform: translate(-50%, -50%);
  color: #ffffff;
  background: rgba(121, 76, 69, 0.88);
  border-radius: 999rpx;
  font-size: 28rpx;
  font-weight: 950;
  line-height: 1;
  text-align: center;
}

.home-page .shop-product.soldout > .round-add {
  background: #c8c0ba;
  box-shadow: none;
}
</style>
